package de.zdf.service.pwValidation.client;

import de.zdf.service.pwValidation.data.TrainResponse;
import de.zdf.utils.test.wiremock.WiremockTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TrainDataServiceClientTest {
    @Rule
    public WiremockTestRule wmockRule = WiremockTestRule.builder()
            .targetUrl("http://localhost:8081")
            .recordMode(true)
            .build();

    private TrainDataServiceClient trainDataServiceClient;

    @Before
    public void setup() {
        trainDataServiceClient = new TrainDataServiceClient();
        ReflectionTestUtils.setField(trainDataServiceClient, "trainDataServiceUrl", wmockRule.getWiremockUrl());
        ReflectionTestUtils.invokeMethod(trainDataServiceClient, "registerJacksonWithUnirest");
    }

    @Test
    public void trainDataServiceWorking() {
        TrainResponse newTrainResponse = trainDataServiceClient.retrieveNewTrainData("local_1000");

        assertThat(newTrainResponse).isNotNull();
        assertThat(newTrainResponse.getSeats().size()).isGreaterThan(4);
    }

    @Test
    public void trainDataCorrectlyParsed() {
        TrainResponse newTrainResponse = trainDataServiceClient.retrieveNewTrainData("local_1000");

        assertThat(newTrainResponse).hasFieldOrProperty("seats");
        assertThat(newTrainResponse.getSeats()).containsKeys("1A");
        assertThat(newTrainResponse.getSeats().get("1A"))
                .hasFieldOrPropertyWithValue("coach", "A")
                .hasFieldOrPropertyWithValue("seat_number", "1")
                .hasFieldOrPropertyWithValue("booking_reference", "");

    }

}