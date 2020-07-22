# Missing interchange sequence detection

It is possible, however unlikely, that the adaptor may generate a sequence number before encountering an exception 
and aborting the transaction. In such cases, the sequence number then becomes redundant and can cause 
the sequencing to break with gaps being sent onto the HA and stored within the adaptors state database. 
This could happen for both Outbound and Inbound transactions.

This document describes how to produce a report on missing sequence numbers.

## Database

NHAIS is using MongoDb as persistence store. 
This database records metadata of all sent messages including key sequence numbers:

- Interchange Sequence Number (SIS)
- Message Sequence Number (SMS)
- Transaction Number (TN)

Database schema information (only relevant fields listed):

    collection: 
        outboundState
    fields:
        sender
        recipient
        transactionTimestamp
        interchangeSequence
        messageSequence
        transactionId
         
## Report

To generate a report on missing sequence numbers use the following query:

    db.getCollection('outboundState').aggregate(
        [
            {$match : {
                transactionTimestamp : { $gt: ISODate('<from_timestamp>'), $lt: ISODate('<to_timestamp>') },
                sender : "some_sender",
                recipient : "some_recipient"
            }},
            {$group : {_id : null, min : {$min : "$<db_field>"}, max : {$max : "$<db_field>"}}},
            {$addFields : {allPossibleNumbers : {$range : ["$min", "$max"]}}},
            {$unwind : '$allPossibleNumbers'},
            {$lookup : {from : "outboundState", localField : "allPossibleNumbers", foreignField : "<db_field>", as : "entries"}},
            {$match : {entries : { $size: 0 }}},
            {$group: { "_id": null, "missingNumbers": {"$push": "$allPossibleNumbers" }}}
        ]
    )
    
where

- `<from_timestamp>` defines the "from date" of the report
- `<to_timestamp>` defines the "to date" of the report
- `<sender>` trading partner code of the GP that sent the message
- `<recipient>` trading partner code of the HA that the message was addressed to
- `<db_field>` the field to generate report for (one of: `[transactionTimestamp, interchangeSequence, messageSequence]`)

yields a single result document:

    {
        "_id" : null,
        "missingIds" : [ <coma_separated_list_of_missing_numbers> ]
    }

### Examples:

1. Reporting missing interchange sequences from `2020-01-01` to `2020-01-31` for GP `TES5` to HA `XX1`

        db.getCollection('outboundState').aggregate(
            [
                {$match : {
                    transactionTimestamp : { $gt: ISODate('2020-01-01 00:00:00.000Z'), $lt: ISODate('2020-02-01 00:00:00.000Z') },
                    sender : "TES5",
                    recipient : "XX1"
                }},
                {$group : {_id : null, min : {$min : "$interchangeSequence"}, max : {$max : "$interchangeSequence"}}},
                {$addFields : {allPossibleNumbers : {$range : ["$min", "$max"]}}},
                {$unwind : '$allPossibleNumbers'},
                {$lookup : {from : "outboundState", localField : "allPossibleNumbers", foreignField : "interchangeSequence", as : "entries"}},
                {$match : {entries : { $size: 0 }}},
                {$group: { "_id": null, "missingNumbers": {"$push": "$allPossibleNumbers" }}}
            ]
        )
        
results with:

    {
        "_id" : null,
        "missingIds" : [ 2, 4, 5, 6, 8 ]
    }
        
2. Reporting missing message sequences from `2020-03-15 15:59:15` to `2020-03-16 14:13:12` for GP `TES5` to HA `XX1`

        db.getCollection('outboundState').aggregate(
            [
                {$match : {
                    transactionTimestamp : { $gt: ISODate('2020-03-15 15:59:15.000Z'), $lt: ISODate('2020-03-16 14:13:12.000Z') },
                    sender : "TES5",
                    recipient : "XX1"
                }},
                {$group : {_id : null, min : {$min : "$messageSequence"}, max : {$max : "$messageSequence"}}},
                {$addFields : {allPossibleNumbers : {$range : ["$min", "$max"]}}},
                {$unwind : '$allPossibleNumbers'},
                {$lookup : {from : "outboundState", localField : "allPossibleNumbers", foreignField : "messageSequence", as : "entries"}},
                {$match : {entries : { $size: 0 }}},
                {$group: { "_id": null, "missingNumbers": {"$push": "$allPossibleNumbers" }}}
            ]
        )

results with:

    {
        "_id" : null,
        "missingIds" : [ 100, 134 ]
    }