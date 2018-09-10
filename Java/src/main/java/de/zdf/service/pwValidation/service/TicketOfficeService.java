package de.zdf.service.pwValidation.service;

import de.zdf.service.pwValidation.client.BookingReferenceServiceClient;
import de.zdf.service.pwValidation.client.TrainDataServiceClient;
import de.zdf.service.pwValidation.data.ReservationResponse;
import de.zdf.service.pwValidation.data.SeatAvailabilityInformation;
import de.zdf.service.pwValidation.data.TrainResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicketOfficeService {
    private String newBookingReference;

    @Autowired
    private TrainDataServiceClient trainDataServiceClient;
    @Autowired
    private BookingReferenceServiceClient bookingReferenceServiceClient;

    public ReservationResponse makeReservation(String trainId, int numberOfDesiredSeats) {
        //Anzahl der verfügbaren Sitze im Zug prüfen
        checkIfEnoughSeatsAreAvailable(trainId, numberOfDesiredSeats);

        // Aufruf des Booking Reference Service
        // Neue unbenutzte Booking_id abrufen
        newBookingReference = bookingReferenceServiceClient.createNewBookingReferenceId();

        // Buchung der gewünschten Anzahl Sitze im gewünschten Zug machen
        ReservationResponse response = new ReservationResponse();
        response.setBooking_reference(newBookingReference);
        response.setTrainId(trainId);
        response.setSeats(mapNumberOfSeatsToActualSeats(trainId, numberOfDesiredSeats));
        trainDataServiceClient.reserveSeats(trainId, mapNumberOfSeatsToActualSeats(trainId, numberOfDesiredSeats), newBookingReference);
        return response;
    }

    private void checkIfEnoughSeatsAreAvailable(String trainId, int numberOfDesiredSeats) {
        TrainResponse trainData = trainDataServiceClient.retrieveNewTrainData(trainId);
        int numberOfAvailableSeatsInTrain = 0;
        int maximumNumberOfAvailableSeatsInCoach = 0;
        String coach = selectCoachWithMaximumSeatsAvailable(trainData);

        for (SeatAvailabilityInformation seat : trainData.getSeats().values()) {

            if (seat.getBooking_reference().isEmpty()) {
                numberOfAvailableSeatsInTrain++;
            }

            if (seat.getBooking_reference().isEmpty() && seat.getCoach().equals(coach)) {
                maximumNumberOfAvailableSeatsInCoach++;
            }
        }

        if (numberOfAvailableSeatsInTrain < numberOfDesiredSeats) {
            throw new RuntimeException(numberOfDesiredSeats + " are too many seats for reservation, just " + numberOfAvailableSeatsInTrain + " seats in the whole train available");
        }
        if (maximumNumberOfAvailableSeatsInCoach < numberOfDesiredSeats) {
            throw new RuntimeException(numberOfDesiredSeats + " are too many seats for reservation, just " + maximumNumberOfAvailableSeatsInCoach + " in the biggest coach available");
        }
    }

    private String selectCoachWithMaximumSeatsAvailable(TrainResponse trainData) {
        String biggestCoach = "";
        int highestSeatNumber = 0;

        for (SeatAvailabilityInformation seat : trainData.getSeats().values()) {
            String currentCoach = seat.getCoach();
            int currentSeatNumber = Integer.parseInt(seat.getSeat_number());

            if (seat.getBooking_reference().isEmpty()) {
                if (currentSeatNumber > highestSeatNumber) {
                    highestSeatNumber = currentSeatNumber;
                    biggestCoach = currentCoach;
                }
            }

        }
        return biggestCoach;
    }

    private List<String> mapNumberOfSeatsToActualSeats(final String trainId, final int numberOfSeats) {
        TrainResponse trainResponse = trainDataServiceClient.retrieveNewTrainData(trainId);
        List<String> seatsForReservation = new ArrayList<>();
        int seatsLeftToBeReserved = numberOfSeats;
        //String coach = selectCoachWithMaximumSeatsAvailable(trainResponse);

        for (SeatAvailabilityInformation seat : trainResponse.getSeats().values()) {

            if (seat.getBooking_reference().isEmpty()) {
                seatsForReservation.add(seat.getSeat_number() + seat.getCoach());
                seatsLeftToBeReserved--;
            }

            if (seatsLeftToBeReserved == 0) {
                break;
            }
        }

        return seatsForReservation;
    }

}
