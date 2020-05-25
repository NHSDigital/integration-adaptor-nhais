from typing import List

import utilities.integration_adaptors_logger as log
from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader
from edifact.models.message import ReferenceMessageRecep
from outbound.state.outbound_state import OutboundState, RECEP_RECEIVED
from persistence.persistence_adaptor import PersistenceAdaptor

logger = log.IntegrationAdaptorsLogger(__name__)


class InboundRecepHandler:

    def __init__(self, persistence_adaptor: PersistenceAdaptor) -> None:
        super().__init__()
        self.persistence_adaptor = persistence_adaptor

    async def handle(self, edifact_recep: Edifact):
        interchange_header: InterchangeHeader = edifact_recep.interchange_header
        reference_message_receps: List[ReferenceMessageRecep] = edifact_recep.reference_message_recep

        interchange_id = interchange_header.sequence_number
        sender = interchange_header.sender
        recipient = interchange_header.recipient
        message_ids = map(lambda rmr: rmr.reference.split(' ')[0], reference_message_receps)

        for message_id in message_ids:
            operation_id = OutboundState.build_operation_id(
                sis_sequence=interchange_id, sms_sequence=message_id, sender=sender, recipient=recipient)

            logger.info("Updating outbound state store for %s after receiving a recep", operation_id)

            await self.persistence_adaptor.update(operation_id, {RECEP_RECEIVED: True})
