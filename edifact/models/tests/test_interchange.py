import unittest
from datetime import datetime

from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.segment import Segment
from edifact.models.tests.base_segment_test_helper import BaseSegmentTestHelper
from edifact.models.tests.segments_comparison_util import SegmentComparisonUtil

SEGMENT_COMPARISON = SegmentComparisonUtil()


class TestHelperInterchangeHeader(BaseSegmentTestHelper, unittest.TestCase):
    TS = datetime(year=2019, month=4, day=23, hour=9, minute=0)

    def _create_segment(self) -> Segment:
        return InterchangeHeader(sender="SNDR", recipient="RECP", date_time=self.TS, sequence_number=1)

    def _create_segment_from_string(self) -> Segment:
        interchange_header_segment = "UNB+UNOA:2+SNDR+RECP+190423:0900+00000001'"
        return InterchangeHeader.from_string(interchange_header_segment)

    def _get_attributes(self):
        return ['sender', 'recipient', 'date_time', 'sequence_number']

    def _get_expected_edifact(self):
        return "UNB+UNOA:2+SNDR+RECP+190423:0900+00000001'"

    def _compare_segments(self, expected_segment: InterchangeHeader, actual_segment: InterchangeHeader):
        SEGMENT_COMPARISON.compare_interchange_header(expected_segment, actual_segment)


class TestHelperInterchangeTrailer(BaseSegmentTestHelper, unittest.TestCase):

    def _create_segment(self) -> Segment:
        return InterchangeTrailer(number_of_messages=1, sequence_number=1)

    def _create_segment_from_string(self) -> Segment:
        interchange_trailer_segment = "UNZ+1+00000001'"
        return InterchangeTrailer.from_string(interchange_trailer_segment)

    def _get_attributes(self):
        return ['number_of_messages', 'sequence_number']

    def _get_expected_edifact(self):
        return "UNZ+1+00000001'"

    def _compare_segments(self, expected_segment: InterchangeTrailer, actual_segment: InterchangeTrailer):
        SEGMENT_COMPARISON.compare_interchange_trailer(expected_segment, actual_segment)
