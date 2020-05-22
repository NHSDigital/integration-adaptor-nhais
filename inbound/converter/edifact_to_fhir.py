from fhir.resources.patient import Patient
from edifact.models.edifact import Edifact
from edifact.models.segment import Segment

TERMINATOR = "'"
DELIMITER = "+"

class EdifactToFhir:

    def convert_edifact_to_fhir(self, edifact: Edifact) -> Patient:
        pass
        # patient = Patient()
        #
        # # ---HEADER ----
        # # 1) UNB -> Interchange Header
        # # get sender and recipient
        # # patient.managingOrganization.identifier.value = sender (of the incoming edifact message)
        # # patient.generalPractitioner[0].identifier.value = recipient
        #
        # interchange_header = edifact.interchange_header
        # patient.managingOrganization.identifier.value = interchange_header.
        # patient.generalPractitioner[0].identifier.value = recipient
        #
        # # 4) NAD+FHS -> Name and Address
        # # patient.managingOrganization.identifier.value
        # patient.managingOrganization.identifier.value = sender
        # patient.active = None
        # patient.address = None
        # patient.birthDate = None
        # patient.communication = None
        # patient.contact = None
        # patient.deceasedBoolean = None
        # patient.deceasedDateTime = None
        # patient.gender = None
        #
        # patient.identifier = None
        # patient.id = None
        # patient.link = None
        #
        # patient.maritalStatus = None
        # patient.multipleBirthBoolean = None
        # patient.multipleBirthInteger = None
        # patient.name = None
        # patient.photo = None
        # patient.resource_type = None
        # patient.telecom = None
        #
        # return patient

    # def translate_header(self):
    #     pass
    #
    # def translate_footer(self):
    #     pass

#     edifact_recep_producer -> generate sequences -> sequence/inbound/sequence_number_manager
#
