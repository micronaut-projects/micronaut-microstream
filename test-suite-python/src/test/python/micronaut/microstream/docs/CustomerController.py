from typing import Annotated

from jakarta.validation import Valid
from jakarta.validation.constraints import NotNull
from micronaut.http import HttpResponse, HttpStatus
from micronaut.http.annotation import Body, Controller, Delete, Get, Patch, PathVariable, Post, Status
from micronaut.http.uri import UriBuilder
from micronaut.scheduling import TaskExecutors
from micronaut.scheduling.annotation import ExecuteOn

from .Customer import Customer
from .CustomerRepository import CustomerRepository
from .CustomerSave import CustomerSave


@Controller("/customer")
@ExecuteOn(TaskExecutors.BLOCKING)
class CustomerController:

    def __init__(self, repository: CustomerRepository):
        self.repository = repository

    @Post
    def save(self, customer_save: Annotated[CustomerSave, NotNull, Valid, Body]) -> HttpResponse:
        customer = self.repository.save(customer_save)
        return HttpResponse.created(UriBuilder.of("/customer").path(customer.id).build())

    @Patch("/{id}")
    def update(self, id: Annotated[str, PathVariable],
               customer: Annotated[CustomerSave, NotNull, Valid, Body]) -> HttpResponse:
        self.repository.update(id, customer)
        return HttpResponse.ok().header("Location", UriBuilder.of("/customer").path(id).build().toString())

    @Get("/{id}")
    def show(self, id: Annotated[str, PathVariable]) -> Customer | None:
        return self.repository.find_by_id(id)

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    def delete(self, id: Annotated[str, PathVariable]) -> None:
        self.repository.delete_by_id(id)
