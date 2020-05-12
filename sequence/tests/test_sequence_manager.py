import unittest
from unittest import mock

import sequence.dynamo_sequence
from sequence.sequence_manager import IdGenerator


class TestSequenceManager(unittest.TestCase):

    def setUp(self):
        self.id_generator = IdGenerator()

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    async def test_generate_message_id(self, mock_next):
        mock_next.return_value = 1
        message_id = await self.id_generator.generate_transaction_id()
        self.assertEquals(message_id, 1)

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    async def test_generate_interchange_id(self, mock_next):
        mock_next.return_value = 5
        interchange_id = await self.id_generator.generate_transaction_id()
        self.assertEquals(interchange_id, 5)

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    async def test_generate_message_id(self, mock_next):
        mock_next.return_value = 9
        message_id = await self.id_generator.generate_transaction_id()
        self.assertEquals(message_id, 9)
