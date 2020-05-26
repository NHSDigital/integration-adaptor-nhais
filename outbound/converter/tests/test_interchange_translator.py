import re
import unittest
from datetime import datetime, timezone
from unittest import mock

from utilities.date_utilities import DateUtilities
from utilities.test_utilities import async_test, awaitable

import outbound.state.outbound_state
from edifact.patterns import UNB_PATTERN, UNH_PATTERN, BGM_PATTERN, NAD_MSG_HEADER_PATTERN, DTM_MSG_HEADER_PATTERN, \
    SG_PATTERN, RFF_TN_PATTERN, UNT_PATTERN, UNZ_PATTERN, RFF_PATTERN
import sequence.outbound.sequence_manager
from edifact.models.message import ReferenceTransactionType
from outbound.converter.interchange_translator import InterchangeTranslator
from outbound.tests.fhir_test_helpers import create_patient, HA_ID, GP_ID
import sequence.outbound.sequence_manager


class TestFhirToEdifactTranslator(unittest.TestCase):

    @mock.patch.object(outbound.state.outbound_state.OutboundState, 'save_as_new')
    @mock.patch.object(sequence.outbound.sequence_manager.OutboundSequenceNumberManager, 'generate_message_id')
    @mock.patch.object(sequence.outbound.sequence_manager.OutboundSequenceNumberManager, 'generate_interchange_id')
    @mock.patch.object(sequence.outbound.sequence_manager.OutboundSequenceNumberManager, 'generate_transaction_id')
    @mock.patch('utilities.date_utilities.DateUtilities.utc_now')
    @async_test
    async def test_message_translated(self, mock_utc_now, mock_generate_transaction_id, mock_generate_interchange_id,
                                      mock_generate_message_id, mock_publish):
        expected_date = datetime(year=2020, month=4, day=27, hour=17, minute=37)
        mock_utc_now.return_value = expected_date
        mock_generate_transaction_id.return_value = awaitable(5174)
        mock_generate_interchange_id.return_value = awaitable(45)
        mock_generate_message_id.return_value = awaitable(56)
        mock_publish.return_value = awaitable(4)
        self.assertEqual(expected_date, DateUtilities.utc_now())
        patient = create_patient()

        translator = InterchangeTranslator()
        operation_id = message_utilities.get_uuid()
        edifact = await translator.convert(patient, ReferenceTransactionType.TransactionType.ACCEPTANCE, operation_id)

        self.assertIsNotNone(edifact)
        self.assertTrue(len(edifact) > 0)
        segments = edifact.splitlines()

        unz = segments.pop()
        self.assertRegex(unz, UNZ_PATTERN)
        unz_match = re.match(UNZ_PATTERN, unz)
        self.assertEqual('1', unz_match.group('message_count'))
        self.assertEqual('00000045', unz_match.group('sis'))

        unt = segments.pop()
        self.assertRegex(unt, UNT_PATTERN)
        unt_match = re.match(UNT_PATTERN, unt)
        self.assertEqual('8', unt_match.group('segment_count'))
        self.assertEqual('00000056', unt_match.group('sms'))

        rff_tn = segments.pop()
        self.assertRegex(rff_tn, RFF_TN_PATTERN)
        rff_tn_match = re.match(RFF_TN_PATTERN, rff_tn)
        self.assertEqual('5174', rff_tn_match.group('transaction_number'))

        s01 = segments.pop()
        self.assertRegex(s01, SG_PATTERN)
        sg_match = re.match(SG_PATTERN, s01)
        assert int(sg_match.group('segment_group_number_0')) == int(sg_match.group('segment_group_number'))

        rff = segments.pop()
        self.assertRegex(rff, RFF_PATTERN)
        rff_match = re.match(RFF_PATTERN, rff)
        self.assertEqual('G1', rff_match.group('transaction_type'))

        dtm_msg_header = segments.pop()
        self.assertRegex(dtm_msg_header, DTM_MSG_HEADER_PATTERN)
        dtm_msg_header_match = re.match(DTM_MSG_HEADER_PATTERN, dtm_msg_header)
        self.assertEqual('137', dtm_msg_header_match.group('type_code'))
        self.assertEqual('202004271737', dtm_msg_header_match.group('date_time_value'))
        self.assertEqual('203', dtm_msg_header_match.group('format_code'))

        nad_msg_header = segments.pop()
        self.assertRegex(nad_msg_header, NAD_MSG_HEADER_PATTERN)
        nad_msg_header_match = re.match(NAD_MSG_HEADER_PATTERN, nad_msg_header)
        self.assertEqual('FHS', nad_msg_header_match.group('party_qualifier'))
        self.assertEqual(HA_ID, nad_msg_header_match.group('party_id'))
        self.assertEqual('954', nad_msg_header_match.group('party_code'))

        bgm = segments.pop()
        self.assertRegex(bgm, BGM_PATTERN)

        unh = segments.pop()
        self.assertRegex(unh, UNH_PATTERN)
        unh_match = re.match(UNH_PATTERN, unh)
        self.assertEqual('00000056', unh_match.group('sms'))

        unb = segments.pop()
        self.assertRegex(unb, UNB_PATTERN)
        unb_match = re.match(UNB_PATTERN, unb)
        self.assertEqual(GP_ID, unb_match.group('sender'))
        self.assertEqual(HA_ID, unb_match.group('recipient'))
        self.assertEqual('200427:1737', unb_match.group('timestamp'))
        self.assertEqual('00000045', unb_match.group('sis'))
