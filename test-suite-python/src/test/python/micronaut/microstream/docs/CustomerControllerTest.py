import uuid

import java
from micronaut.context import ApplicationContext
from micronaut.http import HttpRequest, HttpStatus
from micronaut.http.client import BlockingHttpClient
from micronaut.http.client.exceptions import HttpClientResponseException
from micronaut.runtime.server import EmbeddedServer
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Disabled, Test

from .Customer import Customer
from .CustomerSave import CustomerSave

# TODO(python): the imported shim classes cannot be used as runtime type arguments of ApplicationContext.run /
# createBean / exchange ("TypeError: invalid instantiation of foreign object"), only java.type(...) aliases can
EmbeddedServerType = java.type("io.micronaut.runtime.server.EmbeddedServer")
HttpClientType = java.type("io.micronaut.http.client.HttpClient")
CustomerType = java.type("micronaut.microstream.docs.Customer")


@MicronautTest
class CustomerControllerTest:

    # TODO(python): MicroStream cannot persist the classes generated for Python classes (observed with the identical
    # EclipseStore port): storing the root instance fails with
    # "PersistenceExceptionTypeNotPersistable: Type not persistable: class com.oracle.graal.python.builtins.objects.cext.capi.
    # transitions.CApiTransitions$PythonObjectReference" (the generated class holds the GraalPy object reference) and a
    # @dataclass root has no no-arg constructor for the initial InstantiationUtils.instantiate(rootClass) call
    @Disabled("TODO(python): MicroStream cannot persist the classes generated for Python classes (see DISABLED_TESTS.md)")
    @Test
    def test_crud(self) -> None:
        for customer_repository_implementation in ["store", "embedded-storage-manager"]:
            self.verify_crud_with_micro_stream(customer_repository_implementation)

    def verify_crud_with_micro_stream(self, customer_repository_implementation: str) -> None:
        # Given
        properties = {
            "customer.repository": customer_repository_implementation,
            "microstream.storage.main.root-class": "micronaut.microstream.docs.Data",
            "microstream.storage.main.storage-directory": "build/microstream-" + str(uuid.uuid4()),
        }
        server = self.start_server(properties)
        client = self.create_client(server)

        # And
        sergio_first_name = "Sergio"
        sergio_last_name = "del Amo"
        tim_first_name = "Tim"

        # When we create Sergio and Tim
        sergio_location = self.create(client, sergio_first_name)
        tim_location = self.create(client, tim_first_name)

        # When we retrieve Sergio
        customer = self.show(client, sergio_location)

        # Then
        assert customer.firstName == sergio_first_name
        assert customer.lastName is None

        # When we restart the server
        client.close()
        server.close()
        server = self.start_server(properties)
        client = self.create_client(server)

        # And we re-retrieve Sergio, then he still exists
        customer = self.show(client, sergio_location)
        assert customer.firstName == sergio_first_name
        assert customer.lastName is None

        # When
        patch_response = client.exchange(HttpRequest.PATCH(sergio_location, CustomerSave(customer.firstName, sergio_last_name)))

        # Then
        assert patch_response.status() == HttpStatus.OK
        assert patch_response.getHeaders().get("Location") == sergio_location

        # When we restart the server
        client.close()
        server.close()
        server = self.start_server(properties)
        client = self.create_client(server)

        # And we re-retrieve Sergio, then he still exists and his last name is updated
        customer = self.show(client, sergio_location)
        assert customer.firstName == sergio_first_name
        assert customer.lastName == sergio_last_name

        # When we delete Sergio
        self.delete(client, sergio_location)

        # When we restart the server
        client.close()
        server.close()
        server = self.start_server(properties)
        client = self.create_client(server)

        # Then Sergio remains gone
        try:
            client.exchange(HttpRequest.GET(sergio_location), CustomerType)
        except HttpClientResponseException as e:
            assert e.getStatus() == HttpStatus.NOT_FOUND
        else:
            assert False, "Sergio should be gone"

        # But when we get Tim, then he still exists
        customer = self.show(client, tim_location)
        assert customer.firstName == tim_first_name
        assert customer.lastName is None

        self.delete(client, tim_location)

        client.close()
        server.close()

    @staticmethod
    def start_server(properties: dict[str, object]) -> EmbeddedServer:
        return ApplicationContext.run(EmbeddedServerType, properties)

    @staticmethod
    def create_client(server: EmbeddedServer) -> BlockingHttpClient:
        return server.getApplicationContext().createBean(HttpClientType, server.getURL()).toBlocking()

    @staticmethod
    def create(client: BlockingHttpClient, first_name: str) -> str:
        response = client.exchange(HttpRequest.POST("/customer", {"firstName": first_name}))
        assert response.status() == HttpStatus.CREATED
        location = response.getHeaders().get("Location")
        assert location is not None
        return location

    @staticmethod
    def show(client: BlockingHttpClient, location: str) -> Customer:
        response = client.exchange(HttpRequest.GET(location), CustomerType)
        assert response.status() == HttpStatus.OK
        customer = response.body()
        assert customer is not None
        return customer.asPolyglotValue()

    @staticmethod
    def delete(client: BlockingHttpClient, location: str) -> None:
        delete_response = client.exchange(HttpRequest.DELETE(location), CustomerType)
        assert delete_response.status() == HttpStatus.NO_CONTENT
        try:
            client.exchange(HttpRequest.GET(location), CustomerType)
        except HttpClientResponseException as e:
            assert e.getStatus() == HttpStatus.NOT_FOUND
        else:
            assert False, "customer should be gone"
