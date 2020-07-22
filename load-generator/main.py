import argparse
import dataclasses
import hashlib
import os
import random
from dataclasses import dataclass
from datetime import datetime, timedelta

from pymongo import MongoClient
from pymongo.errors import BulkWriteError

parser = argparse.ArgumentParser(description='Ingest NHAIS state test data')
parser.add_argument('--host', type=str, help='the mongodb host', default='localhost', required=False)
parser.add_argument('--port', type=int, help='the mongodb port', default=27017, required=False)

parser.add_argument('--sis_init', type=int, help='the initial value of interchange sequence', required=True)
parser.add_argument('--sis_count', type=int, help='the number of interchanges to ingest', required=True)
parser.add_argument('--sms_init', type=int, help='the initial value of message sequence', required=True)
parser.add_argument('--sms_count', type=int, help='the number of messages per each interchange to ingest',
                    required=True)
parser.add_argument('--tn_init', type=int, help='the initial value of transaction id', required=True)
parser.add_argument('--tn_count', type=int, help='the number of transactions per each message to ingest', required=True)

parser.add_argument('--sender', type=str, help='the sender of the interchange', required=True)
parser.add_argument('--recipient', type=str, help='the recipient of the interchange', required=True)
parser.add_argument('--timestamp',
                    type=lambda value: datetime.strptime(value, "%Y-%m-%d %H:%M:%S"),
                    help='the initial timestamp value to be used; each next transaction will add 1 minute',
                    required=True)
parser.add_argument('--state_type', type=str, help='the state type to insert data to',
                    required=True, choices=['inbound', 'outbound'])
parser.add_argument('--batch_size', type=int, help='the batch size of inserted data', default=1_000, required=False)

args = parser.parse_args()

sis_init = args.sis_init
sis_count = args.sis_count
sms_init = args.sms_init
sms_count = args.sms_count
tn_init = args.tn_init
tn_count = args.tn_count
sender = args.sender
recipient = args.recipient
timestamp = args.timestamp
state_type = args.state_type
batch_size = args.batch_size

client = MongoClient(args.host, args.port)
collection = client['nhais'][state_type + 'State']


@dataclass
class OutboundState:
    interchangeSequence: int
    messageSequence: int
    transactionId: int
    sender: str
    recipient: str
    translationTimestamp: datetime
    workflowId: str
    operationId: str
    transactionType: str
    recepCode: str
    recepDateTime: datetime


@dataclass
class InboundState:
    interchangeSequence: int
    messageSequence: int
    transactionId: int
    sender: str
    recipient: str
    translationTimestamp: datetime
    workflowId: str
    operationId: str
    transactionType: str


outboundTransactionTypes = ["ACG", "AMG", "REG", "DER"]
inboundTransactionTypes = ["AMF", "DEF", "REF", "APF", "FPN", "FFR", "DRR"]
recepCodes = ["CP", "CA", "CI"]


def build_state_record(
        state_record_type: str, sis: int, sms: int, tn: int, translation_timestamp: datetime):
    def build_operation_id(organization: str):
        return hashlib.sha256((organization + str(tn)).encode("UTF-8")).hexdigest()

    if state_record_type == 'inbound':
        operation_id = build_operation_id(recipient)
        return InboundState(
            interchangeSequence=sis, messageSequence=sms, transactionId=tn, operationId=operation_id,
            workflowId="NHAIS_REG", sender=sender, recipient=recipient, translationTimestamp=translation_timestamp,
            transactionType=random.choice(inboundTransactionTypes))
        pass
    elif state_record_type == 'outbound':
        operation_id = build_operation_id(sender)
        return OutboundState(
            interchangeSequence=sis, messageSequence=sms, transactionId=tn, operationId=operation_id,
            workflowId="NHAIS_REG", sender=sender, recipient=recipient, translationTimestamp=translation_timestamp,
            recepCode=random.choice(recepCodes), recepDateTime=datetime.now(),
            transactionType=random.choice(outboundTransactionTypes))
    else:
        raise Exception("Unknown state type " + state_record_type)


class Generator:
    def __init__(self, initial_value):
        self.value = initial_value

    def get(self):
        value = self.value
        self.value = value + 1
        return value


def run():
    def insert_all(list_of_rows):
        print(f"Inserting {len(list_of_rows)} rows:{os.linesep}{os.linesep.join(map(str, list_of_rows))}")
        collection.insert_many(map(dataclasses.asdict, list_of_rows))

    sis_generator = Generator(sis_init)
    sms_generator = Generator(sis_init)
    tn_generator = Generator(sis_init)
    rows = []
    counter = 0
    for _ in range(1, sis_count + 1):
        sis = sis_generator.get()
        for _ in range(1, sms_count + 1):
            sms = sms_generator.get()
            for _ in range(1, tn_count + 1):
                tn = tn_generator.get()
                t = timestamp + timedelta(minutes=counter)
                counter += 1
                state = build_state_record(state_record_type=state_type, sis=sis, sms=sms, tn=tn,
                                           translation_timestamp=t)
                rows.append(state)
                if len(rows) == batch_size:
                    insert_all(rows)
                    rows = []

    if len(rows) != 0:
        try:
            insert_all(rows)
        except BulkWriteError as ex:
            write_errors = os.linesep.join(map(lambda x: x['errmsg'], ex.details['writeErrors']))
            raise Exception(f"Error during bulk insert:{os.linesep}{write_errors}") from ex


if __name__ == '__main__':
    run()
