import unittest

from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, NameAndAddress, DateTimePeriod, \
    ReferenceTransactionType, ReferenceTransactionNumber


class SegmentComparisonUtil(unittest.TestCase):

    def compare_interchange_header(self, expected_segment: InterchangeHeader,
                                   actual_segment: InterchangeHeader):
        self.assertEqual(expected_segment.sender, actual_segment.sender)
        self.assertEqual(expected_segment.sequence_number, actual_segment.sequence_number)
        self.assertEqual(expected_segment.TIMESTAMP_FORMAT, actual_segment.TIMESTAMP_FORMAT)
        self.assertEqual(expected_segment.date_time, actual_segment.date_time)
        self.assertEqual(expected_segment.recipient, actual_segment.recipient)

    def compare_interchange_trailer(self, expected_segment: InterchangeTrailer,
                                    actual_segment: InterchangeTrailer):
        self.assertEqual(expected_segment.sequence_number, actual_segment.sequence_number)
        self.assertEqual(expected_segment.number_of_messages, actual_segment.number_of_messages)

    def compare_message_header(self, expected_segment: MessageHeader, actual_segment: MessageHeader):
        self.assertEqual(expected_segment.sequence_number, actual_segment.sequence_number)

    def compare_message_trailer(self, expected_segment: MessageTrailer, actual_segment: MessageTrailer):
        self.assertEqual(expected_segment.sequence_number, actual_segment.sequence_number)
        self.assertEqual(expected_segment.number_of_segments, actual_segment.number_of_segments)

    def compare_name_and_address(self, expected_segment: NameAndAddress, actual_segment: NameAndAddress):
        self.assertEqual(expected_segment.qualifier, actual_segment.qualifier)
        self.assertEqual(expected_segment.code, actual_segment.code)
        self.assertEqual(expected_segment.identifier, actual_segment.identifier)

    def compare_date_time_period(self, expected_segment: DateTimePeriod, actual_segment: DateTimePeriod):
        self.assertEqual(expected_segment.timestamp, actual_segment.timestamp)
        self.assertEqual(expected_segment.format_code, actual_segment.format_code)
        self.assertEqual(expected_segment.type_code, actual_segment.type_code)
        self.assertEqual(expected_segment.date_time_format, actual_segment.date_time_format)

    def compare_reference_transaction_type(self, expected_segment: ReferenceTransactionType,
                                           actual_segment: ReferenceTransactionType):
        self.assertEqual(expected_segment.qualifier, actual_segment.qualifier)
        self.assertEqual(expected_segment.reference, actual_segment.reference)

    def compare_reference_transaction_number(self, expected_segment: ReferenceTransactionNumber,
                                             actual_segment: ReferenceTransactionNumber):
        self.assertEqual(expected_segment.reference, actual_segment.reference)
        self.assertEqual(expected_segment.qualifier, actual_segment.qualifier)
