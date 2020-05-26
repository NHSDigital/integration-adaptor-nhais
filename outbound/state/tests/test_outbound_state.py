import datetime
import unittest

from edifact.models.interchange import InterchangeHeader
from edifact.models.message import MessageHeader, ReferenceTransactionNumber, ReferenceTransactionType
from outbound.state import outbound_state as state

OPERATION_ID = 'GD12JD-3H3D-SK2W-JDJS-CCNCJJ3RTS7E'


class TestOutboundState(unittest.TestCase):

    def verify_results_for_input_data(self, outbound_state):
        self.assertEqual(outbound_state.transaction_id, 1)
        self.assertEqual(outbound_state.transaction_timestamp, datetime.datetime(2020, 5, 17, 0, 0))
        self.assertEqual(outbound_state.transaction_type, 'G1')
        self.assertEqual(outbound_state.sis_sequence, 2)
        self.assertEqual(outbound_state.sms_sequences, [3, 7])
        self.assertEqual(outbound_state.sender, 'test_sender')
        self.assertEqual(outbound_state.recipient, 'test_recipient')

    def test_constructor(self):
        input_data = {
            state.OPERATION_ID: 'aaa-bbb-ccc',
            state.TRANSACTION_ID: 1,
            state.TRANSACTION_TIMESTAMP: datetime.datetime(2020, 5, 17, 0, 0),
            state.TRANSACTION_TYPE: 'G1',
            state.SIS_SEQUENCE: 2,
            state.SMS_SEQUENCES: [3, 7],
            state.SENDER: 'test_sender',
            state.RECIPIENT: 'test_recipient'
        }

        outbound_state = state.OutboundState(input_data)

        self.assertEqual(outbound_state.operation_id, 'aaa-bbb-ccc')
        self.verify_results_for_input_data(outbound_state)

    def test_create_outbound_state(self):
        segments = [InterchangeHeader(sender='test_sender',
                                      recipient='test_recipient',
                                      date_time=datetime.datetime(2020, 5, 17),
                                      sequence_number=2),
                    MessageHeader(sequence_number=3),
                    MessageHeader(sequence_number=7),
                    ReferenceTransactionNumber(reference=1),
                    ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
        outbound_state = state.create_new_outbound_state(segments, OPERATION_ID)

        self.verify_results_for_input_data(outbound_state)

    def test_create_state_none_or_empty_parameters(self):
        with self.subTest('None transaction_id'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=None),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('None timestamp'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time=None,
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('None transaction_type'):
            with self.assertRaises(AttributeError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(None)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('Not existing transaction_type'):
            with self.assertRaises(AttributeError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.NOT_EXISTING)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('None sis sequence'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=None),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('Empty sms sequences list'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            ReferenceTransactionNumber(reference='1'),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('None sender'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender=None,
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('Empty sender string'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='',
                                              recipient='test_recipient',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('None recipient'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient=None,
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
        with self.subTest('Empty recipient string'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='',
                                              date_time=datetime.datetime(2020, 5, 17),
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                state.create_new_outbound_state(segments, OPERATION_ID)
