import unittest
from datetime import datetime, timezone

from edifact.edifact_exception import EdifactValidationException
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, \
    DateTimePeriod, ReferenceTransactionType, ReferenceTransactionNumber, SegmentGroup
from edifact.models.segment import Segment
from edifact.models.tests.base_segment_test import BaseSegmentTest


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

    def _compare_segments(self, expected_segment: MessageHeader, actual_segment: MessageHeader) -> bool:
        self.assertEqual(expected_segment.sequence_number, actual_segment.sequence_number)


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

    def _compare_segments(self, expected_segment: MessageTrailer, actual_segment: MessageTrailer) -> bool:
        self.assertEqual(expected_segment.sequence_number, actual_segment.sequence_number)
        self.assertEqual(expected_segment.number_of_segments, actual_segment.number_of_segments)


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

    def _compare_segments(self, expected_segment: NameAndAddress, actual_segment: NameAndAddress) -> bool:
        self.assertEqual(expected_segment.qualifier, actual_segment.qualifier)
        self.assertEqual(expected_segment.code, actual_segment.code)
        self.assertEqual(expected_segment.identifier, actual_segment.identifier)


class TestDateTimePeriod(BaseSegmentTest, unittest.TestCase):

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

    def _compare_segments(self, expected_segment: DateTimePeriod, actual_segment: DateTimePeriod) -> bool:
        self.assertEqual(expected_segment.timestamp, actual_segment.timestamp)
        self.assertEqual(expected_segment.format_code, actual_segment.format_code)
        self.assertEqual(expected_segment.type_code, actual_segment.type_code)
        self.assertEqual(expected_segment.date_time_format, actual_segment.date_time_format)


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

    def _compare_segments(self, expected_segment: ReferenceTransactionType, actual_segment: ReferenceTransactionType) -> bool:
        self.assertEqual(expected_segment.qualifier, actual_segment.qualifier)
        self.assertEqual(expected_segment.reference, actual_segment.reference)


class TestReferenceTransactionNumber(BaseSegmentTest, unittest.TestCase):
    def _create_segment(self) -> Segment:
        return ReferenceTransactionNumber('1234')

    def _create_segment_from_string(self) -> Segment:
        transaction_number_segment = "RFF+TN:1234'"
        return ReferenceTransactionNumber.from_string(transaction_number_segment)

    def _get_attributes(self):
        return ['qualifier', 'reference']

    def _get_expected_edifact(self):
        return "RFF+TN:1234'"

    def _compare_segments(self, expected_segment: ReferenceTransactionNumber, actual_segment: ReferenceTransactionNumber) -> bool:
        self.assertEqual(expected_segment.reference, actual_segment.reference)
        self.assertEqual(expected_segment.qualifier, actual_segment.qualifier)


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
