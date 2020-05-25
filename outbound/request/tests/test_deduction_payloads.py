import json
import unittest
from datetime import datetime, date

from fhir.resources.address import Address
from fhir.resources.extension import Extension
from fhir.resources.fhirdate import FHIRDate
from fhir.resources.fhirreference import FHIRReference
from fhir.resources.humanname import HumanName
from fhir.resources.identifier import Identifier
from fhir.resources.operationdefinition import OperationDefinition, OperationDefinitionParameter, \
    OperationDefinitionParameterBinding
from fhir.resources.organization import Organization
from fhir.resources.parameters import Parameters, ParametersParameter
from fhir.resources.patient import Patient
from fhir.resources.valueset import ValueSet

GP_ID = 'GP123'
HA_ID = 'HA456'
NHS_NUMBER = '54321'
SURNAME = 'Smith'
GENDER = 'male'
DOB = datetime(year=1990, month=1, day=1, hour=9, minute=33)


def _create_org_ref(identifier_value: str):
    ref = FHIRReference()
    ref.type = Organization.resource_type
    identifier = Identifier()
    identifier.value = identifier_value
    ref.identifier = identifier
    return ref


class TestDeductionPayloads(unittest.TestCase):
    """
    Verifies that acceptance examples enclosed in API documentation are valid according to fhir.resources library
    """

    def _deuction_minimal(self) -> Parameters:
        parameters = Parameters()
        patient_param = ParametersParameter()
        patient_param.name = 'patient'
        patient = Patient()
        patient.id = NHS_NUMBER
        patient.generalPractitioner = [_create_org_ref(GP_ID)]
        patient.managingOrganization = _create_org_ref(HA_ID)
        patient_param.resource = patient

        reason = ParametersParameter()
        reason.name = 'reason'
        reason.valueString = 'death'  # Can be: death, emigrated, other

        deduction_date = ParametersParameter()
        deduction_date.name = 'date'
        deduction_date.valueDate = FHIRDate()
        deduction_date.valueDate.date = date(year=1991, month=12, day=25)

        reason_details = ParametersParameter()
        reason_details.name = 'reasonDetails'
        reason_details.valueString = 'DIED ON HOLIDAY IN MAJORCA'  # free text deduction reason to be supplied by GP

        parameters.parameter = [patient_param, reason, deduction_date, reason_details]
        return parameters

    def test_deduction(self):
        payload = self._deuction_minimal()

        json_dict = payload.as_json()
        json_str = json.dumps(json_dict, indent=2)
        print(json_str)
