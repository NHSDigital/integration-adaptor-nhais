//package uk.nhs.digital.nhsconnect.nhais.uat;
//
//import org.junit.jupiter.api.extension.ParameterContext;
//import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
//import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
//import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
//
//public class TestDataAggregator implements ArgumentsAggregator {
//
//    @Override
//    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
//        return TestData.builder()
//            .transactionType(accessor.get)
//            accessor.getString(1), accessor.getString(2), accessor.getString(3));
//    }
//}
