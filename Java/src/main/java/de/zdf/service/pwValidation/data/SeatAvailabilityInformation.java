package de.zdf.service.pwValidation.data;

import lombok.Data;

@Data
public class SeatAvailabilityInformation {
    private String coach;
    private String seat_number;
    private String booking_reference;

    public int getSeatNumber() {
        return Integer.parseInt(seat_number);
    }
}
