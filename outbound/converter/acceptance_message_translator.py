from fhir.resources.patient import Patient

from edifact.models.message import ReferenceTransactionType
from outbound.converter.base_message_translator import BaseMessageTranslator


class AcceptanceMessageTranslator(BaseMessageTranslator):
    def _append_transaction_type(self):
        self.segments.append(ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE))

    def _append_message_body(self, patient: Patient):
        pass