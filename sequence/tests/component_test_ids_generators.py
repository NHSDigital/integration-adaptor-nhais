"""Module to test independence and coherence of message id, interchange id and transaction id generators."""

import unittest

import aioboto3
from sequence.sequence_factory import get_sequence_generator
from sequence.sequence_manager import IdGenerator
from utilities import test_utilities
from utilities import config


class ComponentTestIds(unittest.TestCase):

    @test_utilities.async_test
    async def setUp(self):
        config.setup_config("NHAIS")
        self.table_name = 'test_ids_generators_table'
        self.key = 'transaction_id'
        self.endpoint = config.get_config('DYNAMODB_ENDPOINT_URL', None)
        self.region_name = 'eu-west-2'

        # Create instance id generator and replace the default sequence_generator
        # with one using the test table
        self.id_generator = IdGenerator()
        self.id_generator.sequence_generator = get_sequence_generator(self.table_name)

        async with aioboto3.resource('dynamodb', region_name=self.region_name,
                                     endpoint_url=self.endpoint) as dynamo_resource:
            try:
                table = await dynamo_resource.Table(self.table_name)
                await table.delete()
            except dynamo_resource.meta.client.exceptions.ResourceNotFoundException:
                pass  # table may not exist
            await self.__create_table(dynamo_resource)

    @test_utilities.async_test
    async def tearDown(self):
        async with aioboto3.resource('dynamodb', region_name=self.region_name,
                                     endpoint_url=self.endpoint) as dynamo_resource:
            table = await dynamo_resource.Table(self.table_name)
            await table.delete()

    @test_utilities.async_test
    async def test_each_id_has_separate_sequence(self):
        # Generate two transaction ids - to make sure that the value increase each time
        self.assertEqual(await self.id_generator.generate_transaction_id(), 1)
        self.assertEqual(await self.id_generator.generate_transaction_id(), 2)

        # Generate interchange id - the sequence should be independent from that of transaction id,
        # so it should start from 1
        self.assertEqual(await self.id_generator.generate_interchange_id(111, 222), 1)

        # Generate message id - the sequence should be independent from those of transaction and
        # interchange ids, so it should start from 1'''
        self.assertEqual(await self.id_generator.generate_message_id(111, 222), 1)

    @test_utilities.async_test
    async def test_interchange_id_has_separate_sequence_for_each_key(self):
        # Generate two interchange ids for key SIS-111-222
        self.assertEqual(await self.id_generator.generate_interchange_id(111, 222), 1)
        self.assertEqual(await self.id_generator.generate_interchange_id(111, 222), 2)

        # Generate interchange id for key SIS-AAA-BBB - the sequence should be independent from
        # that of interchange ids for key SIS-111-222, so it should start from 1'''
        self.assertEqual(await self.id_generator.generate_interchange_id('AAA', 'BBB'), 1)

    @test_utilities.async_test
    async def test_message_id_has_separate_sequence_for_each_key(self):
        # Generate two message ids for key SIS-111-222
        self.assertEqual(await self.id_generator.generate_message_id(111, 222), 1)
        self.assertEqual(await self.id_generator.generate_message_id(111, 222), 2)

        # Generate interchange id for key SIS-AAA-BBB - the sequence should be independent
        # from that of interchange ids for key SIS-111-222, so it should start from 1
        self.assertEqual(await self.id_generator.generate_message_id('AAA', 'BBB'), 1)

    async def __create_table(self, dynamo_resource):
        await dynamo_resource.create_table(
            AttributeDefinitions=[
                {'AttributeName': 'key', 'AttributeType': 'S'}
            ],
            KeySchema=[
                {'AttributeName': 'key', 'KeyType': 'HASH'}
            ],
            ProvisionedThroughput={'ReadCapacityUnits': 1, 'WriteCapacityUnits': 1},
            TableName=self.table_name
        )
