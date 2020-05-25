from typing import List, Type

from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, DateTimePeriod, \
    Reference, ReferenceTransactionType, ReferenceTransactionNumber, SegmentGroup
from edifact.models.segment import Segment, SegmentFactory


class Edifact:
    _segments: List[Segment]

    interchange_header = property(lambda self: self._get_segment(InterchangeHeader))
    interchange_trailer = property(lambda self: self._get_segment(InterchangeTrailer))
    message_header = property(lambda self: self._get_segment(MessageHeader))
    message_trailer = property(lambda self: self._get_segment(MessageTrailer))
    beginning_of_message = property(lambda self: self._get_segment(BeginningOfMessage))
    name_and_address = property(lambda self: self._get_segment(NameAndAddress))
    date_time_period = property(lambda self: self._get_segment(DateTimePeriod))
    reference = property(lambda self: self._get_segment(Reference))
    reference_transaction_type = property(lambda self: self._get_segment(ReferenceTransactionType))
    reference_transaction_number = property(lambda self: self._get_segment(ReferenceTransactionNumber))
    segment_group = property(lambda self: self._get_segment(SegmentGroup))

    def __init__(self, segments: List[Segment]) -> None:
        self._segments = segments

    @classmethod
    def create_edifact_from_message(cls, message):
        lines = message.splitlines()
        segments = []
        for line in lines:
            segments.append(SegmentFactory.create_segment_from_string(line))
        cls(segments)

    def create_message_from_edifact(self) -> str:
        return '\n'.join([segment.to_edifact_string() for segment in self._segments])

    def _get_segment(self, segment_type: Type) -> Segment:
        filtered_segments = list(filter(lambda s: type(s) is segment_type, self._segments))
        if len(filtered_segments) != 1:
            raise LookupError(f"EDIFACT message does not contain segment type '{segment_type}'")
        return filtered_segments[0]
