# NHAIS Adaptor Examples

# Outbound (GP->HA) Transactions

The `outbound-gp2ha/` folder contains examples of outbound transactions.

* `<transaction_type>##.txt`: A textual descriptions of the transaction if one was provided by the source data set (mostly Appendix J)
* `<transaction_type>##.js`: The JSON payload sent from the GP System to the Adaptor. These use .js extensions so 
  comments can be used to explain specific parts of the request. These comments need to be removed if the example is to
  be used in a request against the adaptor as Javascript comments are not valid JSON.
* `<transaction_type>##.dat`: The EDIFACT file sent from the adaptor to the NHAIS instance for the request

## Amendments

| Possible Data Items | Example(s)
|-----------|---
| Surname | 1
| First Forename |
| Second Forename |
| Other Forenames |
| Previous Surname | 1
| Title | 1
| Sex |
| Date of Birth |
| Address - 5 fields | 1, 2, 3, 4
| Postcode | 1
| Drugs Dispensed Marker | 1
| RPP Mileage | n/a - DEPRECATED
| Blocked Route/Special District Marker | n/a - DEPRECATED
| Walking Units | n/a - DEPRECATED
| Residential Institute Code | 1

# Inbound (HA->GP) Transactions

The `inbound-ha2gp/` folder contains examples of inbound transactions.

* `<transaction_type>##.txt`: A textual descriptions of the transaction if one was provided by the source data set (mostly Appendix J)
* `<transaction_type>##.js`: The JSON payload published to the GP System Inbound Message queue. These use .js extensions so 
  comments can be used to explain specific parts of the message. These comments need to be removed if the example is to
  be used in a published message against as Javascript comments are not valid JSON.
* `<transaction_type>##.dat`: The EDIFACT file sent from the adaptor to the NHAIS instance for the request

## Amendments

| Possible Data Items | Example(s)
|-----------|---
| Surname | 
| First Forename |
| Second Forename |
| Other Forenames |
| Previous Surname | 
| Title | 
| Sex |
| Date of Birth |
| Address - 5 fields | 1
| Postcode | 
| Drugs Dispensed Marker | 
| RPP Mileage | n/a - DEPRECATED
| Blocked Route/Special District Marker | n/a - DEPRECATED
| Walking Units | n/a - DEPRECATED
| Residential Institute Code | 
