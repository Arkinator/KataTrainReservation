package de.zdf.service.pwValidation.service;

import de.zdf.service.pwValidation.client.TrainDataServiceClient;
import de.zdf.service.pwValidation.data.ReservationResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerServiceControllerTest {

    @Autowired
    public TestRestTemplate restTemplate;

    @MockBean
    public CustomerService customerService;

    @Before
    public void setUp() {
        ReservationResponse toBeReturned = new ReservationResponse();

        toBeReturned.setTrainId("testTrainId");
        toBeReturned.setBooking_reference("testBookingReference");
        toBeReturned.setSeats(Arrays.asList("1A", "2A", "3A", "4A"));

        doReturn(toBeReturned)
                .when(customerService)
                .makeReservation("local_1000",4);
    }

    @Test
    public void customerServiceControllerWorking() {
        ResponseEntity<String> stringResponseEntity = restTemplate
                .getForEntity("/reservationRequest?trainId=local_1000&numberOfDesiredSeats=4", String.class);

        assertThat(stringResponseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(stringResponseEntity.toString().contains("testTrainId"));
        assertThat(stringResponseEntity.toString().contains("testBookingReference"));
        assertThat(stringResponseEntity.toString().contains("4A"));
    }

    @Test
    public void noSeatNumberInRequest_ShouldGive4xx() {
        ResponseEntity<String> stringResponseEntity = restTemplate
                .getForEntity("/reservationRequest?trainId=local_1000", String.class);

        assertThat(stringResponseEntity.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    public void noTrainIdInRequest_ShouldGive4xx() {
        ResponseEntity<String> stringResponseEntity = restTemplate
                .getForEntity("/reservationRequest?numberOfDesiredSeats=4", String.class);

        assertThat(stringResponseEntity.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    public void customerRequestContainsTrainThatExists() {
        //customerService = new CustomerService();

    }
}
