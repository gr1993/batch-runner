package com.example.batch_runner.external.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class RestApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public <T> T get(String url, Class<T> responseType) {
        return request(HttpMethod.GET, url, null, responseType);
    }

    public void post(String url, Object requestBody) {
        request(HttpMethod.POST, url, requestBody, String.class);
    }

    public void put(String url, Object requestBody) {
        request(HttpMethod.PUT, url, requestBody, String.class);
    }

    public void delete(String url) {
        request(HttpMethod.DELETE, url, null, String.class);
    }

    private <T> T request(HttpMethod httpMethod, String url, Object requestBody, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Object> requestEntity = null;

            if (requestBody != null) {
                headers.setContentType(MediaType.APPLICATION_JSON);
                requestEntity = new HttpEntity<>(requestBody, headers);
            }

            ResponseEntity<String> response = restTemplate.exchange(
                    new URI(url), httpMethod, requestEntity, String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HTTP 응답 코드 오류: " + response.getStatusCode());
            }

            if (responseType == String.class) {
                return responseType.cast(response.getBody());
            }
            return parseBody(response.getBody(), responseType);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parseBody(String body, Class<T> responseType) throws Exception {
        if (body == null || body.trim().isEmpty()) {
            throw new Exception("응답 본문이 비어 있습니다.");
        }

        // XML인지 JSON인지 판단해서 파싱
        if (isXml(body)) {
            JAXBContext context = JAXBContext.newInstance(responseType);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(body));
        } else {
            return objectMapper.readValue(body, responseType);
        }
    }

    private boolean isXml(String body) {
        return body.trim().startsWith("<");
    }
}
