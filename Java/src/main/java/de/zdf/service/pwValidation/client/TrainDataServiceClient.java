package de.zdf.service.pwValidation.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.zdf.service.pwValidation.client.util.TrainDataServiceUtil;
import de.zdf.service.pwValidation.data.SeatAvailabilityInformation;
import de.zdf.service.pwValidation.data.TrainDataServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class TrainDataServiceClient {
    private String trainDataServiceUrl = "http://localhost:8081";

    @Autowired
    private TrainDataServiceUtil trainDataServiceUtil;

    @PostConstruct
    public void registerJacksonWithUnirest() {
        Unirest.setObjectMapper(new ObjectMapper() {
            com.fasterxml.jackson.databind.ObjectMapper mapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return mapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public TrainDataServiceResponse retrieveNewTrainData(String trainId) {
        try {
            TrainDataServiceResponse body = Unirest.get(trainDataServiceUrl + "/data_for_train/" + trainId)
                    .asObject(TrainDataServiceResponse.class).getBody();
            if(body == null) {
                throw new RuntimeException("No train found with given trainId");
            }
            return body;
        } catch (UnirestException e) {
            throw new RuntimeException("Train Data Service offline", e);
        }
    }

    public boolean reserveSeats(String trainId, List<String> seats, String bookingReference) {
        try {
            String response = Unirest.post(trainDataServiceUrl + "/reserve")
                    .field("train_id", trainId)
                    .field("seats", trainDataServiceUtil.mapSeatListToJsonString(seats))
                    .field("booking_reference", bookingReference)
                    .asString().getBody();

            return !response.startsWith("already booked with reference:");
        } catch (UnirestException e) {
            throw new RuntimeException("Reserve Service offline", e);
        }
    }

    public boolean checkIfAllSeatsAreAvailable(String trainId) {
        TrainDataServiceResponse requestForSeatAvailability = retrieveNewTrainData(trainId);

        int numberOfUnbookedSeats = 0;
        for (SeatAvailabilityInformation seat : requestForSeatAvailability.getSeats().values()) {
            if (seat.getBooking_reference().isEmpty()) {
                numberOfUnbookedSeats++;
            }
        }

        return requestForSeatAvailability.getSeats().size() == numberOfUnbookedSeats;
    }

    public boolean checkIfEnoughSeatsForBookingRequest(String trainId) {
        TrainDataServiceResponse requestForSeatAvailability = retrieveNewTrainData(trainId);

        int numberOfUnbookedSeats = 0;
        for (SeatAvailabilityInformation seat : requestForSeatAvailability.getSeats().values()) {
            if (seat.getBooking_reference().isEmpty()) {
                numberOfUnbookedSeats++;
            }
        }

        return requestForSeatAvailability.getSeats().size() > numberOfUnbookedSeats;
    }

    public boolean resetReservations(String trainId) {
        try {
            Unirest.get(trainDataServiceUrl + "/reset/"+ trainId)
                    .asString();
        } catch (UnirestException e) {
            System.out.println(e);
        }
        return true;
    }
}
