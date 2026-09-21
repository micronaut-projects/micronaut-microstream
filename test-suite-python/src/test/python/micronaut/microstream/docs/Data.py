from dataclasses import dataclass, field

from micronaut.core.annotation import Introspected

from .Customer import Customer


@Introspected
@dataclass
class Data:
    customers: dict[str, Customer] = field(default_factory=dict)
