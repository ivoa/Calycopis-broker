# coding: utf-8

"""
    IVOA ExecutionBroker

    Prototype implementation of the IVOA ExecutionBroker interface 

    The version of the OpenAPI document: 0.5
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json




from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field, StrictStr, field_validator
from typing import Any, ClassVar, Dict, List, Optional
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class OfferStatus(BaseModel):
    """
    The status block for an offer. 
    """ # noqa: E501
    status: Optional[StrictStr] = Field(default=None, description="Setting the status of an offer to `REJECTED` will reject the offer. Setting the status of an offer to `ACCEPTED` will start the execution process and the status of any sibling offers will automatically be set to `REJECTED`. ")
    expires: Optional[datetime] = Field(default=None, description="The date and time that this offer expires. ")
    __properties: ClassVar[List[str]] = ["status", "expires"]

    @field_validator('status')
    def status_validate_enum(cls, value):
        """Validates the enum"""
        if value is None:
            return value

        if value not in ('OFFERED', 'ACCEPTED', 'REJECTED', 'EXPIRED'):
            raise ValueError("must be one of enum values ('OFFERED', 'ACCEPTED', 'REJECTED', 'EXPIRED')")
        return value

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }


    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        # TODO: pydantic v2: use .model_dump_json(by_alias=True, exclude_unset=True) instead
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        """Create an instance of OfferStatus from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        """Return the dictionary representation of the model using alias.

        This has the following differences from calling pydantic's
        `self.model_dump(by_alias=True)`:

        * `None` is only added to the output dict for nullable fields that
          were set at model initialization. Other fields with value `None`
          are ignored.
        """
        _dict = self.model_dump(
            by_alias=True,
            exclude={
            },
            exclude_none=True,
        )
        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of OfferStatus from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "status": obj.get("status"),
            "expires": obj.get("expires")
        })
        return _obj

