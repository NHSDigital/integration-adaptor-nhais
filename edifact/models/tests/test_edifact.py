import unittest

from edifact.models.edifact import Edifact

EDI_FILE = """UNB+UNOA:2+GP123+HA456+200427:1737+00000045'
UNH+00000056+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+HA456:954'
DTM+137:202004271737:203'
RFF+950:G1'
S01+1'
RFF+TN:5174'
UNT+8+00000056'
UNZ+1+00000045'"""

class TestInterchangeHeader(unittest.TestCase):

    def test_create_from_message(self):
        edifact = Edifact.create_edifact_from_message(EDI_FILE)
        # TODO: validate parsed segments