import utilities.integration_adaptors_logger as log

logger = log.IntegrationAdaptorsLogger(__name__)


class Worker(object):
    # _instances = []

    def __init__(self, message):
        # if len(self._instances) > 2:
        #     self._instances.pop(0).finish_proceeding()
        # self._instances.append(self)
        self.message = message

    def proceed_message(self):
        logger.info(f'Message is proceed: {self.message}')
        pass

    # async def finish_proceeding(self):
    #     logger.warning(f'There are too many instances of worker. '
    #                    f'Waiting for the oldest to finish processng the messaa')
    #     pass
