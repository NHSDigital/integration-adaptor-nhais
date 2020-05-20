import threading

import tornado.httpserver
import tornado.ioloop
import tornado.web
import utilities.integration_adaptors_logger as log
from handlers import healthcheck_handler
from utilities import config

from inbound.request.inbound_handler import InboundHandler
from outbound.request.acceptance_amendment import AcceptanceAmendmentRequestHandler
from outbound.request.deduction import DeductionRequestHandler
from outbound.request.removal import RemovalRequestHandler

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


def start_listening_to_events():
    logger.info('Starting listening to incoming messages')
    handler = InboundHandler()
    adaptor = InboundHandler.create_queue_adaptor(self=handler)
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
