package uk.nhs.digital.nhsconnect.nhais.configuration.ttl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public abstract class TtlCreator {

    private final IndexOperations indexOperations;
    private final Duration duration;

    public abstract void create(Class<? extends TimeToLive> clazz);

    protected abstract Optional<IndexInfo> findTtlIndex();

    protected boolean ttlIndexHasChanged() {
        Optional<IndexInfo> ttlIndex = findTtlIndex();
        return ttlIndex.isPresent() && ttlIndex
            .flatMap(IndexInfo::getExpireAfter)
            .map(indexExpire -> indexExpire.compareTo(duration) != 0)
            .orElse(true);
    }
}
