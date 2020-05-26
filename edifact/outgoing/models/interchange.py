import re
import re
from datetime import datetime
from edifact.models.segment import Segment
from edifact.patterns import UNB_PATTERN, UNZ_PATTERN


class InterchangeHeader(Segment):
    """
    A specialisation of a segment for the specific use case of an interchange header
    takes in specific values required to generate an interchange header
    example: UNB+UNOA:2+TES5+XX11+920113:1317+00000002'
    """

    TIMESTAMP_FORMAT ='%y%m%d:%H%M'

    def __init__(self, sender, recipient, date_time: datetime, sequence_number: (None, int) = None):
        """
        :param sender: the sender of the interchange
        :param recipient: the intended recipient of the interchange
        :param date_time: the date time stamp of the interchange header
        :param sequence_number: a unique reference of the interchange
        """
        self.sender = sender
        self.recipient = recipient
        self.date_time = date_time
        self.sequence_number = sequence_number

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        unb_match = re.match(UNB_PATTERN, message_line)
        sender = unb_match.group('sender')
        recipient = unb_match.group('recipient')
        timestamp_string = unb_match.group('timestamp')
        timestamp = datetime.strptime(timestamp_string, InterchangeHeader.TIMESTAMP_FORMAT)
        sequence_number = int(unb_match.group('sis'))
        return cls(sender, recipient, timestamp, sequence_number)

    @property
    def key(self):
        return "UNB"

    @property
    def value(self):
        formatted_date_time = self.date_time.strftime(InterchangeHeader.TIMESTAMP_FORMAT)
        formatted_sequence_number = f'{self.sequence_number:08}'
        return f"UNOA:2+{self.sender}+{self.recipient}+{formatted_date_time}+{formatted_sequence_number}"

    def pre_validate(self):
        self._required('sender')
        self._required('recipient')
        self._required('date_time')

    def _validate_stateful(self):
        self._required('sequence_number')


class InterchangeTrailer(Segment):
    """
    A specialisation of a segment for the specific use case of an interchange trailer
    takes in specific values required to generate an interchange trailer
    example: UNZ+1+00000002'
    """

    def __init__(self, number_of_messages: int, sequence_number: (None, int) = None):
        """
        :param number_of_messages: the number of messages within this interchange
        :param sequence_number: a unique reference of the interchange
        """
        self.number_of_messages = number_of_messages
        self.sequence_number = sequence_number

    @classmethod
    def from_string(cls, message_line: str) -> Segment:
        """
        generates edifact segment from given edifact message line
        """
        unz_match = re.match(UNZ_PATTERN, message_line)
        number_of_messages = int(unz_match.group('message_count'))
        sequence_number = int(unz_match.group('sis'))
        return cls(number_of_messages, sequence_number)

    @property
    def key(self):
        return "UNZ"

    @property
    def value(self):
        formatted_sequence_number = f'{self.sequence_number:08}'
        return f"{self.number_of_messages}+{formatted_sequence_number}"

    def pre_validate(self):
        self._required('number_of_messages')

    def _validate_stateful(self):
        self._required('sequence_number')
