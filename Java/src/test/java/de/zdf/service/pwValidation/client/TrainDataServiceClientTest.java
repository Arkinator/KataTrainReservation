package de.zdf.service.pwValidation.client;

import com.github.tomakehurst.wiremock.http.Fault;
import de.zdf.service.pwValidation.client.util.TrainDataServiceUtil;
import de.zdf.service.pwValidation.data.TrainDataServiceResponse;
import de.zdf.utils.test.wiremock.WiremockTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

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
        ReflectionTestUtils.setField(trainDataServiceClient, "trainDataServiceUtil", new TrainDataServiceUtil());

        trainDataServiceClient.resetReservations("local_1000");
    }

    @Test
    public void trainDataServiceWorking() {
        TrainDataServiceResponse newTrainDataServiceResponse = trainDataServiceClient.retrieveNewTrainData("local_1000");

        assertThat(newTrainDataServiceResponse).isNotNull();
        assertThat(newTrainDataServiceResponse.getSeats().size()).isGreaterThan(4);
    }

    @Test
    public void trainDataCorrectlyParsed() {
        TrainDataServiceResponse newTrainDataServiceResponse = trainDataServiceClient.retrieveNewTrainData("local_1000");

        assertThat(newTrainDataServiceResponse).hasFieldOrProperty("seats");
        assertThat(newTrainDataServiceResponse.getSeats()).containsKeys("1A");
        assertThat(newTrainDataServiceResponse.getSeats().get("1A"))
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
        String bookingReference = "1234568";
        trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1A"), bookingReference);

        assertThat(trainDataServiceClient.retrieveNewTrainData("local_1000")
                .getSeats().get("1A").getBooking_reference()).isEqualTo(bookingReference);
    }

    @Test
    public void resetReservation() {
        boolean newTrainResponse = trainDataServiceClient.resetReservations("local_1000");

        assertThat(newTrainResponse).isEqualTo(true);

    }

    @Test
    public void allSeatsAvailableAfterReset() {
        //given
        trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1A"), "12346");

        //execute
        trainDataServiceClient.resetReservations("local_1000");

        //assert
        assertThat(trainDataServiceClient.checkIfAllSeatsAreAvailable("local_1000")).isEqualTo(true);
    }

    @Test
    public void noBookingOfSeatwithExistingBookingReference() {
        trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1A"), "123456");

        assertThat(trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1A"), "654321"))
                .isEqualTo(false);
    }

    @Test
    public void reservationServiceWorking() {
        stubFor(post(urlEqualTo("/reserve"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1A"), "12346"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("offline");
    }
}