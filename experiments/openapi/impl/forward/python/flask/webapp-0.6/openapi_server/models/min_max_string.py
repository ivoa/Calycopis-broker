from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server import util


class MinMaxString(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, min=None, max=None, units=None):  # noqa: E501
        """MinMaxString - a model defined in OpenAPI

        :param min: The min of this MinMaxString.  # noqa: E501
        :type min: str
        :param max: The max of this MinMaxString.  # noqa: E501
        :type max: str
        :param units: The units of this MinMaxString.  # noqa: E501
        :type units: str
        """
        self.openapi_types = {
            'min': str,
            'max': str,
            'units': str
        }

        self.attribute_map = {
            'min': 'min',
            'max': 'max',
            'units': 'units'
        }

        self._min = min
        self._max = max
        self._units = units

    @classmethod
    def from_dict(cls, dikt) -> 'MinMaxString':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The MinMaxString of this MinMaxString.  # noqa: E501
        :rtype: MinMaxString
        """
        return util.deserialize_model(dikt, cls)

    @property
    def min(self) -> str:
        """Gets the min of this MinMaxString.


        :return: The min of this MinMaxString.
        :rtype: str
        """
        return self._min

    @min.setter
    def min(self, min: str):
        """Sets the min of this MinMaxString.


        :param min: The min of this MinMaxString.
        :type min: str
        """

        self._min = min

    @property
    def max(self) -> str:
        """Gets the max of this MinMaxString.


        :return: The max of this MinMaxString.
        :rtype: str
        """
        return self._max

    @max.setter
    def max(self, max: str):
        """Sets the max of this MinMaxString.


        :param max: The max of this MinMaxString.
        :type max: str
        """

        self._max = max

    @property
    def units(self) -> str:
        """Gets the units of this MinMaxString.


        :return: The units of this MinMaxString.
        :rtype: str
        """
        return self._units

    @units.setter
    def units(self, units: str):
        """Sets the units of this MinMaxString.


        :param units: The units of this MinMaxString.
        :type units: str
        """

        self._units = units
