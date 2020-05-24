import unittest
from datetime import datetime, timezone

from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.models.message import MessageHeader, MessageTrailer, BeginningOfMessage, NameAndAddress, \
    DateTimePeriod, ReferenceTransactionNumber, ReferenceTransactionType, SegmentGroup
from inbound.converter.edifact_to_fhir import EdifactToFhir

SENDER = 'test_any_ha'
RECEIVER = 'test_any_gp'
INTERCHANGE_ID = 4
MESSAGE_ID = 5
NUMBER_OF_MESSAGES = 1
NUMBER_OF_SEGMENTS = 1


class TestEdifactToFhir(unittest.TestCase):

    def test_edifact_to_fhir_translation(self):
        edifact = create_edifact()
        edifact_to_fhir = EdifactToFhir()
        patient = edifact_to_fhir.convert_edifact_to_fhir(edifact)
        self.assertEqual(patient.managingOrganization.identifier.value, SENDER)
        self.assertEqual(patient.generalPractitioner[0].identifier.value, RECEIVER)
        self.assertEqual(edifact_to_fhir.interchange_id, INTERCHANGE_ID)
        self.assertEqual(edifact_to_fhir.message_ids[0], MESSAGE_ID)
        self.assertEqual(edifact_to_fhir.number_of_messages, NUMBER_OF_MESSAGES)
        self.assertEqual(edifact_to_fhir.number_of_segments, NUMBER_OF_SEGMENTS)


def create_edifact():
    timestamp = datetime(year=2020, month=4, day=27, hour=17, minute=37, tzinfo=timezone.utc)
    segment_group_number = 1
    segments = [InterchangeHeader(SENDER, RECEIVER, timestamp, INTERCHANGE_ID),
                InterchangeTrailer(NUMBER_OF_MESSAGES, INTERCHANGE_ID),
                MessageHeader(MESSAGE_ID),
                MessageTrailer(NUMBER_OF_SEGMENTS, MESSAGE_ID), BeginningOfMessage(),
                NameAndAddress(NameAndAddress.QualifierAndCode.FHS, 'test_party_identifier'),
                DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP, timestamp),
                ReferenceTransactionNumber(None),
                ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE),
                SegmentGroup(segment_group_number)]
    return Edifact(segments)
