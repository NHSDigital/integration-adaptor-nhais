import argparse
import os
from edifactTemplate import EdifactTemplate

parser = argparse.ArgumentParser(description='Generate inbound EDIFACT interchanges as .dat files')
parser.add_argument('--count', type=int, help='the number of files to generate', default='1', required=False)

args = parser.parse_args()


def run():
    try:
        os.mkdir("output")
    except:
        pass
    with os.scandir("output") as it:
        for entry in it:
            if not entry.name.startswith('.') and entry.is_file():
                os.remove("./output/" + entry.name)

    for i in range(0, args.count):
        filename = "./output/" + str(i) + ".dat"
        file = open(filename, "w")
        file.write(EdifactTemplate(i).create_edifact())
        file.close()

    print("Generated " + str(args.count) + " files in ./output/ folder")


if __name__ == '__main__':
    run()
