class Inbound:

    def __init__(self, number: int):
        self._number = number

    _edifact = """
UNH+00000006+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:199201251235:203'
RFF+950:F1'
S01+1'
RFF+TN:100'
NAD+GP+4826940,281:900'
HEA+DM+Y:ZZZ'
S02+2'
PNA+PAT+AVERT1/1:OPI+++SU:MORRIS'
NAD+PAT++??:136 HIGH STREET::BROMLEY:KENT+++++BR11 5RE'
S02+2'
PNA+PER++++SU:ANDREWS'
UNT+15+00000006'

UNH+00000007+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:199201251235:203'
RFF+950:F2'
S01+1'
RFF+TN:101'
NAD+GP+2750922,295:900'
GIS+1:ZZZ'
DTM+961:19920125:102'
S02+2'
PNA+PAT+TE26:OPI'
S01+1'
RFF+TN:102'
NAD+GP+4826940,281:900'
NAD+NFH+COV:954'
GIS+2:ZZZ'
DTM+961:19920125:102'
S02+2'
PNA+PAT+RE1/12/13:OPI'
UNT+21+00000007'

UNH+00000008+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:199201251235:203'
RFF+950:F3'
S01+1'
RFF+TN:103'
NAD+GP+4826940,281:900'
FTX+RGI+++WRONG HA - TRY SURREY'
S01+1'
RFF+TN:104'
NAD+GP+2750922,295:900'
FTX+RGI+++WRONG HA - TRY LONDON'
UNT+10+00000008'

UNH+00000009+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:199201251235:203'
RFF+950:F4'
S01+1'
RFF+TN:105'
NAD+GP+2750922,295:900'
S02+2'
PNA+PAT+RAT56:OPI'
S01+1'
RFF+TN:106'
NAD+GP+2750922,295:900'
UNT+11+00000009'

UNH+00000010+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:202006101438:203'
RFF+950:F9'
S01+1'
RFF+TN:107'
NAD+GP+2750922,295:900'
HEA+FRN+8:ZZZ'
DTM+962:19920725:102'
FTX+RGI+++PATIENT THOUGHT TO BE IN SCOTLAND'
S02+2'
PNA+PAT+TE26:OPI+++SU:STEVENS+FO:CHARLES++MI:ANTHONY+FS:RICHARD'
DTM+329:19400523:102'
NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7EW'
S01+1'
RFF+TN:108'
NAD+GP+2750922,295:900'
HEA+FRN+8:ZZZ'
DTM+962:19920725:102'
FTX+RGI+++PATIENT THOUGHT TO BE IN SCOTLAND'
S02+2'
PNA+PAT+TE21:OPI+++SU:SMITH+FO:JOHN++MI:ANTHONY'
DTM+329:19400523:102'
NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7EW'
S01+1'
RFF+TN:109'
NAD+GP+4826940,281:900'
HEA+FRN+1:ZZZ'
DTM+962:19920725:102'
S02+2'
PNA+PAT+SP56:OPI+++SU:GENTT+FO:BRENDA'
DTM+329:19460312:102'
NAD+PAT++??:16 OLD STREET:ST PAULS CRAY:ORPINGTON:KENT'
UNT+35+00000010'

UNH+00000011+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:202006101438:203'
RFF+950:F10'
S01+1'
RFF+TN:110'
NAD+GP+4826940,281:900'
S02+2'
PNA+PAT+RAT56:OPI'
S01+1'
RFF+TN:111'
NAD+GP+4826940,281:900'
S02+2'
PNA+PAT+NHS123:OPI'
UNT+16+00000011'

UNH+00000012+FHSREG:0:1:FH:FHS001'
BGM+++507'
NAD+FHS+XX1:954'
DTM+137:202006101438:203'
RFF+950:F11'
S01+1'
RFF+TN:112'
NAD+GP+4826940,281:900'
FTX+RGI+++PATIENT KNOWN BY HA GENERAL MANAGER'
S02+2'
PNA+PAT+TT12/24:OPI'
S01+1'
RFF+TN:113'
NAD+GP+4826940,281:900'
FTX+RGI+++PATIENT KNOWN BY HA GENERAL MANAGER'
S02+2'
PNA+PAT+123NHS123:OPI'
UNT+18+00000012'
"""

    def _header(self):
        return f"UNB+UNOA:2+XX11+{'{:04d}'.format(self._number)}+920125:1235+{'{:08d}'.format(self._number)}'"

    def _footer(self):
        return f"UNZ+7+{'{:08d}'.format(self._number)}'"

    def create_edifact(self):
        return f"{self._header()}{self._edifact}{self._footer()}"

