from sequence.sequence_factory import get_sequence_generator

TABLE_NAME = 'generated_id_counter'


class IdGenerator:
    """A component that provides sequential ids"""

    def __init__(self):
        self.sequence_generator = get_sequence_generator(TABLE_NAME)

    async def generate_transaction_id(self) -> int:
        """A function that provides sequential transaction id."""
        key = 'transaction_id'
        return await self.sequence_generator.next(key)

    async def generate_interchange_id(self, sender, recipient) -> int:
        """A function that provides sequential interchange id.
        Definition:
        SIS (Send Interchange Sequence) - sequence number for the entire EDIFACT interchange """
        key = f"SIS-{sender}-{recipient}"
        await self.validate_sender_and_recipient(recipient, sender)
        return await self.sequence_generator.next(key)

    async def generate_message_id(self, sender, recipient) -> int:
        """A function that provides sequential message id
        Definition:
        SMS (Send Message Sequence) - sequence number applied for each message within an interchange"""
        key = f"SMS-{sender}-{recipient}"
        await self.validate_sender_and_recipient(recipient, sender)
        return await self.sequence_generator.next(key)

    async def validate_sender_and_recipient(self, recipient, sender):
        if not sender:
            raise ValueError('Expected sender not to be None or empty')
        if not recipient:
            raise ValueError('Expected recipients not to be None or empty')

