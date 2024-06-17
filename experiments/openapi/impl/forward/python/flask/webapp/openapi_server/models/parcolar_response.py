from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.parcolar_offer import ParcolarOffer
from openapi_server import util

from openapi_server.models.parcolar_offer import ParcolarOffer  # noqa: E501

class ParcolarResponse(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, result=None, offers=None):  # noqa: E501
        """ParcolarResponse - a model defined in OpenAPI

        :param result: The result of this ParcolarResponse.  # noqa: E501
        :type result: str
        :param offers: The offers of this ParcolarResponse.  # noqa: E501
        :type offers: List[ParcolarOffer]
        """
        self.openapi_types = {
            'result': str,
            'offers': List[ParcolarOffer]
        }

        self.attribute_map = {
            'result': 'result',
            'offers': 'offers'
        }

        self._result = result
        self._offers = offers

    @classmethod
    def from_dict(cls, dikt) -> 'ParcolarResponse':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ParcolarResponse of this ParcolarResponse.  # noqa: E501
        :rtype: ParcolarResponse
        """
        return util.deserialize_model(dikt, cls)

    @property
    def result(self) -> str:
        """Gets the result of this ParcolarResponse.


        :return: The result of this ParcolarResponse.
        :rtype: str
        """
        return self._result

    @result.setter
    def result(self, result: str):
        """Sets the result of this ParcolarResponse.


        :param result: The result of this ParcolarResponse.
        :type result: str
        """
        allowed_values = ["YES", "NO"]  # noqa: E501
        if result not in allowed_values:
            raise ValueError(
                "Invalid value for `result` ({0}), must be one of {1}"
                .format(result, allowed_values)
            )

        self._result = result

    @property
    def offers(self) -> List[ParcolarOffer]:
        """Gets the offers of this ParcolarResponse.


        :return: The offers of this ParcolarResponse.
        :rtype: List[ParcolarOffer]
        """
        return self._offers

    @offers.setter
    def offers(self, offers: List[ParcolarOffer]):
        """Sets the offers of this ParcolarResponse.


        :param offers: The offers of this ParcolarResponse.
        :type offers: List[ParcolarOffer]
        """

        self._offers = offers
