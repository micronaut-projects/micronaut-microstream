import uuid
from typing import Annotated

from jakarta.inject import Singleton
from jakarta.validation import Valid
from jakarta.validation.constraints import NotBlank, NotNull
from micronaut.context.annotation import Requires
from micronaut.microstream import RootProvider
from micronaut.microstream.annotations import StoreParams, StoreReturn

from .Customer import Customer
from .CustomerRepository import CustomerRepository
from .CustomerSave import CustomerSave
from .Data import Data


@Requires(property="customer.repository", value="store")
# tag::clazz[]
@Singleton
class CustomerRepositoryStoreImpl(CustomerRepository):

    def __init__(self, root_provider: RootProvider[Data]):  # <1>
        self.root_provider = root_provider

    def save(self, customer_save: Annotated[CustomerSave, NotNull, Valid]) -> Customer:
        return self.add_customer(self.root_provider.root().customers, customer_save)

    def update(self, id: Annotated[str, NotBlank],
               customer_save: Annotated[CustomerSave, NotNull, Valid]) -> None:
        self.update_customer(id, customer_save)

    def find_by_id(self, id: Annotated[str, NotBlank]) -> Customer | None:
        return self.root_provider.root().customers.get(id)

    def delete_by_id(self, id: Annotated[str, NotBlank]) -> None:
        self.remove_customer(self.root_provider.root().customers, id)

    @StoreReturn  # <2>
    def update_customer(self, id: str, customer_save: CustomerSave) -> Customer | None:
        c = self.root_provider.root().customers.get(id)
        if c is not None:
            c.firstName = customer_save.firstName
            c.lastName = customer_save.lastName
            return c
        return None

    @StoreParams("customers")  # <3>
    def add_customer(self, customers: dict[str, Customer], customer_save: CustomerSave) -> Customer:
        customer = Customer(str(uuid.uuid4()), customer_save.firstName, customer_save.lastName)
        customers[customer.id] = customer
        return customer

    @StoreParams("customers")  # <3>
    def remove_customer(self, customers: dict[str, Customer], id: str) -> None:
        customers.pop(id, None)
# end::clazz[]
