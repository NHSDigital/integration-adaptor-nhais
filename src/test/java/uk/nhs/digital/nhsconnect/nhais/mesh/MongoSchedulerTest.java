package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.bson.Document;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.UpdateResult;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MongoSchedulerTest {

    private static final String MESH_TIMESTAMP = "mesh_timestamp";

    @InjectMocks
    MongoScheduler mongoScheduler;

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private MeshClient meshClient;

    @Mock
    private Document document;

    @Mock
    private UpdateResult updateResult;

    @Mock
    private MongoCollection mongoCollection;

    @Test
    public void WhenCollectionDoesNotExistThenTheCollectionAndSingleDocumentIsCreatedAndTheJobIsNotExecuted() {
        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(false);
        when(mongoOperations.save(any(Document.class), any(String.class))).thenReturn(document);

        mongoScheduler.updateConditionally();

        verify(mongoOperations).save(any(), any());
        verifyNoInteractions(meshClient);
    }

    @Test
    public void WhenCollectionIsEmptyThenSingleDocumentIsCreatedAndTheJobIsNotExecuted() {
        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(true);
        when(mongoOperations.getCollection(MESH_TIMESTAMP)).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(0L);

        mongoScheduler.updateConditionally();

        verify(mongoOperations).save(any(), any());
        verifyNoInteractions(meshClient);
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsBeforeFiveMinutesAgoThenDocumentIsUpdateAndTheJobIsExecuted() {
        when(meshClient.getInboxMessageIds()).thenReturn(List.of("messageId"));
        when(meshClient.getMessage("messageId")).thenReturn("something");

        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(true);
        when(mongoOperations.getCollection(MESH_TIMESTAMP)).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(1L);
        when(mongoOperations.updateFirst(any(Query.class), any(Update.class), any(String.class))).thenReturn(updateResult);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        mongoScheduler.updateConditionally();

        verify(meshClient).getMessage(any(String.class));
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsAfterFiveMinutesAgoThenDocumentIsNotUpdateAndTheJobIsNotExecuted() {
        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(true);
        when(mongoOperations.getCollection(MESH_TIMESTAMP)).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(1L);
        when(mongoOperations.updateFirst(any(Query.class), any(Update.class), any(String.class))).thenReturn(updateResult);
        when(updateResult.getModifiedCount()).thenReturn(0L);

        mongoScheduler.updateConditionally();

        verifyNoInteractions(meshClient);
    }
}
