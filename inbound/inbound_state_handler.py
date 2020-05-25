import hashlib
from dataclasses import dataclass
from datetime import datetime

from persistence.persistence_adaptor import PersistenceAdaptor

from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader
from edifact.models.message import MessageHeader, ReferenceTransactionType, DateTimePeriod, ReferenceTransactionNumber

INTERCHANGE_ID = 'INTERCHANGE_ID'
MESSAGE_ID = 'MESSAGE_ID'
SENDER = 'SENDER'
RECIPIENT = 'RECIPIENT'
TRANSACTION_ID = 'TRANSACTION_ID'
TRANSACTION_TYPE = 'TRANSACTION_TYPE'
TRANSLATION_TIMESTAMP = 'TRANSLATION_TIMESTAMP'

DATE_FORMAT = '%Y-%m-%dT%H:%M:%S'

@dataclass
class InboundStateRecord:
    interchange_id: str
    message_id: str
    sender: str
    recipient: str
    transaction_id: str
    transaction_type: str
    translation_timestamp: datetime

    def to_dict(self):
        return {
            INTERCHANGE_ID: self.interchange_id,
            MESSAGE_ID: self.message_id,
            SENDER: self.sender,
            RECIPIENT: self.recipient,
            TRANSACTION_ID: self.transaction_id,
            TRANSACTION_TYPE: self.transaction_type,
            TRANSLATION_TIMESTAMP: self.translation_timestamp.strftime(DATE_FORMAT)
        }

    def build_key(self):
        bare_key = '_'.join([
            self.interchange_id,
            self.message_id,
            self.sender,
            self.recipient])
        return hashlib.sha256(bare_key.encode()).hexdigest()

    @staticmethod
    def from_edifact(edifact: Edifact):
        interchange_header: InterchangeHeader = edifact.interchange_header
        message_header: MessageHeader = edifact.message_header
        reference_transaction_type: ReferenceTransactionType = edifact.reference_transaction_type
        reference_transaction_number: ReferenceTransactionNumber = edifact.reference_transaction_number
        date_time_period: DateTimePeriod = edifact.date_time_period

        return InboundStateRecord(
            interchange_id=interchange_header.sequence_number,
            message_id=message_header.sequence_number,
            sender=interchange_header.sender,
            recipient=interchange_header.recipient,
            transaction_id=reference_transaction_number.reference,
            transaction_type=reference_transaction_type.reference,
            translation_timestamp=date_time_period.timestamp,
        )

    @staticmethod
    def from_dict(d: dict):
        return InboundStateRecord(
            interchange_id=d[INTERCHANGE_ID],
            message_id=d[MESSAGE_ID],
            sender=d[SENDER],
            recipient=d[RECIPIENT],
            transaction_id=d[TRANSACTION_ID],
            transaction_type=d[TRANSACTION_TYPE],
            translation_timestamp=datetime.strptime(d[TRANSLATION_TIMESTAMP], DATE_FORMAT)
        )


class InboundStateHandler:

    def __init__(self, inbound_state: InboundStateRecord, persistence_adaptor: PersistenceAdaptor) -> None:
        super().__init__()
        self.inbound_state = inbound_state
        self.persistence_adaptor = persistence_adaptor

    async def save_as_new(self) -> None:
        """
        Saves inbound state as new record

        :raises: DuplicatePrimaryKeyError exception if key already exists
        :return: None
        """
        # can raise DuplicatePrimaryKeyError
        await self.persistence_adaptor.add(self.inbound_state.build_key(), self.inbound_state.to_dict())
