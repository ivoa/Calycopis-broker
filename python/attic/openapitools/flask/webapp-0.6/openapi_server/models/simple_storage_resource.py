from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.abstract_storage_resource import AbstractStorageResource
from openapi_server.models.min_max_integer import MinMaxInteger
from openapi_server import util

from openapi_server.models.abstract_storage_resource import AbstractStorageResource  # noqa: E501
from openapi_server.models.min_max_integer import MinMaxInteger  # noqa: E501

class SimpleStorageResource(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, ident=None, name=None, properties=None, type=None, size=None):  # noqa: E501
        """SimpleStorageResource - a model defined in OpenAPI

        :param ident: The ident of this SimpleStorageResource.  # noqa: E501
        :type ident: str
        :param name: The name of this SimpleStorageResource.  # noqa: E501
        :type name: str
        :param properties: The properties of this SimpleStorageResource.  # noqa: E501
        :type properties: Dict[str, str]
        :param type: The type of this SimpleStorageResource.  # noqa: E501
        :type type: str
        :param size: The size of this SimpleStorageResource.  # noqa: E501
        :type size: MinMaxInteger
        """
        self.openapi_types = {
            'ident': str,
            'name': str,
            'properties': Dict[str, str],
            'type': str,
            'size': MinMaxInteger
        }

        self.attribute_map = {
            'ident': 'ident',
            'name': 'name',
            'properties': 'properties',
            'type': 'type',
            'size': 'size'
        }

        self._ident = ident
        self._name = name
        self._properties = properties
        self._type = type
        self._size = size

    @classmethod
    def from_dict(cls, dikt) -> 'SimpleStorageResource':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The SimpleStorageResource of this SimpleStorageResource.  # noqa: E501
        :rtype: SimpleStorageResource
        """
        return util.deserialize_model(dikt, cls)

    @property
    def ident(self) -> str:
        """Gets the ident of this SimpleStorageResource.


        :return: The ident of this SimpleStorageResource.
        :rtype: str
        """
        return self._ident

    @ident.setter
    def ident(self, ident: str):
        """Sets the ident of this SimpleStorageResource.


        :param ident: The ident of this SimpleStorageResource.
        :type ident: str
        """

        self._ident = ident

    @property
    def name(self) -> str:
        """Gets the name of this SimpleStorageResource.


        :return: The name of this SimpleStorageResource.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """Sets the name of this SimpleStorageResource.


        :param name: The name of this SimpleStorageResource.
        :type name: str
        """

        self._name = name

    @property
    def properties(self) -> Dict[str, str]:
        """Gets the properties of this SimpleStorageResource.

        A map of name->value properties. See https://swagger.io/docs/specification/data-models/dictionaries/   # noqa: E501

        :return: The properties of this SimpleStorageResource.
        :rtype: Dict[str, str]
        """
        return self._properties

    @properties.setter
    def properties(self, properties: Dict[str, str]):
        """Sets the properties of this SimpleStorageResource.

        A map of name->value properties. See https://swagger.io/docs/specification/data-models/dictionaries/   # noqa: E501

        :param properties: The properties of this SimpleStorageResource.
        :type properties: Dict[str, str]
        """

        self._properties = properties

    @property
    def type(self) -> str:
        """Gets the type of this SimpleStorageResource.


        :return: The type of this SimpleStorageResource.
        :rtype: str
        """
        return self._type

    @type.setter
    def type(self, type: str):
        """Sets the type of this SimpleStorageResource.


        :param type: The type of this SimpleStorageResource.
        :type type: str
        """
        if type is None:
            raise ValueError("Invalid value for `type`, must not be `None`")  # noqa: E501

        self._type = type

    @property
    def size(self) -> MinMaxInteger:
        """Gets the size of this SimpleStorageResource.

        The size of storage required, specified in SI units. Default units are `GiB`.   # noqa: E501

        :return: The size of this SimpleStorageResource.
        :rtype: MinMaxInteger
        """
        return self._size

    @size.setter
    def size(self, size: MinMaxInteger):
        """Sets the size of this SimpleStorageResource.

        The size of storage required, specified in SI units. Default units are `GiB`.   # noqa: E501

        :param size: The size of this SimpleStorageResource.
        :type size: MinMaxInteger
        """

        self._size = size