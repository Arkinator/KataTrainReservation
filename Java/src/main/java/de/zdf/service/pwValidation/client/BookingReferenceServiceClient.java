package de.zdf.service.pwValidation.client;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

@Service
public class BookingReferenceServiceClient {
    private String bookingReferenceServiceUrl = "http://localhost:8082";

    public String createNewBookingReferenceId() {
        try {
            return Unirest.get(bookingReferenceServiceUrl + "/booking_reference")
                    .asString().getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("Booking Service offline");
        }
    }
}
