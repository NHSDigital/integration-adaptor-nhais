import asyncio

from utilities import integration_adaptors_logger as log, timing

from inbound.converter.edifact_recep_consumer import EdifactRecepConsumer
from inbound.converter.edifact_recep_producer import EdifactRecepProducer
from inbound.supplier.supplier_inbound_mq import SupplierInboundMQ
from inbound.converter.edifact_to_fhir import EdifactToFhir
from sequence.inbound.sequence_number_manager import InboundSequenceNumberManager
from utilities import config
from comms import proton_queue_adaptor
from worker.worker import Worker

logger = log.IntegrationAdaptorsLogger(__name__)

class InboundHandler:

    def __init__(self):
        self.edifact_to_fhir = EdifactToFhir()
        self.supplier_incoming_mq = SupplierInboundMQ()
        self.inbound_sequence_number_manager = InboundSequenceNumberManager()
        self.edifact_recep_producer = EdifactRecepProducer()
        self.edifact_recep_consumer = EdifactRecepConsumer()

    def message_callback(self, edifact_message):
        max_number_of_worker = int(config.get_config('INBOUND_MAX_NUMBER_OF_WORKERS', default='3'))
        worker = Worker(edifact_message)
        print(f'I am at worker. I have message: {edifact_message}')
        worker.proceed_message()

        if edifact_message:
            fhir_message = self.edifact_to_fhir.convert_edifact_to_fhir(edifact_message)
            self.inbound_sequence_number_manager.record_sequence_number(fhir_message)
            self.edifact_recep_producer.generate_sequences(fhir_message)
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            loop.run_until_complete(self.supplier_incoming_mq.send(str(fhir_message)))
            loop.close()
        else:
            self.edifact_recep_consumer.record_reciept(edifact_message)

    def create_queue_adaptor(self):
        return proton_queue_adaptor.ProtonQueueAdaptor(
            urls=config.get_config('INBOUND_QUEUE_BROKERS').split(','),
            queue=config.get_config('INBOUND_QUEUE_NAME'),
            username=config.get_config('INBOUND_QUEUE_USERNAME', default=None),
            password=config.get_config('INBOUND_QUEUE_PASSWORD', default=None),
            max_retries=int(config.get_config('INBOUND_QUEUE_MAX_RETRIES', default='3')),
            retry_delay=int(config.get_config('INBOUND_QUEUE_RETRY_DELAY', default='100')) / 1000,
            get_message_callback=self.message_callback)