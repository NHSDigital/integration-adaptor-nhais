package uk.nhs.digital.nhsconnect.nhais.mesh;

import java.io.IOException;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
class MeshResponse {
    private final CloseableHttpResponse response;

    public <T> T parseInto(Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.reader().createParser(EntityUtils.toString(response.getEntity()));
        return objectMapper.readValue(parser, clazz);
    }

}
