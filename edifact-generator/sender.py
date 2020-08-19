import argparse
import os

EDIFACT_FOLDER = "output"

parser = argparse.ArgumentParser(prog=__file__, description='Send inbound EDIFACT interchanges from generated .dat files')
parser.add_argument('--mailbox', type=str, help='the recipient mailbox', default='gp_mailbox', required=False)
parser.add_argument('--limit', type=int, help='max messages to send', default='9999', required=False)

args = parser.parse_args()


def run():
    count = 0
    with os.scandir(EDIFACT_FOLDER) as it:
        for entry in it:
            if entry.name.endswith('.dat') and entry.is_file() and count < args.limit:
                command = f"cd ../mesh/ && ./mesh.sh send {args.mailbox} @../edifact-generator/output/{entry.name}"
                print(command)
                os.system(command)
                count = count + 1

    print(f"\nSent {count} messages")


if __name__ == '__main__':
    run()
