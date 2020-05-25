import json
import unittest
from datetime import datetime

import jsonpatch
from fhir.resources.address import Address
from fhir.resources.fhirdate import FHIRDate
from fhir.resources.fhirreference import FHIRReference
from fhir.resources.humanname import HumanName
from fhir.resources.identifier import Identifier
from fhir.resources.organization import Organization
from fhir.resources.patient import Patient

GP_ID = 'GP123'
HA_ID = 'HA456'
NHS_NUMBER = '54321'
GENDER = 'male'
DOB = datetime(year=1990, month=1, day=1, hour=9, minute=33)


def _create_org_ref(identifier_value: str):
    ref = FHIRReference()
    ref.type = Organization.resource_type
    identifier = Identifier()
    identifier.value = identifier_value
    ref.identifier = identifier
    return ref


def _create_name_minimal(surname):
    name = HumanName()
    name.family = surname

    return name


def _create_date(value: datetime) -> FHIRDate:
    fhir_date = FHIRDate()
    fhir_date.date = value
    return fhir_date


def _create_address_minimal() -> Address:
    a = Address()
    a.line = ['current house name', 'current number or road name', 'current locality', 'current town', 'current county']
    a.postalCode = 'SO15 2GB'
    return a


class TestAcceptancePayloads(unittest.TestCase):
    """
    Verifies that acceptance examples enclosed in API documentation are valid according to fhir.resources library
    """

    def _birth_minimal(self):
        patient = Patient()
        patient.id = NHS_NUMBER
        patient.generalPractitioner = [_create_org_ref(GP_ID)]
        patient.managingOrganization = _create_org_ref(HA_ID)
        patient.name = [_create_name_minimal('Surname')]
        patient.gender = GENDER
        patient.birthDate = _create_date(DOB)
        patient.address = [_create_address_minimal()]
        return patient

    def test_amendment_minimal(self):
        original_patient = self._birth_minimal()
        updated_patient = self._birth_minimal()
        updated_patient.name[0].family = "NewSurname"
        updated_patient.name[0].given = ['FirstForename', 'NewSecondForename']

        patch_obj = jsonpatch.make_patch(original_patient.as_json(), updated_patient.as_json())
        patch_dict = {'patches': patch_obj.patch}
        json_str = json.dumps(patch_dict, indent=2)
        print(json_str)
