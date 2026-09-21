from dataclasses import dataclass
from typing import Annotated

from jakarta.validation.constraints import NotBlank
from micronaut.serde.annotation import Serdeable


@Serdeable
@dataclass
class CustomerSave:
    firstName: Annotated[str, NotBlank]
    lastName: str | None = None
