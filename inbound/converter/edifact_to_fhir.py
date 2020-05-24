from edifact.edifact_exception import EdifactValidationException
from fhir.resources.patient import Patient
from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, \
    DateTimePeriod, ReferenceTransactionNumber, ReferenceTransactionType, SegmentGroup


class EdifactToFhir:

    def __init__(self):
        self.__patient = Patient()
        self.interchange_id = None
        self.number_of_messages = None
        self.number_of_segments = {}
        self.message_ids = {}

    def convert_edifact_to_fhir(self, edifact: Edifact) -> Patient:
        self.__translate_interchange_header(edifact.interchange_header)
        self.__translate_interchange_trailer(edifact.interchange_trailer)

        # TODO 2010-05-24: for now (NIAD-141) it should be assumed a single message per interchange
        # TODO although the loop is ready to use for the task NIAD-224
        for message_no in range(self.number_of_messages):
            self.__translate_message_header(message_no, edifact.message_header)
            self.__translate_message_trailer(message_no, edifact.message_trailer)
            self.__translate_beginning_of_message(edifact.beginning_of_message)
            self.__translate_name_and_address(edifact.name_and_address)
            self.__translate_date_time_period(edifact.date_time_period)
            self.__translate_reference_transaction_type(edifact.reference_transaction_type)
            self.__translate_reference_transaction_number(edifact.reference_transaction_number)
            for segment_no in range(self.number_of_segments[message_no]):
                self.__translate_segment_group(edifact.segment_group)

        return self.__patient

    def __translate_interchange_header(self, interchange_header: InterchangeHeader):
        self.__patient.managingOrganization.identifier.value = interchange_header.sender
        self.__patient.generalPractitioner[0].identifier.value = interchange_header.recipient
        self.interchange_id = interchange_header.sequence_number

    def __translate_interchange_trailer(self, interchange_trailer: InterchangeTrailer):
        interchange_id = interchange_trailer.sequence_number
        if self.interchange_id != interchange_id:
            raise EdifactValidationException(f'Interchange id in the interchange header {self.interchange_id} is not'
                                             f'equal to interchange id in the interchange footer {interchange_id}')
        self.number_of_messages = interchange_trailer.number_of_messages

    def __translate_message_header(self, message_no, message_header: MessageHeader):
        self.message_ids[message_no] = message_header.sequence_number

    def __translate_message_trailer(self, message_no, message_trailer: MessageTrailer):
        message_id = message_trailer.sequence_number
        if self.message_ids[message_no] != message_id:
            raise EdifactValidationException(f'Message id in the message header {self.message_ids[message_no]} is not'
                                             f'equal to message id in the message footer {message_id}')
        self.number_of_segments[message_no] = message_trailer.number_of_segments

    def __translate_beginning_of_message(self, beginning_of_message: BeginningOfMessage):
        # BGM is for now a constant part of message. No translation occurs
        pass

    def __translate_name_and_address(self, name_and_address: NameAndAddress):
        # TODO 2020-05-24: NIAD-141 translates only common header and footer message portions
        # TODO this method should be amended under the tickets for HA -> GP message translation
        # TODO NIAD-{216, 217, 218, 219, 220, 221, 222}
        identifier = name_and_address.identifier
        qualifier = name_and_address.qualifier
        code = name_and_address.code

    def __translate_date_time_period(self, date_time_period: DateTimePeriod):
        # TODO 2020-05-24: NIAD-141 translates only common header and footer message portions
        # TODO this method should be amended under the tickets for HA -> GP message translation
        # TODO NIAD-{216, 217, 218, 219, 220, 221, 222}
        date_time_format = date_time_period.date_time_format
        timestamp = date_time_period.timestamp
        type_code = date_time_period.type_code
        format_code = date_time_period.format_code

    def __translate_reference_transaction_type(self, reference_transaction_type: ReferenceTransactionType):
        # TODO 2020-05-24: NIAD-141 translates only common header and footer message portions
        # TODO this method should be amended under the tickets for HA -> GP message translation
        # TODO NIAD-{216, 217, 218, 219, 220, 221, 222}
        qualifier = reference_transaction_type.qualifier
        reference = reference_transaction_type.reference

    def __translate_reference_transaction_number(self, reference_transaction_number: ReferenceTransactionNumber):
        # TODO 2020-05-24: NIAD-141 translates only common header and footer message portions
        # TODO this method should be amended under the tickets for HA -> GP message translation
        # TODO NIAD-{216, 217, 218, 219, 220, 221, 222}
        qualifier = reference_transaction_number.qualifier
        reference = reference_transaction_number.reference

    def __translate_segment_group(self, segment_group: SegmentGroup):
        # TODO 2020-05-24: NIAD-141 translates only common header and footer message portions
        # TODO this method should be amended under the tickets for HA -> GP message translation
        # TODO NIAD-{216, 217, 218, 219, 220, 221, 222}
        segment_group_number = segment_group.segment_group_number


