import enum
import re
from datetime import datetime

from edifact.edifact_exception import EdifactValidationException
from edifact.models.segment import Segment
from edifact.patterns import UNH_PATTERN, NAD_MSG_HEADER_PATTERN, DTM_MSG_HEADER_PATTERN, \
    SG_PATTERN, RFF_TN_PATTERN, UNT_PATTERN, RFF_PATTERN


class MessageHeader(Segment):
    """
    A specialisation of a segment for the specific use case of a message header
    takes in specific values required to generate an message header
    example: UNH+00000003+FHSREG:0:1:FH:FHS001'
    """

    def __init__(self, sequence_number: (int, None) = None):
        """
        :param sequence_number: a unique reference of the message
        """
        self.sequence_number = sequence_number

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        unh_match = re.match(UNH_PATTERN, message_line)
        sequence_number = int(unh_match.group('sms'))
        return cls(sequence_number)

    @property
    def key(self):
        return "UNH"

    def _validate_stateful(self):
        self._required('sequence_number')

    @property
    def value(self):
        formatted_sequence_number = f'{self.sequence_number:08}'
        return f"{formatted_sequence_number}+FHSREG:0:1:FH:FHS001"

    def pre_validate(self):
        pass


class MessageTrailer(Segment):
    """
    A specialisation of a segment for the specific use case of a message trailer
    takes in specific values required to generate a message trailer
    example: UNT+18+00000003'
    """

    def __init__(self, number_of_segments: (None, int) = None, sequence_number: (None, int) = None):
        """
        :param number_of_segments: the total number of segments in the message including the header and trailer
        :param sequence_number: a unique reference of the message
        """
        self.number_of_segments = number_of_segments
        self.sequence_number = sequence_number

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        unt_match = re.match(UNT_PATTERN, message_line)
        number_of_segments = int(unt_match.group('segment_count'))
        sequence_number = int(unt_match.group('sms'))
        return cls(number_of_segments, sequence_number)

    @property
    def key(self):
        return "UNT"

    @property
    def value(self):
        formatted_sequence_number = f'{self.sequence_number:08}'
        return f"{self.number_of_segments}+{formatted_sequence_number}"

    def pre_validate(self):
        pass

    def _validate_stateful(self):
        self._required('number_of_segments')
        self._required('sequence_number')


class BeginningOfMessage(Segment):
    """
    This segment is used to provide a code for the message which indicates its use. It is a constant of EDIFACT
    example: BGM+++507'
    """

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        return cls()

    @property
    def key(self):
        return 'BGM'

    @property
    def value(self):
        return '++507'

    def pre_validate(self):
        pass


class NameAndAddress(Segment):

    class QualifierAndCode(enum.Enum):
        FHS = ('FHS', '954')

    def __init__(self, party_qualifier_and_code: QualifierAndCode, party_identifier: str):
        (self.qualifier, self.code) = party_qualifier_and_code.value
        self.identifier = party_identifier

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        nad_match = re.match(NAD_MSG_HEADER_PATTERN, message_line)
        party_qualifier_and_code = NameAndAddress.QualifierAndCode.FHS
        assert (nad_match.group('party_qualifier'), nad_match.group('party_code')) == party_qualifier_and_code.value
        party_identifier = nad_match.group('party_id')
        return cls(party_qualifier_and_code, party_identifier)

    @property
    def key(self):
        return 'NAD'

    @property
    def value(self):
        return f'{self.qualifier}+{self.identifier}:{self.code}'

    def pre_validate(self):
        self._required('qualifier')
        self._required('identifier')
        self._required('code')


class DateTimePeriod(Segment):

    class TypeAndFormat (enum.Enum):
        TRANSLATION_TIMESTAMP = ('137', '203', '%Y%m%d%H%M')
        PERIOD_END_DATE = ('206', '102', '%Y%m%d')

    def __init__(self, qualifier_and_code: TypeAndFormat, timestamp: datetime):
        (self.type_code, self.format_code, self.date_time_format) = qualifier_and_code.value
        self.timestamp = timestamp

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        dtm_match = re.match(DTM_MSG_HEADER_PATTERN, message_line)
        type_code = dtm_match.group('type_code')
        format_code = dtm_match.group('format_code')

        date_time_formats = {}
        for type_and_format in DateTimePeriod.TypeAndFormat:
            date_time_formats[type_and_format.value[0]] = type_and_format

        type_and_format = date_time_formats[type_code]
        assert format_code == type_and_format.value[1]

        date_time_format = type_and_format.value[2]
        timestamp_string = dtm_match.group('date_time_value')
        timestamp = datetime.strptime(timestamp_string, date_time_format)

        qualifier_and_code = date_time_formats.get(type_code)
        return cls(qualifier_and_code, timestamp)

    @property
    def key(self):
        return 'DTM'

    @property
    def value(self):
        formatted_date_time = self.timestamp.strftime(self.date_time_format)
        return f'{self.type_code}:{formatted_date_time}:{self.format_code}'

    def pre_validate(self):
        self._required('type_code')
        self._required('format_code')
        self._required('date_time_format')
        self._required('timestamp')


class Reference(Segment):

    def __init__(self, qualifier: str, reference: (None, int, str)):
        self.qualifier = qualifier
        self.reference = reference

    @property
    def key(self):
        return 'RFF'

    @property
    def value(self):
        return f'{self.qualifier}:{self.reference}'

    def pre_validate(self):
        self._required('qualifier')
        self._required('reference')


class ReferenceTransactionType(Reference):
    class TransactionType(enum.Enum):
        ACCEPTANCE = 'G1'
        AMENDMENT = 'G2'
        REMOVAL = 'G3'
        DEDUCTION = 'G5'

    def __init__(self, transaction_type: TransactionType):
        super().__init__(qualifier='950', reference=transaction_type.value)

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        rff_match = re.match(RFF_PATTERN, message_line)
        transaction_type = rff_match.group('transaction_type')

        transaction_types = {}
        for t in ReferenceTransactionType.TransactionType:
            transaction_types[t.value] = t
        transaction_type_code = transaction_types.get(transaction_type)

        return cls(transaction_type_code)


class ReferenceTransactionNumber(Reference):

    def __init__(self, reference: (int, None) = None):
        super().__init__(qualifier='TN', reference=reference)

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        rff_tn_match = re.match(RFF_TN_PATTERN, message_line)
        reference = rff_tn_match.group('transaction_number')
        return cls(reference)

    def pre_validate(self):
        self._required('qualifier')

    def _validate_stateful(self):
        self._required('reference')


class SegmentGroup(Segment):

    def __init__(self, segment_group_number: int):
        self.segment_group_number = segment_group_number

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        sg_match = re.match(SG_PATTERN, message_line)

        sg0 = sg_match.group('segment_group_number_0')
        sg1 = sg_match.group('segment_group_number')

        # TODO: sg0 is zero-padded, ignoring this check for now
        # if sg0 != sg1:
        #     raise EdifactValidationException(f'Segment group numbers do not match for segment: {message_line}')

        segment_group_number = sg_match.group('segment_group_number')
        return cls(int(segment_group_number))

    @property
    def key(self):
        return f'S{self.segment_group_number:02}'

    @property
    def value(self):
        return f'{self.segment_group_number}'

    def pre_validate(self):
        if not self.segment_group_number:
            raise EdifactValidationException(f'S: Attribute segment_group_number is required')
        if not isinstance(self.segment_group_number, int):
            raise EdifactValidationException(f'S: Attribute segment_group_number must be an integer')
        if self.segment_group_number not in (1, 2):
            raise EdifactValidationException(f'S: Attribute segment_group_number must be 1 or 2')
