import abc
import re
from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, ReferenceTransactionNumber, \
    ReferenceTransactionType, BeginningOfMessage, NameAndAddress, DateTimePeriod, SegmentGroup

from edifact.edifact_exception import EdifactValidationException
from edifact.util import UNB_PATTERN, UNH_PATTERN, BGM_PATTERN, NAD_MSG_HEADER_PATTERN, DTM_MSG_HEADER_PATTERN, \
    SG_PATTERN, RFF_TN_PATTERN, UNT_PATTERN, UNZ_PATTERN, RFF_PATTERN


class Segment(abc.ABC):
    """
    A segment is the basic building block of an edifact message.
    It represent each line in the edifact message that will be generated.
    example: NAD+GP+4826940,281:900'
    """

    TERMINATOR = "'"

    @property
    @abc.abstractmethod
    def key(self):
        """
        :return: the key of the segment for example NAD, DTM ...
        """
        pass

    @property
    @abc.abstractmethod
    def value(self):
        """
        :return: the value of the segment
        """
        pass

    @abc.abstractmethod
    def pre_validate(self):
        """
        Validates non-stateful data items of the segment (excludes things like sequence numbers)
        :raises: EdifactValidationException
        """
        pass

    def _validate_stateful(self):
        """
        Validates stateful data items of the segment like sequence numbers
        :raises: EdifactValidationException
        """
        pass

    def validate(self):
        """
        Validates the entire segment including stateful items like sequence numbers
        :raises: EdifactValidationException
        """
        self.pre_validate()
        self._validate_stateful()

    def to_edifact_string(self):
        """
        generates the edifact message of the segment
        :return: a string of the formatted edifact message using the key and value
        """
        self.validate()
        edifact_segment = f"{self.key}+{self.value}{Segment.TERMINATOR}"
        return edifact_segment

    def _required(self, attribute_name):
        """
        A validation method to require that a specific property is truthy
        :param attribute_name: the attribute name to test
        :raises: EdifactValidationException if the attribute is not set
        """
        if not getattr(self, attribute_name, None):
            raise EdifactValidationException(f'{self.key}: Attribute {attribute_name} is required')


class SegmentFactory:

    @staticmethod
    def create_segment_from_string(message_line: str) -> Segment:
        if segment_pattern_check(message_line, UNB_PATTERN):
            return InterchangeHeader.from_string(message_line)
        elif segment_pattern_check(message_line, UNZ_PATTERN):
            return InterchangeTrailer.from_string(message_line)
        elif segment_pattern_check(message_line, UNH_PATTERN):
            return MessageHeader.from_string(message_line)
        elif segment_pattern_check(message_line, UNT_PATTERN):
            return MessageTrailer.from_string(message_line)
        elif segment_pattern_check(message_line, BGM_PATTERN):
            return BeginningOfMessage.from_string(message_line)
        elif segment_pattern_check(message_line, NAD_MSG_HEADER_PATTERN):
            return NameAndAddress.from_string(message_line)
        elif segment_pattern_check(message_line, DTM_MSG_HEADER_PATTERN):
            return DateTimePeriod.from_string(message_line)
        elif segment_pattern_check(message_line, RFF_PATTERN):
            return ReferenceTransactionType.from_string(message_line)
        elif segment_pattern_check(message_line, RFF_TN_PATTERN):
            return ReferenceTransactionNumber.from_string(message_line)
        elif segment_pattern_check(message_line, SG_PATTERN):
            return SegmentGroup.from_string(message_line)


def segment_pattern_check(message_line, regex_pattern) -> bool:
    regex = re.compile(regex_pattern)
    return bool(regex.match(message_line))
