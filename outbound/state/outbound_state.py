import hashlib

import utilities.integration_adaptors_logger as log
from persistence.persistence_adaptor_factory import get_persistence_adaptor

from edifact.models.interchange import InterchangeHeader
from edifact.models.message import MessageHeader, ReferenceTransactionNumber, \
    ReferenceTransactionType

TRANSACTION_ID = 'TRANSACTION_ID'
TRANSACTION_TIMESTAMP = 'TRANSACTION_TIMESTAMP'
TRANSACTION_TYPE = 'TRANSACTION_TYPE'
SIS_SEQUENCE = 'SIS_SEQUENCE'
SMS_SEQUENCE = 'SMS_SEQUENCE'
SENDER = 'SENDER'
RECIPIENT = 'RECIPIENT'

NHAIS_OUTBOUND_STATE = 'nhais_outbound_state'

logger = log.IntegrationAdaptorsLogger(__name__)


class OutboundState(object):
    """A local copy of an instance of an outbound state from the state store"""

    def __init__(self, store_data: dict):
        """
        Given
        :param persistence_store:
        :param store_data:
        """

        self.persistence_store = get_persistence_adaptor(table_name=NHAIS_OUTBOUND_STATE, max_retries=3, retry_delay=0.1)
        self._from_dict(store_data)

    async def save_as_new(self):
        """
        Attempts to publish the local state of the outbound state to the state store
        :return:
        """
        operation_id = self.build_operation_id()

        logger.info(f'Attempting to publish outbound state {operation_id}')

        serialised = self._to_dict()

        await self.persistence_store.add(operation_id, serialised)
        logger.info(f'Successfully updated outbound state to state store for {operation_id}')

    def build_operation_id(self):
        bare_key = '_'.join([
            str(self.sis_sequence),
            str(self.sms_sequence),
            self.sender,
            self.recipient])
        return hashlib.sha256(bare_key.encode()).hexdigest()

    def _from_dict(self, store_data):
        self.transaction_id: int = store_data[TRANSACTION_ID]
        self.transaction_timestamp: str = store_data[TRANSACTION_TIMESTAMP]
        self.transaction_type: str = store_data[TRANSACTION_TYPE]
        self.sis_sequence: int = store_data[SIS_SEQUENCE]
        self.sms_sequence: str = store_data[SMS_SEQUENCE]
        self.sender: str = store_data[SENDER]
        self.recipient: str = store_data[RECIPIENT]

    def _to_dict(self):
        """
        A simple serialization method that produces an object from the local data which can be stored in the
        persistence store
        """
        return {
            TRANSACTION_ID: str(self.transaction_id),
            TRANSACTION_TIMESTAMP: self.transaction_timestamp.isoformat(),
            TRANSACTION_TYPE: self.transaction_type,
            SIS_SEQUENCE: str(self.sis_sequence),
            SMS_SEQUENCE: str(self.sms_sequence),
            SENDER: self.sender,
            RECIPIENT: self.recipient
        }


def create_new_outbound_state(segments) -> OutboundState:
    """
    Builds a new local outbound state instance given the details of the message, these details are held locally
    until a `publish` is executed
    """
    transaction_timestamp = None
    sender = None
    recipient = None
    sis_sequence = None
    sms_sequence = None
    transaction_id = None
    transaction_type = None

    for segment in segments:
        if isinstance(segment, InterchangeHeader):
            transaction_timestamp = segment.date_time
            sender = segment.sender
            recipient = segment.recipient
            sis_sequence = segment.sequence_number
        elif isinstance(segment, MessageHeader):
            sms_sequence = segment.sequence_number
        elif isinstance(segment, ReferenceTransactionNumber):
            transaction_id = segment.reference
        elif isinstance(segment, ReferenceTransactionType):
            transaction_type = segment.reference

    if not transaction_id:
        raise ValueError('Expected transaction_id to not be None or empty')
    elif not transaction_timestamp:
        raise ValueError('Expected transaction_timestamp to not be None or empty')
    elif not transaction_type:
        raise ValueError('Expected transaction_type to not be None or empty')
    elif not sis_sequence:
        raise ValueError('Expected sis_sequence to not be None or empty')
    elif not sms_sequence:
        raise ValueError('Expected sms_sequence to not be None or empty')
    elif not sender:
        raise ValueError('Expected sender to not be None or empty')
    elif not recipient:
        raise ValueError('Expected recipient to not be None or empty')

    outbound_state_map = {
        TRANSACTION_ID: transaction_id,
        TRANSACTION_TIMESTAMP: transaction_timestamp,
        TRANSACTION_TYPE: transaction_type,
        SIS_SEQUENCE: sis_sequence,
        SMS_SEQUENCE: sms_sequence,
        SENDER: sender,
        RECIPIENT: recipient
    }

    return OutboundState(outbound_state_map)
