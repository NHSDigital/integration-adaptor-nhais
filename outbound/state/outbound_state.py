import utilities.integration_adaptors_logger as log
from edifact.outgoing.models.interchange import InterchangeHeader, InterchangeTrailer
from edifact.outgoing.models.message import MessageHeader, MessageTrailer, ReferenceTransactionNumber, \
    ReferenceTransactionType
from utilities.message_utilities import get_uuid
from persistence.persistence_adaptor_factory import get_persistence_adaptor

DATA = 'DATA'
OPERATION_ID = 'OPERATION_ID'
TRANSACTION_ID = 'TRANSACTION_ID'
TRANSACTION_TIMESTAMP = 'TRANSACTION_TIMESTAMP'
TRANSACTION_TYPE = 'TRANSACTION_TYPE'
SIS_SEQUENCE = 'SIS_SEQUENCE'
SMS_SEQUENCES = 'SMS_SEQUENCES'
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

        self.persistence_store = get_persistence_adaptor(table_name=NHAIS_OUTBOUND_STATE)
        self._deserialize_data(store_data)

    async def publish(self):
        """
        Attempts to publish the local state of the outbound state to the state store
        :return:
        """
        logger.info(f'Attempting to publish outbound state {self.operation_id}')

        serialised = self._serialise_data()

        await self.persistence_store.add(self.operation_id, serialised)
        logger.info(f'Successfully updated outbound state to state store for {self.operation_id}')

    def _deserialize_data(self, store_data):
        data_attribute = store_data['DATA']
        self.operation_id: str = store_data.get('OPERATION_ID')
        self.transaction_id: int = data_attribute.get('TRANSACTION_ID')
        self.transaction_timestamp: str = data_attribute.get('TRANSACTION_TIMESTAMP')
        self.transaction_type: str = data_attribute.get('TRANSACTION_TYPE')
        self.sis_sequence: int = data_attribute.get('SIS_SEQUENCE')
        self.sms_sequences: list = data_attribute.get('SMS_SEQUENCES')
        self.sender: str = data_attribute.get('SENDER')
        self.recipient: str = data_attribute.get('RECIPIENT')

    def _serialise_data(self):
        """
        A simple serialization method that produces an object from the local data which can be stored in the
        persistence store
        """
        return {
            OPERATION_ID: self.operation_id,
            DATA: {
                TRANSACTION_ID: self.transaction_id,
                TRANSACTION_TIMESTAMP: self.transaction_timestamp,
                TRANSACTION_TYPE: self.transaction_type,
                SIS_SEQUENCE: self.sis_sequence,
                SMS_SEQUENCES: self.sms_sequences,
                SENDER: self.sender,
                RECIPIENT: self.recipient
            }
        }


def create_new_outbound_state(segments) -> OutboundState:
    """
    Builds a new local outbound state instance given the details of the message, these details are held locally
    until a `publish` is executed
    """

    operation_id = get_uuid()
    sms_sequences = []

    for segment in segments:
        if isinstance(segment, InterchangeHeader):
            transaction_timestamp = segment.date_time
            sender = segment.sender
            recipient = segment.recipient
            sis_sequence = segment.sequence_number
        elif isinstance(segment, MessageHeader):
            sms_sequences.append(segment.sequence_number)
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
    elif not sms_sequences:
        raise ValueError('Expected sms_sequences to not be None or empty')
    elif not sender:
        raise ValueError('Expected sender to not be None or empty')
    elif not recipient:
        raise ValueError('Expected recipient to not be None or empty')

    outbound_state_map = {
        OPERATION_ID: operation_id,
        DATA: {
            TRANSACTION_ID: transaction_id,
            TRANSACTION_TIMESTAMP: transaction_timestamp,
            TRANSACTION_TYPE: transaction_type,
            SIS_SEQUENCE: sis_sequence,
            SMS_SEQUENCES: sms_sequences,
            SENDER: sender,
            RECIPIENT: recipient
        }
    }

    return OutboundState(outbound_state_map)
