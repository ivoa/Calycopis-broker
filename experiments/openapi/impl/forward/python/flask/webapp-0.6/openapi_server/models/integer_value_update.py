from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.update_base import UpdateBase
from openapi_server import util

from openapi_server.models.update_base import UpdateBase  # noqa: E501

class IntegerValueUpdate(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, type=None, path=None, value=None, units=None):  # noqa: E501
        """IntegerValueUpdate - a model defined in OpenAPI

        :param type: The type of this IntegerValueUpdate.  # noqa: E501
        :type type: str
        :param path: The path of this IntegerValueUpdate.  # noqa: E501
        :type path: str
        :param value: The value of this IntegerValueUpdate.  # noqa: E501
        :type value: int
        :param units: The units of this IntegerValueUpdate.  # noqa: E501
        :type units: str
        """
        self.openapi_types = {
            'type': str,
            'path': str,
            'value': int,
            'units': str
        }

        self.attribute_map = {
            'type': 'type',
            'path': 'path',
            'value': 'value',
            'units': 'units'
        }

        self._type = type
        self._path = path
        self._value = value
        self._units = units

    @classmethod
    def from_dict(cls, dikt) -> 'IntegerValueUpdate':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The IntegerValueUpdate of this IntegerValueUpdate.  # noqa: E501
        :rtype: IntegerValueUpdate
        """
        return util.deserialize_model(dikt, cls)

    @property
    def type(self) -> str:
        """Gets the type of this IntegerValueUpdate.


        :return: The type of this IntegerValueUpdate.
        :rtype: str
        """
        return self._type

    @type.setter
    def type(self, type: str):
        """Sets the type of this IntegerValueUpdate.


        :param type: The type of this IntegerValueUpdate.
        :type type: str
        """
        if type is None:
            raise ValueError("Invalid value for `type`, must not be `None`")  # noqa: E501

        self._type = type

    @property
    def path(self) -> str:
        """Gets the path of this IntegerValueUpdate.

        The target path that the update applies to.   # noqa: E501

        :return: The path of this IntegerValueUpdate.
        :rtype: str
        """
        return self._path

    @path.setter
    def path(self, path: str):
        """Sets the path of this IntegerValueUpdate.

        The target path that the update applies to.   # noqa: E501

        :param path: The path of this IntegerValueUpdate.
        :type path: str
        """
        if path is None:
            raise ValueError("Invalid value for `path`, must not be `None`")  # noqa: E501

        self._path = path

    @property
    def value(self) -> int:
        """Gets the value of this IntegerValueUpdate.

        The integer value to use.   # noqa: E501

        :return: The value of this IntegerValueUpdate.
        :rtype: int
        """
        return self._value

    @value.setter
    def value(self, value: int):
        """Sets the value of this IntegerValueUpdate.

        The integer value to use.   # noqa: E501

        :param value: The value of this IntegerValueUpdate.
        :type value: int
        """
        if value is None:
            raise ValueError("Invalid value for `value`, must not be `None`")  # noqa: E501

        self._value = value

    @property
    def units(self) -> str:
        """Gets the units of this IntegerValueUpdate.

        The units to use for the value.   # noqa: E501

        :return: The units of this IntegerValueUpdate.
        :rtype: str
        """
        return self._units

    @units.setter
    def units(self, units: str):
        """Sets the units of this IntegerValueUpdate.

        The units to use for the value.   # noqa: E501

        :param units: The units of this IntegerValueUpdate.
        :type units: str
        """

        self._units = units