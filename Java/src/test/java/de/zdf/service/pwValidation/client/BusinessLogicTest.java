package de.zdf.service.pwValidation.client;

import de.zdf.service.pwValidation.client.util.TrainDataServiceUtil;
import de.zdf.service.pwValidation.data.ReservationResponse;
import de.zdf.service.pwValidation.service.TicketOfficeService;
import de.zdf.utils.test.wiremock.WiremockTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BusinessLogicTest {
    @Rule
    public WiremockTestRule wmockRule = WiremockTestRule.builder()
            .targetUrl("http://localhost:8081")
            .recordMode(false)
            .build();

    @Autowired
    private TrainDataServiceClient trainDataServiceClient;
    @Autowired
    private BookingReferenceServiceClient bookingReferenceServiceClient;
    @Autowired
    private TicketOfficeService ticketOfficeService;

    @Before
    public void setup() {
        //ReflectionTestUtils.setField(bookingReferenceServiceClient, "bookingReferenceServiceUrl", wmockRule.getWiremockUrl());
        //ReflectionTestUtils.setField(trainDataServiceClient, "trainDataServiceUrl", wmockRule.getWiremockUrl());

        trainDataServiceClient.resetReservations("local_1000");
    }

    @Test
    public void reserveSingleSeatInEmptyTrain_ShouldReturnOkResponse() {
        ReservationResponse reservationResponse = ticketOfficeService.makeReservation("local_1000", 1);

        assertThat(reservationResponse.getBooking_reference()).isNotEmpty();
        assertThat(reservationResponse.getSeats())
                .hasSize(1)
                .contains("1A");
        assertThat(reservationResponse.getTrainId()).isEqualTo("local_1000");
    }

    @Test
    public void reserveMultipleSeatsTwice_ShouldReturnOkResponse() {
        ReservationResponse reservationResponse = ticketOfficeService.makeReservation("local_1000", 4);

        reservationResponse = ticketOfficeService.makeReservation("local_1000", 4);

        assertThat(reservationResponse.getBooking_reference()).isNotEmpty();
        assertThat(reservationResponse.getSeats())
                .hasSize(4)
                .contains("1B");
        assertThat(reservationResponse.getTrainId()).isEqualTo("local_1000");
    }

    @Test
    public void reserveMultipleSeats_ShouldReturnMultipleSeats() {
        ReservationResponse reservationResponse = ticketOfficeService.makeReservation("local_1000", 4);

        assertThat(reservationResponse.getBooking_reference()).isNotEmpty();
        assertThat(reservationResponse.getSeats())
                .hasSize(4)
                .contains("1A");
        assertThat(reservationResponse.getTrainId()).isEqualTo("local_1000");
    }

    @Test
    public void reserveSeatsInSecondCar_ShouldReturnMultipleSeats() {
        ReservationResponse reservationResponse = ticketOfficeService.makeReservation("local_1000", 7);

        assertThat(reservationResponse.getBooking_reference()).isNotEmpty();
        assertThat(reservationResponse.getSeats())
                .hasSize(7)
                .contains("1B","7B");
        assertThat(reservationResponse.getTrainId()).isEqualTo("local_1000");
    }

    @Test
    public void reservingTooManySeatsInOneGo_ShouldReturnException() {
        assertThatThrownBy(() -> ticketOfficeService.makeReservation("local_1000", 1000))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("1000")
                .hasMessageContaining("too many");
    }

    @Test
    public void reservingMoreSeatsThenThereAreInASingleCoach_ShouldThrowException() {
        assertThatThrownBy(() -> ticketOfficeService.makeReservation("local_1000", 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("10")
                .hasMessageContaining("too many");
    }

    @Test
    public void tooManyReservationsBeingDone_ShouldReturnException() {
        assertThatThrownBy(() -> {
            for (int i = 0; i < 100; i++)
                ticketOfficeService.makeReservation("local_1000", 1);
        })
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("1")
                .hasMessageContaining("too many");
    }

    // Nicht mehr als 70% der Sitze eines Zuges können reserviert werden
    @Test
    public void reserveMaximumOfSeatsPerTrain() {
        // prepare
        // Mind. 70% der Sitze eines Zuges buchen
        List<String> dummySeatList = Arrays.asList("1A", "2A", "3A", "4A", "1B", "2B", "3B", "4B", "5B", "6B", "7B", "8B");
        trainDataServiceClient.reserveSeats("local_1000", dummySeatList, "75bcd15");


        // execute
        // Mind. einen weiteren Platz dieses Zuges buchen
//        boolean newTrainResponse = trainDataServiceClient.reserveSeats("local_1000", Collections.singletonList("1C"), "75bcd15");
        ReservationResponse reservationResponse = ticketOfficeService.makeReservation("local_1000", 4);

        // assert
        // Hinweis, dass dies nicht möglich ist
        // assertThat(newTrainResponse).isEqualTo(false);
    }


    // Nicht mehr als 70% der Sitze eines Waggons sollten reserviert werden, kann aber passieren
    @Test
    public void reserveMaximumOfSeatsPerWaggon() {
        // prepare
        // Mind. 70% der Sitze eines Waggons buchen

        // execute
        // Mind. einen weiteren Platz dieses Zuges buchen

        // assert
        // Hinweis, dass dies nicht möglich ist
        // Hier müssten zwei Fälle unterschieden werden:
        // 1. Der Zug ist bereits zu 70% ausgebucht, dann geht das nicht
        // 2. Der Zug ist noch nicht zu 70% ausgebucht, dann darf ein einzelner Waggon über 70% bebucht werden
        // Zwei Tests?
    }

    // Buchungen mehrerer Plätze sollen in einem Waggon erfolgen
    @Test
    public void reserveAllSeatsInOneWaggon() {
        // prepare
        // Nichts bzw. leerer Zug

        // execute
        // Mehrere Sitze reservieren

        // assert
        // Gebuchte Sitzplätze sollen alle in einem Waggon (gleicher Buchstabe) sein
    }

}
