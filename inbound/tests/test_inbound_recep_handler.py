import unittest
from unittest.mock import MagicMock

from edifact.models.edifact import Edifact
from edifact.models.interchange import InterchangeHeader
from edifact.models.message import ReferenceMessageRecep
from inbound.inbound_recep_handler import InboundRecepHandler
from utilities import test_utilities
from utilities.test_utilities import awaitable


class TestInboundRecepHandler(unittest.TestCase):

    @test_utilities.async_test
    async def test_handle(self):
        persistence_adaptor = MagicMock()
        persistence_adaptor.update.return_value = awaitable()

        interchange_header = InterchangeHeader(
            sender="some_sender", recipient="some_recipient", date_time=None, sequence_number="some_interchange_id")
        reference_message_recep_1 = ReferenceMessageRecep("some_message_id_1")
        reference_message_recep_2 = ReferenceMessageRecep("some_message_id_2")

        handler = InboundRecepHandler(persistence_adaptor)

        await handler.handle(Edifact([
            interchange_header, reference_message_recep_1, reference_message_recep_2
        ]))

        self.assertEqual(persistence_adaptor.update.call_count, 2)
        operation_id, update_dict = persistence_adaptor.update.mock_calls[0][1]
        self.assertEqual(operation_id, '04c9f6f9052c1840e5e156cae2d04fe134eee9e62c983e84f7159d85baa2364a')
        self.assertEqual(update_dict, {'RECEP_RECEIVED': True})
        operation_id, update_dict = persistence_adaptor.update.mock_calls[1][1]
        self.assertEqual(operation_id, 'afe7c39b4d645730a3085b1d94b024e37821d83f470c7e5172ea2b426d665d63')
        self.assertEqual(update_dict, {'RECEP_RECEIVED': True})
