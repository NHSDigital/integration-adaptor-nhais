package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import javax.swing.text.Document;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.client.MongoCollection;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MongoSchedulerTest {

    private static final String MESH_TIMESTAMP = "mesh_timestamp";
    private static final String TIMESTAMP = "timestamp";

    @InjectMocks
    MongoScheduler mongoScheduler;

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private MeshClient meshClient;

    @Test
    public void When_collectionDoesNotExist_Then_theCollectionAndSingleDocumentIsCreated_andTheJobIsNotExecuted() {
        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(false);

        verify(mongoOperations.save(ArgumentMatchers.isA(Document.class), ArgumentMatchers.isA(String.class)));

        verifyNoInteractions(meshClient);
    }

    @Test
    public void When_collectionIsEmpty_Then_singleDocumentIsCreated_andTheJobIsNotExecuted() {
        MongoCollection mongoCollection = mock(MongoCollection.class);
        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(true);
        when(mongoOperations.getCollection(MESH_TIMESTAMP)).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(0L);

        verifyNoInteractions(meshClient);
    }

    @Test
    public void When_documentExistsAndTimestampIsAfterFiveMinutesAgo_Then_documentIsUpdate_andTheJobIsExecuted() {
        when(meshClient.getInboxMessageIds()).thenReturn(List.of("messageId"));
        when(meshClient.getMessage("messageId")).thenReturn("something");
        MongoCollection mongoCollection = mock(MongoCollection.class);
        when(mongoOperations.collectionExists(MESH_TIMESTAMP)).thenReturn(true);
        when(mongoOperations.getCollection(MESH_TIMESTAMP)).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(1L);

//        verify mesh client was called
//        verify(meshClient)
    }



}
