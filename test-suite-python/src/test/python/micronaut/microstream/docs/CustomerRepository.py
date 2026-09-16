from typing import Annotated

from jakarta.validation import Valid
from jakarta.validation.constraints import NotBlank, NotNull

from .Customer import Customer
from .CustomerSave import CustomerSave


class CustomerRepository:

    def save(self, customer_save: Annotated[CustomerSave, NotNull, Valid]) -> Customer:
        ...

    def update(self, id: Annotated[str, NotBlank],
               customer_save: Annotated[CustomerSave, NotNull, Valid]) -> None:
        ...

    def find_by_id(self, id: Annotated[str, NotBlank]) -> Customer | None:
        ...

    def delete_by_id(self, id: Annotated[str, NotBlank]) -> None:
        ...
