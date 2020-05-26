import unittest
from datetime import datetime, timezone
from edifact.edifact_exception import EdifactValidationException
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, \
    DateTimePeriod, ReferenceTransactionType, ReferenceTransactionNumber, SegmentGroup
from edifact.models.segment import Segment
from edifact.models.tests.base_segment_test import BaseSegmentTest
from edifact.models.tests.segments_comparison_util import SegmentComparisonTest

SEGMENT_COMPARISON = SegmentComparisonTest()


class TestMessageHeader(BaseSegmentTest, unittest.TestCase):
    """
    Test the generating of a message header
    """
    def _create_segment(self) -> Segment:
        return MessageHeader(sequence_number=1)

    def _create_segment_from_string(self) -> Segment:
        message_header_segment = "UNH+00000001+FHSREG:0:1:FH:FHS001'"
        return MessageHeader.from_string(message_header_segment)

    def _get_attributes(self):
        return ['sequence_number']

    def _get_expected_edifact(self):
        return "UNH+00000001+FHSREG:0:1:FH:FHS001'"

    def _compare_segments(self, expected_segment: MessageHeader, actual_segment: MessageHeader):
        SEGMENT_COMPARISON.compare_message_header(expected_segment, actual_segment)


class TestMessageTrailer(BaseSegmentTest, unittest.TestCase):
    """
    Test the generating of a message trailer
    """
    def _create_segment(self) -> Segment:
        return MessageTrailer(number_of_segments=5, sequence_number=1)

    def _create_segment_from_string(self) -> Segment:
        message_trailer_segment = "UNT+5+00000001'"
        return MessageTrailer.from_string(message_trailer_segment)

    def _get_attributes(self):
        return ['number_of_segments', 'sequence_number']

    def _get_expected_edifact(self):
        return "UNT+5+00000001'"

    def _compare_segments(self, expected_segment: MessageTrailer, actual_segment: MessageTrailer):
        SEGMENT_COMPARISON.compare_message_trailer(expected_segment, actual_segment)


class TestBeginningOfMessage(unittest.TestCase):

    def test_to_edifact(self):
        self.assertEqual("BGM+++507'", BeginningOfMessage().to_edifact_string())


class TestNameAndAddress(BaseSegmentTest, unittest.TestCase):

    def _create_segment(self) -> Segment:
        return NameAndAddress(NameAndAddress.QualifierAndCode.FHS, 'PARTY')

    def _create_segment_from_string(self) -> Segment:
        name_and_address_segment = "NAD+FHS+PARTY:954'"
        return NameAndAddress.from_string(name_and_address_segment)

    def _get_attributes(self):
        return ['qualifier', 'code', 'identifier']

    def _get_expected_edifact(self):
        return "NAD+FHS+PARTY:954'"

    def _compare_segments(self, expected_segment: NameAndAddress, actual_segment: NameAndAddress):
        SEGMENT_COMPARISON.compare_name_and_address(expected_segment, actual_segment)


class TestDateTimePeriod(BaseSegmentTest, unittest.TestCase):

    TS = datetime(year=2020, month=4, day=28, hour=20, minute=58, tzinfo=timezone.utc)
    TS = datetime(year=2020, month=4, day=28, hour=20, minute=58)

    def _create_segment(self) -> Segment:
        return DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP, self.TS)

    def _create_segment_from_string(self) -> Segment:
        time_period_segment = "DTM+137:202004282058:203'"
        return DateTimePeriod.from_string(time_period_segment)

    def _get_attributes(self):
        return ['type_code', 'format_code', 'date_time_format', 'timestamp']

    def _get_expected_edifact(self):
        return "DTM+137:202004282058:203'"

    def _compare_segments(self, expected_segment: DateTimePeriod, actual_segment: DateTimePeriod):
        SEGMENT_COMPARISON.compare_date_time_period(expected_segment, actual_segment)


class TestReferenceTransactionType(BaseSegmentTest, unittest.TestCase):
    def _create_segment(self) -> Segment:
        return ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)

    def _create_segment_from_string(self) -> Segment:
        transaction_type_segment = "RFF+950:G1'"
        return ReferenceTransactionType.from_string(transaction_type_segment)

    def _get_attributes(self):
        return ['qualifier', 'reference']

    def _get_expected_edifact(self):
        return "RFF+950:G1'"

    def _compare_segments(self, expected_segment: ReferenceTransactionType, actual_segment: ReferenceTransactionType):
        SEGMENT_COMPARISON.compare_reference_transaction_type(expected_segment, actual_segment)


class TestReferenceTransactionNumber(BaseSegmentTest, unittest.TestCase):
    def _create_segment(self) -> Segment:
        return ReferenceTransactionNumber(1234)

    def _create_segment_from_string(self) -> Segment:
        transaction_number_segment = "RFF+TN:1234'"
        return ReferenceTransactionNumber.from_string(transaction_number_segment)

    def _get_attributes(self):
        return ['qualifier', 'reference']

    def _get_expected_edifact(self):
        return "RFF+TN:1234'"

    def _compare_segments(self, expected_segment: ReferenceTransactionNumber, actual_segment: ReferenceTransactionNumber) -> bool:
        SEGMENT_COMPARISON.compare_reference_transaction_number(expected_segment, actual_segment)


class TestSegmentGroup(unittest.TestCase):

    def test_to_edifact(self):
        self.assertEqual("S01+1'", SegmentGroup(1).to_edifact_string())
        self.assertEqual("S02+2'", SegmentGroup(2).to_edifact_string())

    def test_missing_segment_group_number(self):
        sg = SegmentGroup(1)
        sg.segment_group_number = None
        with self.assertRaises(EdifactValidationException, msg=f'missing "segment_group_number" did not fail validation') as ctx:
            sg.to_edifact_string()
        self.assertEqual(f'S: Attribute segment_group_number is required', ctx.exception.args[0])

    def test_segment_group_number_is_not_integer(self):
        sg = SegmentGroup('1')
        with self.assertRaises(EdifactValidationException, msg=f'missing "segment_group_number" did not fail validation') as ctx:
            sg.to_edifact_string()
        self.assertEqual(f'S: Attribute segment_group_number must be an integer', ctx.exception.args[0])

    def test_segment_group_number_is_out_of_range(self):
        sg = SegmentGroup(3)
        with self.assertRaises(EdifactValidationException, msg=f'missing "segment_group_number" did not fail validation') as ctx:
            sg.to_edifact_string()
        self.assertEqual(f'S: Attribute segment_group_number must be 1 or 2', ctx.exception.args[0])