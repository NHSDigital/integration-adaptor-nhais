from utilities import integration_adaptors_logger as log

from inbound.converter.edifact_recep_consumer import EdifactRecepConsumer
from inbound.converter.edifact_recep_producer import EdifactRecepProducer
from inbound.supplier.supplier_inbound_mq import SupplierInboundMQ
from inbound.converter.edifact_to_fhir import EdifactToFhir
from sequence.inbound.sequence_manager import InboundSequenceNumberManager

logger = log.IntegrationAdaptorsLogger(__name__)

class InboundHandler:

    def __init__(self):
        self.edifact_to_fhir = EdifactToFhir()
        self.supplier_incoming_mq = SupplierInboundMQ()
        self.inbound_sequence_number_manager = InboundSequenceNumberManager()
        self.edifact_recep_producer = EdifactRecepProducer()
        self.edifact_recep_consumer = EdifactRecepConsumer()

    async def on_message_recieved(self, edifact_message):
        if edifact_message.message:
            fhir_message = self.edifact_to_fhir.convert_edifact_to_fhir(edifact_message)
            self.inbound_sequence_number_manager.record_sequence_number(fhir_message)
            self.edifact_recep_producer.generate_sequences(fhir_message)
            self.supplier_incoming_mq.send(fhir_message)
        else:
            self.edifact_recep_consumer.record_reciept(edifact_message)
