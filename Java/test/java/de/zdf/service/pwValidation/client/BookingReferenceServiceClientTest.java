package de.zdf.service.pwValidation.client;

import de.zdf.utils.test.wiremock.WiremockTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BookingReferenceServiceClientTest {
    @Rule
    public WiremockTestRule wmockRule = WiremockTestRule.builder()
            .targetUrl("http://localhost:8082")
            .recordMode(false)
            .build();

    private BookingReferenceServiceClient bookingReferenceServiceClient;

    @Before
    public void setup() {
        bookingReferenceServiceClient = new BookingReferenceServiceClient();
        ReflectionTestUtils.setField(bookingReferenceServiceClient, "bookingReferenceServiceUrl", wmockRule.getWiremockUrl());
    }

    @Test
    public void BookingReferenceServiceWorking() {
        //gegeben dass: prepare (in diesem Fall nicht notwendig)

        String newBookingReferenceId = bookingReferenceServiceClient.createNewBookingReferenceId();

        assertThat(newBookingReferenceId).containsPattern("^(?:[0-9a-fA-F]{7})$");
    }
}