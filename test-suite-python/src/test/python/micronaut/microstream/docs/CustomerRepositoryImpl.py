import uuid
from typing import Annotated

from jakarta.inject import Singleton
from jakarta.validation import Valid
from jakarta.validation.constraints import NotBlank, NotNull
from micronaut.context.annotation import Requires
from one.microstream.concurrency import XThreads
from one.microstream.storage.types import StorageManager

from .Customer import Customer
from .CustomerRepository import CustomerRepository
from .CustomerSave import CustomerSave
from .Data import Data


@Requires(property="customer.repository", value="embedded-storage-manager")
# tag::clazz[]
@Singleton
class CustomerRepositoryImpl(CustomerRepository):

    def __init__(self, storage_manager: StorageManager):  # <1>
        self.storage_manager = storage_manager

    def save(self, customer_save: Annotated[CustomerSave, NotNull, Valid]) -> Customer:
        def store() -> Customer:
            id = str(uuid.uuid4())
            customer = Customer(id, customer_save.firstName, customer_save.lastName)
            self.data().customers[id] = customer
            self.storage_manager.store(self.data().customers)  # <3>
            return customer
        return XThreads.executeSynchronized(store)  # <2>

    def update(self, id: Annotated[str, NotBlank],
               customer_save: Annotated[CustomerSave, NotNull, Valid]) -> None:
        def store() -> None:
            c = self.data().customers[id]
            c.firstName = customer_save.firstName
            c.lastName = customer_save.lastName
            self.storage_manager.store(c)  # <3>
        XThreads.executeSynchronized(store)  # <2>

    def find_by_id(self, id: Annotated[str, NotBlank]) -> Customer | None:
        return self.data().customers.get(id)

    def delete_by_id(self, id: Annotated[str, NotBlank]) -> None:
        def store() -> None:
            self.data().customers.pop(id, None)
            self.storage_manager.store(self.data().customers)  # <3>
        XThreads.executeSynchronized(store)  # <2>

    def data(self) -> Data:
        return self.storage_manager.root()
# end::clazz[]
