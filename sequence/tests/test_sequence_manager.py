import unittest
from unittest import mock

import sequence.dynamo_sequence
from sequence.sequence_manager import IdGenerator
from utilities.test_utilities import async_test, awaitable


class TestSequenceManager(unittest.TestCase):

    def setUp(self):
        self.id_generator = IdGenerator()

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_transaction_id(self, mock_next):
        mock_next.return_value = awaitable(1)
        message_id = await self.id_generator.generate_transaction_id()
        mock_next.assert_called_with('transaction_id')
        self.assertEqual(message_id, 1)

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_interchange_id(self, mock_next):
        mock_next.return_value = awaitable(5)
        interchange_id = await self.id_generator.generate_interchange_id('sender', 'recipient')
        mock_next.assert_called_with('SIS-sender-recipient')
        self.assertEqual(interchange_id, 5)

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_message_id(self, mock_next):
        mock_next.return_value = awaitable(9)
        message_id = await self.id_generator.generate_message_id('sender', 'recipient')
        mock_next.assert_called_with('SMS-sender-recipient')
        self.assertEqual(message_id, 9)

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_interchange_id_for_empty_sender(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_interchange_id('', 'recipient')

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_interchange_id_for_none_sender(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_interchange_id(None, 'recipient')

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_interchange_id_for_empty_recipient(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_interchange_id('sender', '')

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_interchange_id_for_none_recipient(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_interchange_id('sender', None)

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_message_id_for_empty_sender(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_message_id('', 'recipient')

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_message_id_for_none_sender(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_message_id(None, 'recipient')

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_message_id_for_empty_recipient(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_message_id('sender', '')

    @mock.patch.object(sequence.dynamo_sequence.DynamoSequenceGenerator, 'next')
    @async_test
    async def test_generate_message_id_for_none_recipient(self, mock_next):
        with self.assertRaises(ValueError):
            mock_next.return_value = 5
            await self.id_generator.generate_message_id('sender', None)
