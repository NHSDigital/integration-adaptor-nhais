from typing import List, Type

from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, DateTimePeriod, \
    Reference, ReferenceTransactionType, ReferenceTransactionNumber, SegmentGroup, ReferenceInterchangeRecep, \
    ReferenceMessageRecep
from edifact.models.segment import Segment


class Edifact:
    _segments: List[Segment]

    interchange_header = property(lambda self: self._get_segment(InterchangeHeader))
    interchange_trailer = property(lambda self: self._get_segment(InterchangeTrailer))
    message_header = property(lambda self: self._get_segment(MessageHeader))
    message_trailer = property(lambda self: self._get_segment(MessageTrailer))
    beginning_of_message = property(lambda self: self._get_segment(BeginningOfMessage))
    name_and_address = property(lambda self: self._get_segment(NameAndAddress))
    date_time_period = property(lambda self: self._get_segment(DateTimePeriod))
    reference_transaction_type = property(lambda self: self._get_segment(ReferenceTransactionType))
    reference_transaction_number = property(lambda self: self._get_segment(ReferenceTransactionNumber))
    segment_group = property(lambda self: self._get_segment(SegmentGroup))
    reference_interchange_recep = property(lambda self: self._get_segment(ReferenceInterchangeRecep))
    reference_message_recep = property(lambda self: self._get_multiple_segments(ReferenceMessageRecep))

    def __init__(self, segments: List[Segment]) -> None:
        self._segments = segments

    def _get_segment(self, segment_type: Type) -> Segment:
        segments = self._get_multiple_segments(segment_type)
        if len(segments) != 1:
            raise LookupError(f"EDIFACT message contains more than 1 expected segment type '{segment_type}'")
        return segments[0]

    def _get_multiple_segments(self, segment_type: Type) -> List[Segment]:
        filtered_segments = list(filter(lambda s: type(s) is segment_type, self._segments))
        if len(filtered_segments) == 0:
            raise LookupError(f"EDIFACT message does not contain segment type '{segment_type}'")
        return filtered_segments
