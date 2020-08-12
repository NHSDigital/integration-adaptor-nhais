package uk.nhs.digital.nhsconnect.nhais.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TtlConfiguration {

    public static final String TTL_INDEX_NAME = "TTL";
    private static final String TTL_FIELD_NAME = "translationTimestamp";

    private final NhaisMongoClientConfiguration mongoConfig;
    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        createTimeToLiveIndex(InboundState.class);
        createTimeToLiveIndex(OutboundState.class);
    }

    private void createTimeToLiveIndex(Class<?> clazz) {
        var duration = Duration.parse(mongoConfig.getTtl());
        var indexOperations = mongoTemplate.indexOps(clazz);

        if (ttlIndexExists(indexOperations) && ttlIndexHasChanged(indexOperations)) {
            LOGGER.info("TTL value has changed for {} - dropping index and creating new one using value {}", clazz.getSimpleName(), duration);
            indexOperations.dropIndex(TTL_INDEX_NAME);
        }
        indexOperations.ensureIndex(
            new Index()
                .expire(duration)
                .named(TTL_INDEX_NAME)
                .on(TTL_FIELD_NAME, Sort.Direction.ASC)
        );
    }

    private boolean ttlIndexExists(IndexOperations indexOperations) {
        return indexOperations.getIndexInfo().stream()
            .anyMatch(index -> TTL_INDEX_NAME.equals(index.getName()));
    }

    private boolean ttlIndexHasChanged(IndexOperations indexOperations) {
        return indexOperations.getIndexInfo().stream()
            .filter(index -> TTL_INDEX_NAME.equals(index.getName()))
            .map(IndexInfo::getExpireAfter)
            .flatMap(Optional::stream)
            .noneMatch(indexExpire -> indexExpire.compareTo(Duration.parse(mongoConfig.getTtl())) == 0);
    }
}
