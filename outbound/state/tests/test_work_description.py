import unittest
from unittest.mock import MagicMock

from edifact.outgoing.models.interchange import InterchangeHeader
from edifact.outgoing.models.message import MessageHeader, ReferenceTransactionNumber, ReferenceTransactionType
from outbound.state import outbound_state as wd


class TestWorkDescription(unittest.TestCase):

    def verify_results_for_input_data(self, outbound_state):
        self.assertEqual(outbound_state.transaction_id, 1)
        self.assertEqual(outbound_state.transaction_timestamp, '12:00')
        self.assertEqual(outbound_state.transaction_type, 'G1')
        self.assertEqual(outbound_state.sis_sequence, 2)
        self.assertEqual(outbound_state.sms_sequences, [3, 7])
        self.assertEqual(outbound_state.sender, 'test_sender')
        self.assertEqual(outbound_state.recipient, 'test_recipient')

    def test_constructor(self):
        input_data = {
            wd.OPERATION_ID: 'aaa-bbb-ccc',
            wd.DATA: {
                wd.TRANSACTION_ID: 1,
                wd.TRANSACTION_TIMESTAMP: '12:00',
                wd.TRANSACTION_TYPE: 'G1',
                wd.SIS_SEQUENCE: 2,
                wd.SMS_SEQUENCES: [3, 7],
                wd.SENDER: 'test_sender',
                wd.RECIPIENT: 'test_recipient'
            }
        }

        persistence = MagicMock()
        outbound_state = wd.OutboundState(persistence, input_data)

        self.assertEqual(outbound_state.operation_id, 'aaa-bbb-ccc')
        self.verify_results_for_input_data(outbound_state)

    def test_create_outbound_state(self):
        persistence = MagicMock()
        segments = [InterchangeHeader(sender='test_sender',
                                      recipient='test_recipient',
                                      date_time='12:00',
                                      sequence_number=2),
                    MessageHeader(sequence_number=3),
                    MessageHeader(sequence_number=7),
                    ReferenceTransactionNumber(reference=1),
                    ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
        outbound_state = wd.create_new_outbound_state(persistence, segments)

        self.verify_results_for_input_data(outbound_state)

    def test_create_wd_none_or_empty_parameters(self):
        persistence = MagicMock()
        with self.subTest('None transaction_id'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=None),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
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
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('Empty timestamp string'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time='',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('None transaction_type'):
            with self.assertRaises(AttributeError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(None)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('Not existing transaction_type'):
            with self.assertRaises(AttributeError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.NOT_EXISTING)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('None sis sequence'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=None),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('Empty sms sequences list'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=2),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('None sender'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender=None,
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('Empty sender string'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='',
                                              recipient='test_recipient',
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('None recipient'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient=None,
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )
        with self.subTest('Empty recipient string'):
            with self.assertRaises(ValueError):
                segments = [InterchangeHeader(sender='test_sender',
                                              recipient='',
                                              date_time='12:00',
                                              sequence_number=2),
                            MessageHeader(sequence_number=3),
                            MessageHeader(sequence_number=7),
                            ReferenceTransactionNumber(reference=1),
                            ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE)]
                wd.create_new_outbound_state(
                    persistence,
                    segments
                )