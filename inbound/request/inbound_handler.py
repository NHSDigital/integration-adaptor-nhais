from utilities import integration_adaptors_logger as log, timing

from inbound.supplierincomingmq.incoming_mq import SupplierIncomingMQ
from edifact.incoming.edifact_to_fhir import EdifactToFhir

logger = log.IntegrationAdaptorsLogger(__name__)

class Inbound_Handler():

    def __init__(self):
        self.edifact_to_fhir = EdifactToFhir()
        self.supplier_incoming_mq = SupplierIncomingMQ()

    async def on_message_recieved(self, edifact_message):
        fhir_message = self.edifact_to_fhir.convert_edifact_to_fhir(edifact_message)
        self.supplier_incoming_mq.send(fhir_message)
