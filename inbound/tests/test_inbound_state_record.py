import unittest
from datetime import datetime
import os

from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader
from edifact.models.message import MessageHeader, ReferenceTransactionType, ReferenceTransactionNumber, DateTimePeriod
from inbound.inbound_state_handler import InboundStateRecord


class TestInboundStateRecord(unittest.TestCase):
    inbound_state_record = InboundStateRecord(
        interchange_id="some_interchange_id",
        message_id="some_message_id",
        sender="some_sender",
        recipient="some_recipient",
        transaction_id="some_transaction_id",
        transaction_type='G1',
        translation_timestamp=datetime.fromtimestamp(1284286795)
    )
    inbound_state_as_dictionary = {
        'INTERCHANGE_ID': 'some_interchange_id',
        'MESSAGE_ID': 'some_message_id',
        'SENDER': 'some_sender',
        'RECIPIENT': 'some_recipient',
        'TRANSACTION_ID': 'some_transaction_id',
        'TRANSACTION_TYPE': 'G1',
        'TRANSLATION_TIMESTAMP': '2010-09-12T10:19:55'
    }
    edifact = Edifact([
        InterchangeHeader(
            sender="some_sender",
            recipient="some_recipient",
            date_time=datetime.fromtimestamp(1284286795),
            sequence_number="some_interchange_id"),
        MessageHeader(sequence_number="some_message_id"),
        ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE),
        ReferenceTransactionNumber(reference='some_transaction_id'),
        DateTimePeriod(
            qualifier_and_code=DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP,
            timestamp=datetime.fromtimestamp(1284286795))
    ])
    key = "1cd5025637968ae969238c0b33174387f97455d7772d2b35996dc28e5edaf2ad"

    def setUp(self) -> None:
        os.environ['TZ'] = 'GMT'

    def tearDown(self) -> None:
        del os.environ['TZ']

    def test_to_dict(self):
        self.assertEqual(self.inbound_state_record.to_dict(), self.inbound_state_as_dictionary)

    def test_build_key(self):
        self.assertEqual(self.inbound_state_record.build_key(), self.key)

    def test_from_edifact(self):
        self.assertEqual(InboundStateRecord.from_edifact(self.edifact), self.inbound_state_record)

    def test_from_dict(self):
        self.assertEqual(InboundStateRecord.from_dict(self.inbound_state_as_dictionary), self.inbound_state_record)
