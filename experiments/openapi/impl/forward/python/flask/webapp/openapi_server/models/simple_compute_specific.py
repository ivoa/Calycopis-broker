from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.min_max_integer import MinMaxInteger
from openapi_server import util

from openapi_server.models.min_max_integer import MinMaxInteger  # noqa: E501

class SimpleComputeSpecific(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, cores=None, memory=None):  # noqa: E501
        """SimpleComputeSpecific - a model defined in OpenAPI

        :param cores: The cores of this SimpleComputeSpecific.  # noqa: E501
        :type cores: MinMaxInteger
        :param memory: The memory of this SimpleComputeSpecific.  # noqa: E501
        :type memory: MinMaxInteger
        """
        self.openapi_types = {
            'cores': MinMaxInteger,
            'memory': MinMaxInteger
        }

        self.attribute_map = {
            'cores': 'cores',
            'memory': 'memory'
        }

        self._cores = cores
        self._memory = memory

    @classmethod
    def from_dict(cls, dikt) -> 'SimpleComputeSpecific':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The SimpleComputeSpecific of this SimpleComputeSpecific.  # noqa: E501
        :rtype: SimpleComputeSpecific
        """
        return util.deserialize_model(dikt, cls)

    @property
    def cores(self) -> MinMaxInteger:
        """Gets the cores of this SimpleComputeSpecific.


        :return: The cores of this SimpleComputeSpecific.
        :rtype: MinMaxInteger
        """
        return self._cores

    @cores.setter
    def cores(self, cores: MinMaxInteger):
        """Sets the cores of this SimpleComputeSpecific.


        :param cores: The cores of this SimpleComputeSpecific.
        :type cores: MinMaxInteger
        """

        self._cores = cores

    @property
    def memory(self) -> MinMaxInteger:
        """Gets the memory of this SimpleComputeSpecific.


        :return: The memory of this SimpleComputeSpecific.
        :rtype: MinMaxInteger
        """
        return self._memory

    @memory.setter
    def memory(self, memory: MinMaxInteger):
        """Sets the memory of this SimpleComputeSpecific.


        :param memory: The memory of this SimpleComputeSpecific.
        :type memory: MinMaxInteger
        """

        self._memory = memory
