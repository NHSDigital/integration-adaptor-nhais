import json
import unittest
from datetime import datetime

from fhir.resources.address import Address
from fhir.resources.extension import Extension
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


def _create_name_minimal(surname):
    name = HumanName()
    name.family = surname

    return name

def _create_name_maximal(surname, first, second, others, title):
    name = HumanName()
    name.family = surname
    name.given = [first, second, others]
    name.prefix = [title]
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

def _create_address_maximal() -> Address:
    a = _create_address_minimal()
    a.postalCode = 'SO15 2GB'
    return a

def _create_place_of_birth() -> Extension:
    extension = Extension()
    extension.url = 'http://hl7.org/fhir/StructureDefinition/patient-birthPlace'
    extension.valueString = 'TownOfBirth'
    return extension

def _create_drugs_dispensed_marker() -> Extension:
    extension = Extension()
    extension.url = 'https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-DrugsDispensedMarker'
    extension.valueBoolean = True
    return extension

def _create_residential_institute_code() -> Extension:
    extension = Extension()
    extension.url = 'https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-ResidentialInstituteCode'
    extension.valueString = 'ResidentialInstituteCode'
    return extension

def _create_gp_notes() -> Extension:
    extension = Extension()
    extension.url = 'https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-GPNotes'
    extension.valueString = 'GP Notes Free Text'
    return extension

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

    def _birth_maximal(self):
        patient = self._birth_minimal()
        patient.name[0] = _create_name_maximal('Surname', 'FirstForename', 'SecondForename', 'OtherForename1 OtherForename2', 'Title')
        patient.name.append(_create_name_minimal('Previous Surname'))
        patient.address = [_create_address_maximal()]
        patient.extension = [_create_place_of_birth(),
                             _create_drugs_dispensed_marker(),
                             _create_residential_institute_code(),
                             _create_gp_notes()]
        return patient

    def test_birth_minimal(self):
        patient = self._birth_minimal()

        json_dict = patient.as_json()
        json_str = json.dumps(json_dict, indent=2)
        print(json_str)

    def test_birth_maximal(self):
        patient = self._birth_maximal()

        json_dict = patient.as_json()
        json_str = json.dumps(json_dict, indent=2)
        print(json_str)
