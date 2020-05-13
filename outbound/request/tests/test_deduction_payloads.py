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
