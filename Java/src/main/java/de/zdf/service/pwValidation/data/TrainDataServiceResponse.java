package de.zdf.service.pwValidation.data;

import lombok.Data;

import java.util.Map;

@Data
public class TrainDataServiceResponse {
    private Map<String, SeatAvailabilityInformation> seats;
}
