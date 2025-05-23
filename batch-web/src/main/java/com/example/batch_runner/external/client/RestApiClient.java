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
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    new URI(url), HttpMethod.GET, null, String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HTTP 응답 코드 오류: " + response.getStatusCode());
            }

            if (responseType == String.class) {
                return responseType.cast(response.getBody());
            }
            return parseBody(response.getBody(), responseType);

        } catch (Exception e) {
            throw new RuntimeException("GET 요청 실패", e);
        }
    }

    public <T> T post(String url, Object requestBody, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    new URI(url), HttpMethod.POST, requestEntity, String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HTTP 응답 코드 오류: " + response.getStatusCode());
            }

            if (responseType == String.class) {
                return responseType.cast(response.getBody());
            }
            return parseBody(response.getBody(), responseType);

        } catch (Exception e) {
            throw new RuntimeException("POST 요청 실패", e);
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
