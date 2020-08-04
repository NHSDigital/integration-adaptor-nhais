# Reporting missing GP Links transactions

This document first briefly introduces the sequence number mechanism with links to further reading.
Next, the three types of faults related to sequence numbers are described.
Finally, Mongodb queries to create reports detecting these faults are described.

## Introduction to Sequence Numbers

GP Links and EDIFACT use sequence numbers to identify and track the transmissions between systems. Each
new message creates a new sequence number that is one greater than the number used in the previous message.
Each sender/recipient pair has their own sequence. Any gaps in the sequence could indicate that a transmission was lost.

Additionally, for every outbound transaction NHAIS will reply with a RECEP receipt report message. A missing
RECEP for an outbound transaction could indicate that NHAIS has not received the transaction.

For more information refer to the [NHAIS developer document library](https://digital.nhs.uk/services/nhais/nhais-developer-document-library):

- Communications specifications v3
- Receipt report message

## Missing Outbound Transaction

It is possible, however unlikely, that the adaptor may generate a sequence number before encountering an exception 
and aborting the transaction. In such cases, the sequence number then becomes redundant and can cause 
the sequencing to break with gaps being sent onto NHAIS and stored within the adaptors state database.
When such errors are detected they should be correlated with the application logs to determine the cause of the error.
The NHAIS operator may need to be advised the reason for the missing sequence number.

A "Missing Sequence Number Report" on the `outboundState` collection can detect these faults.

## Missing RECEP for Outbound Transactions

It must be assumed that any outbound transaction that has not been matched to an inbound RECEP has not been processed by
NHAIS. For any such transaction the operator of the GP System must ask the NHAIS operator for the reason why the
message was not processed.

A "Missing RECEP Report" can detect these faults.

## Missing Inbound Transaction

It is possible that an inbound transaction was not processed successfully by the adaptor or lost in transmission. If any
missing inbound transaction is identified the GP System operator must check the dead-letter queues for any messages that
could not be processed successfully and correlate with the application logs to determine the cause of the error. If there
are no matching messages in the dead-letter queues then the GP System operator must ask the NHAIS operator for the reason
why the message was not received.

A "Missing Sequence Number Report" on the `inboundState` collection can detect these faults.

## Adaptor State Database Overview

This adaptor uses a persistence store (supports any MongoDB compatible database) configurable 
based on your own implementation/hosting preferences. 
This database records metadata of all sent messages including key sequence numbers:

- Interchange Sequence Number (SIS)
- Message Sequence Number (SMS)
- Transaction Number (TN)

Database schema information (only relevant fields listed):

    collection: 
        outboundState / inboundState
    fields:
        sndr
        recip
        translationTimestamp
        intSeq
        msgSeq
        tn
         
## Missing Sequence Number Report

To generate a report on missing sequence numbers use the following query:

    db.getCollection('<state_table>').aggregate(
        [
            {$match : {
                translationTimestamp : { $gt: ISODate('<from_timestamp>'), $lt: ISODate('<to_timestamp>') },
                sndr : "some_sender",
                recip : "some_recipient"
            }},
            {$group : {_id : null, min : {$min : "$<db_field>"}, max : {$max : "$<db_field>"}}},
            {$addFields : {allPossibleNumbers : {$range : ["$min", "$max"]}}},
            {$unwind : '$allPossibleNumbers'},
            {$lookup : {from : "<state_table>", localField : "allPossibleNumbers", foreignField : "<db_field>", as : "entries"}},
            {$match : {entries : { $size: 0 }}},
            {$group: { "_id": null, "missingNumbers": {"$push": "$allPossibleNumbers" }}}
        ]
    )
    
where

- `<state_table>` state table to run report on (one of: `[inboundState, outboundState]`)
- `<from_timestamp>` defines the "from date" of the report
- `<to_timestamp>` defines the "to date" of the report
- `<sndr>` trading partner code of the GP that sent the message
- `<recip>` trading partner code of the HA that the message was addressed to
- `<db_field>` the field to generate report for (one of: `[translationTimestamp, intSeq, msgSeq, tn]`)

yields a single result document:

    {
        "_id" : null,
        "missingIds" : [ <coma_separated_list_of_missing_numbers> ]
    }

### Missing Sequence Number Report Examples

Report missing `outbound` interchange sequences from `2020-01-01` to `2020-01-31` for GP `TES5` to HA `XX11`

    db.getCollection('outboundState').aggregate(
        [
            {$match : {
                translationTimestamp : { $gt: ISODate('2020-01-01 00:00:00.000Z'), $lt: ISODate('2020-02-01 00:00:00.000Z') },
                sndr : "TES5",
                recip : "XX11"
            }},
            {$group : {_id : null, min : {$min : "$intSeq"}, max : {$max : "$intSeq"}}},
            {$addFields : {allPossibleNumbers : {$range : ["$min", "$max"]}}},
            {$unwind : '$allPossibleNumbers'},
            {$lookup : {from : "outboundState", localField : "allPossibleNumbers", foreignField : "intSeq", as : "entries"}},
            {$match : {entries : { $size: 0 }}},
            {$group: { "_id": null, "missingNumbers": {"$push": "$allPossibleNumbers" }}}
        ]
    )

produces a list of missing interchange sequence numbers:

    {
        "_id" : null,
        "missingIds" : [ 2, 4, 5, 6, 8 ]
    }
        
Report missing `inbound` message sequences from `2020-03-15 15:59:15` to `2020-03-16 14:13:12` for GP `TES5` to HA `XX1`

    db.getCollection('inboundState').aggregate(
        [
            {$match : {
                translationTimestamp : { $gt: ISODate('2020-03-15 15:59:15.000Z'), $lt: ISODate('2020-03-16 14:13:12.000Z') },
                sndr : "TES5",
                recip : "XX1"
            }},
            {$group : {_id : null, min : {$min : "$msgSeq"}, max : {$max : "$msgSeq"}}},
            {$addFields : {allPossibleNumbers : {$range : ["$min", "$max"]}}},
            {$unwind : '$allPossibleNumbers'},
            {$lookup : {from : "inboundState", localField : "allPossibleNumbers", foreignField : "msgSeq", as : "entries"}},
            {$match : {entries : { $size: 0 }}},
            {$group: { "_id": null, "missingNumbers": {"$push": "$allPossibleNumbers" }}}
        ]
    )

produces a list of missing message sequence numbers:

    {
        "_id" : null,
        "missingIds" : [ 100, 134 ]
    }
    
## Missing RECEP Report

To generate a report on outbound transactions for which no RECEP has been received:

    db.getCollection('outboundState').find(
        {   
            $and: [
                {sndr: '<sender>'},
                {recip: '<recipient>'},
                {translationTimestamp: {$gt: ISODate('<from_timestamp>'), $lt: ISODate('<to_timestamp>')}},
                {$or: [{"recepCode": {"$exists":false}}, {"recepCode": null}, {"recepDateTime": {"$exists":false}}, {"recepDateTime" :null}]}
            ]
        }
    )
    
where

- `<sndr>` trading partner code of the GP that sent the message (optional)
- `<recip>` trading partner code of the HA that the message was addressed to (optional)
- `<from_timestamp>` defines the "from date" of the report
- `<to_timestamp>` defines the "to date" of the report
- `<sndr>` trading partner code of the GP that sent the message
- `<recip>` trading partner code of the HA that the message was addressed to

### Missing RECEP Report Examples

Report on interchanges that have not received RECEP from `2020-01-01` to `2020-01-31` for GP `TES5` to HA `XX1`

    db.getCollection('outboundState').find(
        {   
            $and: [
                {sndr: 'TES5'},
                {recip: 'XX1'},
                {translationTimestamp: {$gt: ISODate('2020-01-01 00:00:00.000Z'), $lt: ISODate('2020-02-01 00:00:00.000Z')}},
                {$or: [{"recepCode": {"$exists":false}}, {"recepCode": null}, {"recepDateTime": {"$exists":false}}, {"recepDateTime" :null}]}
            ]
        }
    )
        
Report on interchanges that have not received RECEP from `2020-01-01` to `2020-01-31` for all GP and HA pairs

    db.getCollection('outboundState').find(
        {   
            $and: [
                {translationTimestamp: {$gt: ISODate('2020-01-01 00:00:00.000Z'), $lt: ISODate('2020-02-01 00:00:00.000Z')}},
                {$or: [{"recepCode": {"$exists":false}}, {"recepCode": null}, {"recepDateTime": {"$exists":false}}, {"recepDateTime" :null}]}
            ]
        }
    )