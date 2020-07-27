package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshRequests {

    private final MeshConfig meshConfig;

    private final MeshHeaders meshHeaders;

    public HttpPost sendMessage(String recipient, WorkflowId workflowId){
        var request = new HttpPost(meshConfig.getHost() + meshConfig.getMailboxId() + "/outbox/");
        request.setHeaders(meshHeaders.createSendHeaders(recipient, workflowId));
        return request;
    }

    public HttpGet getMessage(String messageId){
        var request = new HttpGet(meshConfig.getHost() + meshConfig.getMailboxId() + "/inbox/" + messageId);
        request.setHeaders(meshHeaders.createMinimalHeaders());
        return request;
    }

    public HttpGet getMessageIds(){
        var request = new HttpGet(meshConfig.getHost() + meshConfig.getMailboxId() + "/inbox");
        request.setHeaders(meshHeaders.createMinimalHeaders());
        return request;
    }

    public HttpPut acknowledge(String messageId){
        var request = new HttpPut(meshConfig.getHost() + meshConfig.getMailboxId() + "/inbox/" + messageId + "/status/acknowledged");
        request.setHeaders(meshHeaders.createMinimalHeaders());
        return request;
    }

}
