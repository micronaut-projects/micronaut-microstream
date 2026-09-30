from dataclasses import dataclass
from typing import Annotated

from jakarta.validation.constraints import NotBlank
from micronaut.serde.annotation import Serdeable


@Serdeable  # <1>
@dataclass
class Customer:
    id: Annotated[str, NotBlank]
    firstName: Annotated[str, NotBlank]
    lastName: str | None = None
