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




from importlib import import_module
from pydantic import ConfigDict, Field, StrictStr
from typing import Any, ClassVar, Dict, List, Optional, Union
from openapi_server.models.abstract_polymorph import AbstractPolymorph
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class AbstractStorageResource(AbstractPolymorph):
    """
    Abstract base class for storage resources. 
    """ # noqa: E501
    ident: Optional[StrictStr] = None
    name: Optional[StrictStr] = None
    properties: Optional[Dict[str, StrictStr]] = Field(default=None, description="A map of name->value properties. See https://swagger.io/docs/specification/data-models/dictionaries/ ")
    type: StrictStr
    __properties: ClassVar[List[str]] = ["ident", "name", "properties", "type"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }


    # JSON field name that stores the object type
    __discriminator_property_name: ClassVar[List[str]] = 'type'

    # discriminator mappings
    __discriminator_value_class_map: ClassVar[Dict[str, str]] = {
        'urn:simple-storage-resource': 'SimpleStorageResource'
    }

    @classmethod
    def get_discriminator_value(cls, obj: Dict) -> str:
        """Returns the discriminator value (object type) of the data"""
        discriminator_value = obj[cls.__discriminator_property_name]
        if discriminator_value:
            return cls.__discriminator_value_class_map.get(discriminator_value)
        else:
            return None

    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        # TODO: pydantic v2: use .model_dump_json(by_alias=True, exclude_unset=True) instead
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Union[Self]:
        """Create an instance of AbstractStorageResource from a JSON string"""
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
    def from_dict(cls, obj: Dict) -> Union[Self]:
        """Create an instance of AbstractStorageResource from a dict"""
        # look up the object type based on discriminator mapping
        object_type = cls.get_discriminator_value(obj)
        if object_type:
            klass = globals()[object_type]
            return klass.from_dict(obj)
        else:
            raise ValueError("AbstractStorageResource failed to lookup discriminator value from " +
                             json.dumps(obj) + ". Discriminator property name: " + cls.__discriminator_property_name +
                             ", mapping: " + json.dumps(cls.__discriminator_value_class_map))

