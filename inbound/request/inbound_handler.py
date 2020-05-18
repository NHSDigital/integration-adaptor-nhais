from utilities import integration_adaptors_logger as log, timing

from inbound.supplierincomingmq.incoming_mq import SupplierIncomingMQ
from edifact.incoming.edifact_to_fhir import EdifactToFhir

logger = log.IntegrationAdaptorsLogger(__name__)

class Inbound_Handler():

    def on_message_recieved(self, edifact_message):
        fhir_message = EdifactToFhir.convert_edifact_to_fhir(edifact_message)
        SupplierIncomingMQ.send(fhir_message)
