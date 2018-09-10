package de.zdf.service.pwValidation.client.util;

import de.zdf.service.pwValidation.data.SeatAvailabilityInformation;
import de.zdf.service.pwValidation.data.TrainResponse;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TrainDataServiceUtil {
    public String mapSeatListToJsonString(List<String> seatList) {
        String result = "[";
        int counter = 0;
        for (String singleSeat : seatList) {
            result += "\"" + singleSeat;
            if (!(counter == seatList.size() - 1)) {
                result += "\",";
            } else {
                result += "\"";
            }
            counter++;
        }

        return result + "]";
    }

    public static String selectCoachWithMaximumSeatsAvailable(TrainResponse trainData) {
        Map<String, Integer> seatCoachMap = populateCoachSeatMap(trainData);

        return selectMaximumEntryFromMap(seatCoachMap);
    }

    private static String selectMaximumEntryFromMap(Map<String, Integer> seatCoachMap) {
        String maxCoach = "";
        for (String currentCoach : seatCoachMap.keySet()) {
            if (maxCoach.isEmpty()
                    || seatCoachMap.get(maxCoach)<seatCoachMap.get(currentCoach)) {
                maxCoach = currentCoach;
            }
        }
        return maxCoach;
    }

    private static Map<String, Integer> populateCoachSeatMap(TrainResponse trainData) {
        Map<String, Integer> seatCoachMap = new HashMap<>();

        for (SeatAvailabilityInformation seat : trainData.getSeats().values()) {
            String coach = seat.getCoach();
            if (!seat.getBooking_reference().isEmpty()) {
                continue;
            }

            if (seatCoachMap.containsKey(coach)) {
                seatCoachMap.put(coach, seatCoachMap.get(coach) + 1);
            } else {
                seatCoachMap.put(coach, 1);
            }
        }
        return seatCoachMap;
    }
}
