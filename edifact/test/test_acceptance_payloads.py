import unittest
from datetime import datetime

from fhir.resources.fhirdate import FHIRDate
from fhir.resources.fhirreference import FHIRReference
from fhir.resources.humanname import HumanName
from fhir.resources.identifier import Identifier
from fhir.resources.organization import Organization
from fhir.resources.patient import Patient

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

class TestAcceptancePayloads(unittest.TestCase):

    def test_birth(self):
        patient = Patient()
        patient.id = NHS_NUMBER
        patient.generalPractitioner = [_create_org_ref(GP_ID)]
        patient.managingOrganization = _create_org_ref(HA_ID)
        patient.name = [_create_name(SURNAME)]
        patient.gender = GENDER
        patient.birthDate = _create_date(DOB)
