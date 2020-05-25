import unittest
from datetime import datetime
from unittest.mock import MagicMock

from exceptions import MaxRetriesExceeded
from inbound.inbound_state_handler import InboundStateHandler, InboundStateRecord
from utilities import test_utilities
from utilities.test_utilities import awaitable


class TestInboundStateHandler(unittest.TestCase):
    inbound_state_record = InboundStateRecord(
        interchange_id="qwe",
        message_id="asd",
        sender="some_sender",
        recipient="some_recipient",
        transaction_id="some_transaction_id",
        transaction_type="some_transaction_type",
        translation_timestamp=datetime.fromtimestamp(1284286794)
    )
    dict_to_save = inbound_state_record.to_dict()
    calculated_key = inbound_state_record.build_key()

    @test_utilities.async_test
    async def test_save_as_new(self):
        persistence_adaptor = MagicMock()
        persistence_adaptor.add.return_value = awaitable(1)

        handler = InboundStateHandler(self.inbound_state_record, persistence_adaptor)

        await handler.save_as_new()

        self.assertEqual(persistence_adaptor.add.call_count, 1)
        key, data = persistence_adaptor.add.mock_calls[0][1]
        self.assertEqual(key, self.calculated_key)
        self.assertEqual(data, self.dict_to_save)

    @test_utilities.async_test
    async def test_save_as_new_raises_error_if_key_already_exists(self):
        persistence_adaptor = MagicMock()
        persistence_adaptor.add.side_effect = MaxRetriesExceeded()

        handler = InboundStateHandler(self.inbound_state_record, persistence_adaptor)

        with self.assertRaisesRegex(MaxRetriesExceeded, ""):
            await handler.save_as_new()

        self.assertEqual(persistence_adaptor.add.call_count, 1)
        key, data = persistence_adaptor.add.mock_calls[0][1]
        self.assertEqual(key, self.calculated_key)
        self.assertEqual(data, self.dict_to_save)
