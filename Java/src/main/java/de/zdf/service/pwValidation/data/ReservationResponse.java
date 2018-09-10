package de.zdf.service.pwValidation.data;

import de.zdf.service.pwValidation.client.BookingReferenceServiceClient;
import lombok.Data;

import java.util.List;

@Data
public class ReservationResponse {
    String trainId;
    String booking_reference;
    List<String> seats;
}
