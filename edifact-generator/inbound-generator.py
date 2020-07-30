import argparse
import os
from templates import Inbound

parser = argparse.ArgumentParser(prog=__file__, description='Generate inbound EDIFACT interchanges as .dat files')
parser.add_argument('--count', type=int, help='the number of files to generate - minimum 1 maximum 9999',
                    default='1', required=False)

args = parser.parse_args()


def run():
    validate_count_argument()
    create_output_folder()
    clear_output_folder()

    generate_edifact_files()

    print("Generated " + str(args.count) + " files in ./output/ folder")


def generate_edifact_files():
    for i in range(0, args.count):
        filename = "./output/" + str(i) + ".dat"
        file = open(filename, "w")
        file.write(Inbound(i).create_edifact())
        file.close()


def validate_count_argument():
    if args.count > 9999 or args.count < 1:
        raise ValueError("Count value must be between 1 and 9999")


def clear_output_folder():
    with os.scandir("output") as it:
        for entry in it:
            if not entry.name.startswith('.') and entry.is_file():
                os.remove("./output/" + entry.name)


def create_output_folder():
    try:
        os.mkdir("output")
    except:
        pass


if __name__ == '__main__':
    run()
