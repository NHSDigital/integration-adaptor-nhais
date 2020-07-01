//package uk.nhs.digital.nhsconnect.nhais.repository;
//
//import com.mongodb.DBObject;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
//import org.springframework.stereotype.Component;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class TransactionTypeConverter implements InitializingBean {
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        List<Converter> converters = new ArrayList<>();
//        converters.add(new Converter<DBObject, ReferenceTransactionType.TransactionType>() {
//            public ReferenceTransactionType.TransactionType convert(DBObject s) {
//                throw new UnsupportedOperationException("Not supported yet 1.");
//            }
//        });
//
//        converters.add(new Converter<ReferenceTransactionType.TransactionType, DBObject>() {
//            public DBObject convert(ReferenceTransactionType.TransactionType s) {
//                throw new UnsupportedOperationException("Not supported yet 2.");
//            }
//        });
//
//
//
//        converters.add(new Converter<DBObject, ReferenceTransactionType.Outbound>() {
//            public ReferenceTransactionType.Outbound convert(DBObject s) {
//                throw new UnsupportedOperationException("Not supported yet 1.");
//            }
//        });
//
//        converters.add(new Converter<ReferenceTransactionType.Outbound, DBObject>() {
//            public DBObject convert(ReferenceTransactionType.Outbound s) {
//                throw new UnsupportedOperationException("Not supported yet 2.");
//            }
//        });
//
//
//
//        converters.add(new Converter<DBObject, ReferenceTransactionType.Inbound>() {
//            public ReferenceTransactionType.Inbound convert(DBObject s) {
//                throw new UnsupportedOperationException("Not supported yet 1.");
//            }
//        });
//
//        converters.add(new Converter<ReferenceTransactionType.Inbound, DBObject>() {
//            public DBObject convert(ReferenceTransactionType.Inbound s) {
//                throw new UnsupportedOperationException("Not supported yet 2.");
//            }
//        });
//
//
//        MongoCustomConversions cc = new MongoCustomConversions(converters);
//
//        ((MappingMongoConverter)mongoTemplate.getConverter()).setCustomConversions(cc);
//    }
//}
