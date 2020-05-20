import tornado.httpserver
import tornado.ioloop
import tornado.web
import threading

import utilities.integration_adaptors_logger as log
from handlers import healthcheck_handler
from outbound.request.acceptance_amendment import AcceptanceAmendmentRequestHandler
from outbound.request.deduction import DeductionRequestHandler
from outbound.request.removal import RemovalRequestHandler
from utilities import config
from comms import proton_queue_adaptor
from worker.worker import Worker

logger = log.IntegrationAdaptorsLogger(__name__)


def start_tornado_server() -> None:
    tornado_application = tornado.web.Application(
        [
            (r'/fhir/Patient/([0-9]*)/\$nhais\.removal', RemovalRequestHandler),
            (r'/fhir/Patient/([0-9]*)/\$nhais\.deduction', DeductionRequestHandler),
            (r'/fhir/Patient/([0-9]*)', AcceptanceAmendmentRequestHandler),  # POST -> Acceptance, PATCH -> Amendment
            (r'/healthcheck', healthcheck_handler.HealthcheckHandler)
        ])
    tornado_server = tornado.httpserver.HTTPServer(tornado_application)
    server_port = int(config.get_config('OUTBOUND_SERVER_PORT', default='80'))
    tornado_server.listen(server_port)

    logger.info('Starting nhais server at port {server_port}', fparams={'server_port': server_port})
    tornado_io_loop = tornado.ioloop.IOLoop.current()
    try:
        tornado_io_loop.start()
    except KeyboardInterrupt:
        logger.warning('Keyboard interrupt')
    finally:
        tornado_io_loop.stop()
        tornado_io_loop.close(True)
    logger.info('Server shut down, exiting...')


def message_callback(message):
    # number of workers should be configurable
    max_number_of_worker = int(config.get_config('INBOUND_MAX_NUMBER_OF_WORKERS', default='3'))
    worker = Worker(message)
    print(f'I am at worker. I have message: {message}')
    worker.proceed_message()


def create_queue_adaptor():
    # username/password was set as quest, not sure if this should be guest or None as done before
    return proton_queue_adaptor.ProtonQueueAdaptor(
        urls=config.get_config('INBOUND_QUEUE_BROKERS').split(','),
        queue=config.get_config('INBOUND_QUEUE_NAME'),
        username=config.get_config('INBOUND_QUEUE_USERNAME', default=None),
        password=config.get_config('INBOUND_QUEUE_PASSWORD', default=None),
        max_retries=int(config.get_config('INBOUND_QUEUE_MAX_RETRIES', default='3')),
        retry_delay=int(config.get_config('INBOUND_QUEUE_RETRY_DELAY', default='100')) / 1000,
        get_message_callback=message_callback)


def start_listening_to_events():
    logger.info('Starting listening to incoming messages')
    adaptor = create_queue_adaptor()
    adaptor.wait_for_messages()


def main():
    config.setup_config("NHAIS")
    log.configure_logging("NHAIS")

    listening_events_thread = threading.Thread(target=start_listening_to_events)
    listening_events_thread.start()

    start_tornado_server()


if __name__ == "__main__":
    try:
        main()
    except Exception:
        logger.critical('Fatal exception in main application', exc_info=True)
    finally:
        logger.info('Exiting application')
