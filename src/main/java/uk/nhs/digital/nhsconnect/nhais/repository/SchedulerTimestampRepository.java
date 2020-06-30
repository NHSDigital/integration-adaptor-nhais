package uk.nhs.digital.nhsconnect.nhais.repository;

    import org.springframework.data.repository.CrudRepository;
    import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerTimestampRepository extends CrudRepository<SchedulerTimestamp, String>, SchedulerTimestampRepositoryExtentions {
}
