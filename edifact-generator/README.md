# Inbound EDIFACT interchnage generator

A Python script that generates inbound EDIFACT interchanges.

Number of generated files depends on  `--count` parameter - default and minimum is 1 maximum 9999
(for V&P testing it is recommended to use at least 1750 files)

Example:
````
python3 inbound-generator.py --count 1750
````
Script creates files in `edifact-generator/output/` folder.

All generated files have exactly the same messages/transactions. Only header and footer of EDIFACT interchange generate dynamically.

Each interchange contains all supported by NHAIS adaptor inbound transaction types. DEDUCTION (F2) contains multiple transactions in single message.