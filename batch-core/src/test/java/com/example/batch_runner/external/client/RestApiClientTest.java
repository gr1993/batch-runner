package com.example.batch_runner.external.client;

import com.example.batch_runner.external.dto.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class RestApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private RestApiClient restApiClient;

    @BeforeEach
    void setUp() {
        restApiClient = new RestApiClient(restTemplate, new ObjectMapper());
    }

    @Test
    public void getApiForXml() {
        // given
        String serviceKey = "serviceKey";
        String busRouteId = "119900022";
        String url = String.format(
                "http://ws.bus.go.kr/api/rest/buspos/getBusPosByRtid?serviceKey=%s&busRouteId=%s",
                serviceKey, busRouteId
        );

        @SuppressWarnings("unchecked")
        ResponseEntity<String> response = (ResponseEntity<String>) Mockito.mock(ResponseEntity.class);
        HttpStatusCode okCode = HttpStatus.OK;
        Mockito.when(response.getStatusCode())
                .thenReturn(okCode);

        String xmlData = """
                <ServiceResult>
                    <comMsgHeader/>
                    <msgHeader>
                        <headerCd>0</headerCd>
                        <headerMsg>정상적으로 처리되었습니다.</headerMsg>
                        <itemCount>0</itemCount>
                    </msgHeader>
                    <msgBody>
                        <itemList>
                            <busType>1</busType>
                            <congetion>0</congetion>
                            <dataTm>20250521134915</dataTm>
                            <fullSectDist>0.315</fullSectDist>
                            <gpsX>126.923976</gpsX>
                            <gpsY>37.500662</gpsY>
                            <isFullFlag>0</isFullFlag>
                            <islastyn>0</islastyn>
                            <isrunyn>1</isrunyn>
                            <lastStTm>2226</lastStTm>
                            <lastStnId>119900119</lastStnId>
                            <nextStId>0</nextStId>
                            <nextStTm>0</nextStTm>
                            <plainNo>서울75사8713</plainNo>
                            <posX>193278.46589861516</posX>
                            <posY>444586.24915255466</posY>
                            <rtDist>11.7</rtDist>
                            <sectDist>0.252</sectDist>
                            <sectOrd>11</sectOrd>
                            <sectionId>119600469</sectionId>
                            <stopFlag>0</stopFlag>
                            <trnstnid>119900242</trnstnid>
                            <vehId>120004457</vehId>
                        </itemList>
                    </msgBody>
                </ServiceResult>
                """;
        Mockito.when(response.getBody())
                .thenReturn(xmlData);

        Mockito.when(restTemplate.exchange(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(response);

        // when
        ServiceResult result = restApiClient.get(url, ServiceResult.class);

        // then
        assertEquals(1, result.getMsgBody().getItemList().size());
    }

    @Test
    public void getApiForJson() {
        //given
        String url = "https://jsonplaceholder.typicode.com/users";
        @SuppressWarnings("unchecked")
        ResponseEntity<String> response = (ResponseEntity<String>) Mockito.mock(ResponseEntity.class);
        HttpStatusCode okCode = HttpStatus.OK;
        Mockito.when(response.getStatusCode())
                .thenReturn(okCode);

        String jsonData = """
                [
                  {
                    "id": 1,
                    "name": "Leanne Graham",
                    "username": "Bret",
                    "email": "Sincere@april.biz",
                    "address": {
                      "street": "Kulas Light",
                      "suite": "Apt. 556",
                      "city": "Gwenborough",
                      "zipcode": "92998-3874",
                      "geo": {
                        "lat": "-37.3159",
                        "lng": "81.1496"
                      }
                    },
                    "phone": "1-770-736-8031 x56442",
                    "website": "hildegard.org",
                    "company": {
                      "name": "Romaguera-Crona",
                      "catchPhrase": "Multi-layered client-server neural-net",
                      "bs": "harness real-time e-markets"
                    }
                  }
                ]
                """;
        Mockito.when(response.getBody())
                .thenReturn(jsonData);

        Mockito.when(restTemplate.exchange(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(response);

        //when
        String result = restApiClient.get(url, String.class);

        //then
        assertFalse(StringUtils.isEmpty(result));
    }

    @Test
    public void postApiForJson() {
        //given
        String url = "https://jsonplaceholder.typicode.com/posts";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "홍길동");

        @SuppressWarnings("unchecked")
        ResponseEntity<String> response = (ResponseEntity<String>) Mockito.mock(ResponseEntity.class);
        HttpStatusCode okCode = HttpStatus.OK;
        Mockito.when(response.getStatusCode())
                .thenReturn(okCode);

        String jsonData = """
                {
                    "title": "홍길동",
                    "id": 101
                }
                """;
        Mockito.when(response.getBody())
                .thenReturn(jsonData);

        Mockito.when(restTemplate.exchange(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(response);

        //given
        String result = restApiClient.post(url, requestBody, String.class);

        //then
        assertFalse(StringUtils.isEmpty(result));
    }
}
