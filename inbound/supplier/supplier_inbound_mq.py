from comms import proton_queue_adaptor
from utilities import config


class SupplierInboundMQ:

    def __init__(self):
        self.queue_adaptor = proton_queue_adaptor.ProtonQueueAdaptor(
            urls=config.get_config('SUPPLIER_QUEUE_BROKERS').split(','),
            queue=config.get_config('SUPPLIER_QUEUE_NAME'),
            username=config.get_config('SUPPLIER_QUEUE_USERNAME', default=None),
            password=config.get_config('SUPPLIER_QUEUE_PASSWORD', default=None),
            max_retries=int(config.get_config('SUPPLIER_QUEUE_MAX_RETRIES', default='3')),
            retry_delay=int(config.get_config('SUPPLIER_QUEUE_RETRY_DELAY', default='100')) / 1000)

    async def _publish_message_to_outbound_queue(self, message):
        await self._put_message_onto_queue_with(message)

    async def _put_message_onto_queue_with(self, message):
        await self.queue_adaptor.send_async({'payload': message})

    async def send(self, message: str):
        await self._publish_message_to_outbound_queue(message)
