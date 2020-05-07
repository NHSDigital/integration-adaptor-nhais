import json
import unittest
from datetime import datetime

from fhir.resources.address import Address
from fhir.resources.fhirdate import FHIRDate
from fhir.resources.fhirreference import FHIRReference
from fhir.resources.humanname import HumanName
from fhir.resources.identifier import Identifier
from fhir.resources.operationdefinition import OperationDefinition, OperationDefinitionParameter, \
    OperationDefinitionParameterBinding
from fhir.resources.organization import Organization
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


def _create_name(surname):
    name = HumanName()
    name.family = surname
    return name


def _create_date(value: datetime) -> FHIRDate:
    fhir_date = FHIRDate()
    fhir_date.date = value
    return fhir_date


def _create_address() -> Address:
    a = Address()
    a.line = ['house name', 'number or road name', 'locality', 'town', 'county']
    a.postalCode = 'SO15 2GB'
    return a


def _acceptance_type(type: str) -> OperationDefinitionParameter:
    param = OperationDefinitionParameter()
    param.name = 'acceptanceType'
    param.type = 'data'
    param.value = type
    binding = OperationDefinitionParameterBinding()
    value_set = ValueSet()
    value_set.identifier = Identifier()
    value_set.identifier.value = type
    binding.valueSet = value_set
    param.binding = binding
    return param


class TestAcceptancePayloads(unittest.TestCase):

    def test_birth_minimal(self):
        patient = Patient()
        patient.id = NHS_NUMBER
        patient.generalPractitioner = [_create_org_ref(GP_ID)]
        patient.managingOrganization = _create_org_ref(HA_ID)
        patient.name = [_create_name(SURNAME)]
        patient.gender = GENDER
        patient.birthDate = _create_date(DOB)
        patient.address = [_create_address()]

        opdef = OperationDefinition()
        opdef.contained = [patient]
        opdef.parameter = [_acceptance_type('birth')]

        json_dict = patient.as_json()
        json_str = json.dumps(json_dict, indent=2)
        print(json_str)
