# Inbound EDIFACT interchnage generator

A Python script that generates inbound EDIFACT interchanges.

Number of generated files depends on  `--count` parameter - default is 1 (for V&P testing 1750 files recommended)

All generated files have exactly the same messages/transactions. Only header and footer of EDIFACT interchange generate dynamically.

Each interchange contains all supported by NHAIS adaptor inbound transaction types. DEDUCTION (F2) contains multiple transactions in single message.