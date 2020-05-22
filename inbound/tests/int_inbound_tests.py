import asyncio
import unittest

from comms import proton_queue_adaptor
from comms.blocking_queue_adaptor import BlockingQueueAdaptor
from utilities import config, test_utilities


class InboundIntegrationTests(unittest.TestCase):
    """
     These tests demonstrate each inbound (HA -> GP) transaction
    """

    def setUp(self):
        config.setup_config('NHAIS')
        self.supplier_queue = BlockingQueueAdaptor(username=config.get_config('SUPPLIER_QUEUE_USERNAME', default=None),
                                               password=config.get_config('SUPPLIER_QUEUE_PASSWORD', default=None),
                                               queue_url=config.get_config('SUPPLIER_QUEUE_BROKERS'),
                                               queue_name=config.get_config('SUPPLIER_QUEUE_NAME'))
        self.supplier_queue.drain()
        self.incoming_queue = proton_queue_adaptor.ProtonQueueAdaptor(
            urls=config.get_config('INBOUND_QUEUE_BROKERS').split(','),
            queue=config.get_config('INBOUND_QUEUE_NAME'),
            username=config.get_config('INBOUND_QUEUE_USERNAME', default=None),
            password=config.get_config('INBOUND_QUEUE_PASSWORD', default=None),
            max_retries=int(config.get_config('INBOUND_QUEUE_MAX_RETRIES', default='3')),
            retry_delay=int(config.get_config('INBOUND_QUEUE_RETRY_DELAY', default='100')) / 1000)

    @test_utilities.async_test
    async def test_acceptance_transaction(self):
        mymessage = 'this is my message'
        await self.incoming_queue.send_async(mymessage)
        message = self.supplier_queue.get_next_message_on_queue()
        self.assertIsNotNone(message, 'message from queue should exist')
        self.assertTrue(len(message.body) > 0, 'message from queue should not be empty')
