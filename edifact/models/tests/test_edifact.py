import unittest

from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, DateTimePeriod, \
    ReferenceTransactionType, ReferenceTransactionNumber, SegmentGroup

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

interchange_header = InterchangeHeader(None, None, None, None)
interchange_trailer = InterchangeTrailer(None, None)
message_header = MessageHeader(None)
message_trailer = MessageTrailer(None, None)
beginning_of_message = BeginningOfMessage()
name_and_address = NameAndAddress(NameAndAddress.QualifierAndCode.FHS, None)
date_time_period = DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP, None)
reference_transaction_type = ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)
reference_transaction_number = ReferenceTransactionNumber(None)
segment_group = SegmentGroup(None)

class TestInterchangeHeader(unittest.TestCase):

    def test_create_from_message(self):
        edifact = Edifact.create_edifact_from_message(EDI_FILE)
        # TODO: validate parsed segments
        self.assertEqual(edifact.interchange_header, interchange_header)
        self.assertEqual(edifact.interchange_trailer, interchange_trailer)
        self.assertEqual(edifact.message_header, message_header)
        self.assertEqual(edifact.message_trailer, message_trailer)
        self.assertEqual(edifact.beginning_of_message, beginning_of_message)
        self.assertEqual(edifact.name_and_address, name_and_address)
        self.assertEqual(edifact.date_time_period, date_time_period)
        self.assertEqual(edifact.reference_transaction_type, reference_transaction_type)
        self.assertEqual(edifact.reference_transaction_number, reference_transaction_number)
        self.assertEqual(edifact.segment_group, segment_group)

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
