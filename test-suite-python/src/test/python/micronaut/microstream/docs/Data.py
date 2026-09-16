from dataclasses import dataclass, field

from .Customer import Customer


@dataclass
class Data:
    customers: dict[str, Customer] = field(default_factory=dict)
