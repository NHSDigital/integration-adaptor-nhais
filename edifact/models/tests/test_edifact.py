import unittest
from datetime import datetime

from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, DateTimePeriod, \
    ReferenceTransactionType, ReferenceTransactionNumber, SegmentGroup
from edifact.models.tests.segments_comparison_util import SegmentComparisonTest

SEGMENT_COMPARISON = SegmentComparisonTest()

EDI_FILE = """UNB+UNOA:2+GP123+HA456+200427:1737+00000045'
UNH+00000056+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+HA456:954'
DTM+137:202004271737:203'
RFF+950:G1'
S01+1'
RFF+TN:5174'
UNT+8+00000056'
UNZ+1+00000045'"""

interchange_header = InterchangeHeader('GP123', 'HA456', datetime(2020, 4, 27, 17, 37), 45)
interchange_trailer = InterchangeTrailer(1, 45)
message_header = MessageHeader(56)
message_trailer = MessageTrailer(8, 56)
beginning_of_message = BeginningOfMessage()
name_and_address = NameAndAddress(NameAndAddress.QualifierAndCode.FHS, 'HA456')
date_time_period = DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP, datetime(2020, 4, 27, 17, 37))
reference_transaction_type = ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)
reference_transaction_number = ReferenceTransactionNumber(5174)
segment_group = SegmentGroup(1)


class TestEdifact(unittest.TestCase):

    def test_create_from_message(self):
        edifact = Edifact.create_edifact_from_message(EDI_FILE)
        # TODO: validate parsed segments

        SEGMENT_COMPARISON.compare_interchange_header(edifact.interchange_header, interchange_header)
        SEGMENT_COMPARISON.compare_interchange_trailer(edifact.interchange_trailer, interchange_trailer)
        SEGMENT_COMPARISON.compare_message_header(edifact.message_header, message_header)
        SEGMENT_COMPARISON.compare_message_trailer(edifact.message_trailer, message_trailer)
        SEGMENT_COMPARISON.compare_name_and_address(edifact.name_and_address, name_and_address)
        SEGMENT_COMPARISON.compare_date_time_period(edifact.date_time_period, date_time_period)
        SEGMENT_COMPARISON.compare_reference_transaction_type(edifact.reference_transaction_type, reference_transaction_type)
        SEGMENT_COMPARISON.compare_reference_transaction_number(edifact.reference_transaction_number, reference_transaction_number)

    def test_missing_segment_raises_error(self):
        edifact = Edifact([])
        self.assertRaises(LookupError, lambda: edifact.interchange_header)
        self.assertRaises(LookupError, lambda: edifact.interchange_trailer)
        self.assertRaises(LookupError, lambda: edifact.message_header)
        self.assertRaises(LookupError, lambda: edifact.message_trailer)
        self.assertRaises(LookupError, lambda: edifact.beginning_of_message)
        self.assertRaises(LookupError, lambda: edifact.name_and_address)
        self.assertRaises(LookupError, lambda: edifact.date_time_period)
        self.assertRaises(LookupError, lambda: edifact.reference_transaction_type)
        self.assertRaises(LookupError, lambda: edifact.reference_transaction_number)
        self.assertRaises(LookupError, lambda: edifact.segment_group)
