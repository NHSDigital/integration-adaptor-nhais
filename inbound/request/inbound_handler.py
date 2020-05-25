import asyncio
import json

from comms import proton_queue_adaptor
from proton import Message
from utilities import config
from utilities import integration_adaptors_logger as log

from edifact.models.edifact import Edifact
from inbound.converter.edifact_recep_consumer import EdifactRecepConsumer
from inbound.converter.edifact_recep_producer import EdifactRecepProducer
from inbound.converter.edifact_to_fhir import EdifactToFhir
from inbound.supplier.supplier_inbound_mq import SupplierInboundMQ
from sequence.inbound.sequence_number_manager import InboundSequenceNumberManager

logger = log.IntegrationAdaptorsLogger(__name__)

class InboundHandler:

    def __init__(self):
        self.edifact_to_fhir = EdifactToFhir()
        self.supplier_incoming_mq = SupplierInboundMQ()
        self.inbound_sequence_number_manager = InboundSequenceNumberManager()
        self.edifact_recep_producer = EdifactRecepProducer()
        self.edifact_recep_consumer = EdifactRecepConsumer()

    def message_callback(self, message: Message):
        logger.info(f'Message recieved by inbound, message: {message}')
        mesh_message = json.loads(message.body)
        if mesh_message['workflowId'] == 'NHAIS_REG':
            edifact_file_content = mesh_message['content']
            edifact = Edifact.create_edifact_from_message(edifact_file_content)
            fhir_message = self.edifact_to_fhir.convert_edifact_to_fhir(edifact)
            self.inbound_sequence_number_manager.record_sequence_number(fhir_message)
            self.edifact_recep_producer.generate_sequences(fhir_message)
            # TODO: is str() the correct way to marshall a FHIR Patient?
            asyncio.run(self.supplier_incoming_mq.send(str(fhir_message)))
        else:
            self.edifact_recep_consumer.record_reciept(message)

    def create_queue_adaptor(self):
        return proton_queue_adaptor.ProtonQueueAdaptor(
            urls=config.get_config('INBOUND_QUEUE_BROKERS').split(','),
            queue=config.get_config('INBOUND_QUEUE_NAME'),
            username=config.get_config('INBOUND_QUEUE_USERNAME', default=None),
            password=config.get_config('INBOUND_QUEUE_PASSWORD', default=None),
            max_retries=int(config.get_config('INBOUND_QUEUE_MAX_RETRIES', default='3')),
            retry_delay=int(config.get_config('INBOUND_QUEUE_RETRY_DELAY', default='100')) / 1000,
            get_message_callback=self.message_callback)