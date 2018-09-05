package de.zdf.service.pwValidation.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.zdf.service.pwValidation.data.TrainResponse;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class TrainDataServiceClient {
    private String trainDataServiceUrl = "http://localhost:8081";

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

    public TrainResponse retrieveNewTrainData(String trainId) {
        try {
            return Unirest.get(trainDataServiceUrl + "/data_for_train/" + trainId)
                    .asObject(TrainResponse.class).getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("Train Data Service offline", e);
        }
    }

    public TrainResponse reserveSeats(String trainId, List<String> seats, String bookingReference) {
        try {
            return Unirest.put(trainDataServiceUrl + "/reserve")
                    .field("train_id", trainId)
                    .field("seats", seats)
                    .field("bookingReference", bookingReference)
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException("Train Data Service offline", e);
        }
    }
}
