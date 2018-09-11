package de.zdf.service.pwValidation.service;

import de.zdf.service.pwValidation.client.TrainDataServiceClient;
import de.zdf.service.pwValidation.data.ReservationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void customerServiceControllerWorking() {
        doReturn(new ReservationResponse())
                .when(customerService)
                .makeReservation("local_1000",4);

        String bla = restTemplate.postForEntity("/reservationRequest", "?trainId=local_1000&numberOfDesiredSeats=2", String.class)
                .toString();

        assertThat(bla).isNotEmpty();

    }

    @Test
    public void customerRequestContainAtLeastOneSeat() {
        //customerService = new CustomerService();

        //assertThat(customerService).hasFieldOrProperty();
    }

    @Test
    public void customerRequestContainsTrainThatExists() {
        //customerService = new CustomerService();

    }
}
