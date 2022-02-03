package com.tnt.aggregationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.tnt.aggregationservice.dto.AggregationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.tnt.aggregationservice.util.TestData.AGGREGATION_ID_1;
import static com.tnt.aggregationservice.util.TestData.AGGREGATION_ID_2;
import static com.tnt.aggregationservice.util.TestData.AGGREGATION_ID_3;
import static com.tnt.aggregationservice.util.TestData.AGGREGATION_ID_4;
import static com.tnt.aggregationservice.util.TestData.AGGREGATION_ID_5;
import static com.tnt.aggregationservice.util.TestData.AGGREGATION_ID_6;
import static com.tnt.aggregationservice.util.TestData.CA;
import static com.tnt.aggregationservice.util.TestData.DE;
import static com.tnt.aggregationservice.util.TestData.FR;
import static com.tnt.aggregationservice.util.TestData.IT;
import static com.tnt.aggregationservice.util.TestData.NL;
import static com.tnt.aggregationservice.util.TestData.RU;
import static com.tnt.aggregationservice.util.TestData.getPricingResponse;
import static com.tnt.aggregationservice.util.TestData.getShipmentsResponse;
import static com.tnt.aggregationservice.util.TestData.getTrackResponse;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT2M")
@AutoConfigureWireMock(port = 0)
public class AggregationControllerIT {

    private static final String GET_AGGREGATION_URL = "/aggregation";
    private static final String PRICING_PARAM = "pricing";
    private static final String TRACK_PARAM = "track";
    private static final String SHIPMENTS_PARAM = "shipments";
    private static final String SHIPMENTS_URI = "/shipments";
    private static final String PRICING_URI = "/pricing";
    private static final String TRACK_URI = "/track";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Test
    @DisplayName("First request has 4 elements for each param, second request 2 for each param. The requests have to be throttled and triggered when bulk size is reached")
    void aggregate_throttledBulkTheRequests_whenFirstRequestHasLessParamsThenBulkSize() throws JsonProcessingException, ExecutionException, InterruptedException {

        var prices1 = List.of(NL, CA, DE, FR);
        var trackIds1 = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);
        var shipmentIds1 = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);


        mockEndpoint(PRICING_URI, jsonObjectMapper.writeValueAsString(getPricingResponse(prices1)), Scenario.STARTED, "FIRST");
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(trackIds1)), Scenario.STARTED, "FIRST");
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(shipmentIds1)), Scenario.STARTED, "FIRST");

        var prices2 = List.of(IT, RU);
        var trackIds2 = List.of(AGGREGATION_ID_5, AGGREGATION_ID_6);
        var shipmentIds2 = List.of(AGGREGATION_ID_5, AGGREGATION_ID_6);

        mockEndpoint(PRICING_URI, jsonObjectMapper.writeValueAsString(getPricingResponse(prices2)),  "FIRST", Scenario.STARTED);
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(trackIds2)), "FIRST", Scenario.STARTED);
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(shipmentIds2)), "FIRST", Scenario.STARTED);

        CompletableFuture.allOf(
                CompletableFuture.supplyAsync( () ->  webTestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(GET_AGGREGATION_URL)
                                .queryParam(PRICING_PARAM, prices1)
                                .queryParam(TRACK_PARAM, trackIds1)
                                .queryParam(SHIPMENTS_PARAM, shipmentIds1)
                                .build())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AggregationDTO.class)
                        .returnResult().getResponseBody()),

                CompletableFuture.supplyAsync( () ->  webTestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(GET_AGGREGATION_URL)
                                .queryParam(PRICING_PARAM, prices2)
                                .queryParam(TRACK_PARAM, trackIds2)
                                .queryParam(SHIPMENTS_PARAM, shipmentIds2)
                                .build())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AggregationDTO.class)
                        .returnResult().getResponseBody())
        ).get();
    }

    @Test
    @DisplayName("First request has 4 elements for each param, second request 2 for each param. The requests should complete in less then 10 sec")
    void aggregate_throttledBulkTheRequests_shouldMeetSLA() throws JsonProcessingException {

        var prices1 = List.of(NL, CA, DE, FR);
        var trackIds1 = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);
        var shipmentIds1 = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);


        mockEndpoint(PRICING_URI, jsonObjectMapper.writeValueAsString(getPricingResponse(prices1)), Scenario.STARTED, "FIRST");
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(trackIds1)), Scenario.STARTED, "FIRST");
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(shipmentIds1)), Scenario.STARTED, "FIRST");

        var prices2 = List.of(IT, RU);
        var trackIds2 = List.of(AGGREGATION_ID_5, AGGREGATION_ID_6);
        var shipmentIds2 = List.of(AGGREGATION_ID_5, AGGREGATION_ID_6);

        mockEndpoint(PRICING_URI, jsonObjectMapper.writeValueAsString(getPricingResponse(prices2)),  "FIRST", Scenario.STARTED);
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(trackIds2)), "FIRST", Scenario.STARTED);
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(shipmentIds2)), "FIRST", Scenario.STARTED);

        Assertions.assertTimeout(Duration.ofSeconds(10) ,() -> CompletableFuture.allOf(
                CompletableFuture.supplyAsync( () ->  webTestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(GET_AGGREGATION_URL)
                                .queryParam(PRICING_PARAM, prices1)
                                .queryParam(TRACK_PARAM, trackIds1)
                                .queryParam(SHIPMENTS_PARAM, shipmentIds1)
                                .build())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AggregationDTO.class)
                        .returnResult().getResponseBody()),

                CompletableFuture.supplyAsync( () ->  webTestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(GET_AGGREGATION_URL)
                                .queryParam(PRICING_PARAM, prices2)
                                .queryParam(TRACK_PARAM, trackIds2)
                                .queryParam(SHIPMENTS_PARAM, shipmentIds2)
                                .build())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AggregationDTO.class)
                        .returnResult().getResponseBody()))
                .get()
        );
    }

    @Test
    @DisplayName("Request has less elements for each param, it should be triggered when throttled timeout is reached")
    void aggregate_shouldRequestDataAfterThrottleTimout_whenInputParamsAreLessThenBulkSize() throws JsonProcessingException {
        var prices = List.of(NL, CA);
        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2);
        var shipmentIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2);


        mockEndpoint(PRICING_URI, jsonObjectMapper.writeValueAsString(getPricingResponse(prices)));
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(trackIds)));
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(shipmentIds)));

        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(PRICING_PARAM, prices)
                        .queryParam(TRACK_PARAM, trackIds)
                        .queryParam(SHIPMENTS_PARAM, shipmentIds)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AggregationDTO.class)
                .returnResult().getResponseBody();

        System.out.println(response);
    }

    @Test
    @DisplayName("Request more then bulk size elements for each param, it should  trigger the request immediately")
    void aggregate_shouldReturnOKAndValidResult_whenInputParamsAreValid() throws JsonProcessingException {
        var prices = List.of(NL, CA, DE, FR, IT);
        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);
        var shipmentIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);


        mockEndpoint(PRICING_URI, jsonObjectMapper.writeValueAsString(getPricingResponse(prices)));
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(trackIds)));
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(shipmentIds)));


        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(PRICING_PARAM, prices)
                        .queryParam(TRACK_PARAM, trackIds)
                        .queryParam(SHIPMENTS_PARAM, shipmentIds)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AggregationDTO.class)
                .returnResult().getResponseBody();

        System.out.println(response);
    }

    @Test
    @DisplayName("Request only one param, it should  trigger the request and return empty for other params")
    void aggregate_shouldReturnOKAndValidResult_whenOnlyOneParamIsPassed() throws JsonProcessingException {

        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(trackIds)));

        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(TRACK_PARAM, trackIds)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AggregationDTO.class)
                .returnResult().getResponseBody();

        System.out.println(response);
    }

    @Test
    @DisplayName("One Internal fetch request fail with 503, it should return null values for failed resource")
    void aggregate_shouldReturnOKAndValidResult_whenOneRequestFails() throws JsonProcessingException {
        var prices = List.of(NL, CA, DE, FR, IT);
        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);
        var shipmentIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);
        mockEndpoint(SHIPMENTS_URI, jsonObjectMapper.writeValueAsString(getShipmentsResponse(trackIds)));
        mockEndpoint(TRACK_URI, jsonObjectMapper.writeValueAsString(getTrackResponse(shipmentIds)));
        mockEndpointStatus(PRICING_URI, HttpStatus.SERVICE_UNAVAILABLE);

        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(PRICING_PARAM, prices)
                        .queryParam(TRACK_PARAM, trackIds)
                        .queryParam(SHIPMENTS_PARAM, shipmentIds)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AggregationDTO.class)
                .returnResult().getResponseBody();

        System.out.println(response);
    }

    @Test
    @DisplayName("All Internal fetch request fail with 503, it should return all with null values")
    void aggregate_shouldReturnEmptyValidResult_whenAllRequestFail() {

        var prices = List.of(NL, CA, DE, FR, IT);
        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);
        var shipmentIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4, AGGREGATION_ID_5);

        mockEndpointStatus(PRICING_URI, HttpStatus.SERVICE_UNAVAILABLE);
        mockEndpointStatus(SHIPMENTS_URI, HttpStatus.SERVICE_UNAVAILABLE);
        mockEndpointStatus(TRACK_URI, HttpStatus.SERVICE_UNAVAILABLE);

        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(PRICING_PARAM, prices)
                        .queryParam(TRACK_PARAM,trackIds)
                        .queryParam(SHIPMENTS_PARAM, shipmentIds)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AggregationDTO.class)
                .returnResult().getResponseBody();

        System.out.println(response);
    }

    private void mockEndpointStatus(String trackUri, HttpStatus status) {

        givenThat(WireMock.get(urlPathEqualTo(trackUri))
                .willReturn(aResponse()
                        .withStatus(status.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        );
    }

    private void mockEndpoint(String endpoint, String body) {
        givenThat(WireMock.get(urlPathEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(body)
                )
        );
    }

    private void mockEndpoint(String endpoint, String body, String currentState, String nextState) {
        givenThat(WireMock.get(urlPathEqualTo(endpoint))
                .inScenario(endpoint)
                .whenScenarioStateIs(currentState)
                .willSetStateTo(nextState)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(body)
                )
        );
    }
}
