package de.zdf.service.pwValidation.client;

import de.zdf.service.pwValidation.client.util.TrainDataServiceUtil;
import de.zdf.service.pwValidation.data.TrainResponse;
import de.zdf.utils.test.wiremock.WiremockTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
//        ReflectionTestUtils.setField(trainDataServiceClient, "trainDataServiceUrl", wmockRule.getWiremockUrl());
        ReflectionTestUtils.invokeMethod(trainDataServiceClient, "registerJacksonWithUnirest");
        ReflectionTestUtils.setField(trainDataServiceClient, "trainDataServiceUtil", new TrainDataServiceUtil());
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

    // Testfall: Alle Sitze vorhanden, Buchung geht auf jeden Fall durch
    @Test
    public void trainDataAllSeatsAvailable() {
        boolean newTrainResponse = trainDataServiceClient.checkIfAllSeatsAreAvailable("local_1000");

        assertThat(newTrainResponse).isEqualTo(true);

    }

    // Testfall: Nicht genügend Sitze
//    @Test
//    public void trainDataCheckIfEnoughSeatsForBookingRequest() {
//        boolean newTrainResponse = trainDataServiceClient.checkIfEnoughSeatsForBookingRequest("local_1000");
//
//        assertThat(newTrainResponse).isEqualTo(true);
//
//    }

    // Testfall: Request an Reservierungsservice korrekt?
    @Test
    public void reservationRequestCorrectlyWorking() {
        List<String> dummySeatList = Arrays.asList("1A", "2A");

        boolean newTrainResponse = trainDataServiceClient.reserveSeats("local_1000", dummySeatList, "75bcd15");

        assertThat(newTrainResponse).isEqualTo(true);

    }

    @Test
    public void reserveSeat_seatShouldBeReservedAfterwards() {
        String bookingReference = "1234567";
        trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1A"), bookingReference);

        assertThat(trainDataServiceClient.retrieveNewTrainData("local_1000")
                .getSeats().get("1A").getBooking_reference()).isEqualTo(bookingReference);
    }
}