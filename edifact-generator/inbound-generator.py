import argparse
import os
from templates import Inbound

OUTPUT_FOLDER = "output"

parser = argparse.ArgumentParser(prog=__file__, description='Generate inbound EDIFACT interchanges as .dat files')
parser.add_argument('--count', type=int, help='the number of .dat files to generate - minimum 1 maximum 9999',
                    default='1', required=False)

args = parser.parse_args()


def run():
    validate_count_argument_in_range()
    create_output_folder()
    clear_output_folder()

    generate_edifact_files()

    print(f"Generated {args.count} file(s) in ./{OUTPUT_FOLDER}/ folder")


def validate_count_argument_in_range():
    if args.count > 9999 or args.count < 1:
        raise ValueError("Count value must be between 1 and 9999")


def create_output_folder():
    try:
        os.mkdir(OUTPUT_FOLDER)
    except FileExistsError:
        pass  # folder already exists


def clear_output_folder():
    with os.scandir(OUTPUT_FOLDER) as it:
        for entry in it:
            if not entry.name.startswith('.') and entry.is_file():
                os.remove(f"./{OUTPUT_FOLDER}/{entry.name}")


def generate_edifact_files():
    for i in range(0, args.count):
        filename = f"./{OUTPUT_FOLDER}/{i}.dat"
        file = open(filename, "w")
        file.write(Inbound(i).create_edifact())
        file.close()


if __name__ == '__main__':
    run()
