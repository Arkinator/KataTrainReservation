package de.zdf.service.pwValidation.data;

import lombok.Data;

import java.util.List;

@Data
public class ReservationRequest {
    String trainId;
    int numberOfDesiredSeats;
}
