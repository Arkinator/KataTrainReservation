package de.zdf.service.pwValidation.client.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainDataServiceUtil {
    public String mapSeatListToJsonString(List<String> seatList) {
        String result = "[";
        int counter = 0;
        for(String singleSeat : seatList) {
            result += "\"" + singleSeat;
            if(!(counter == seatList.size()-1)) {
                    result += "\",";
            } else {
                    result += "\"";
            }
            counter++;
        }

        return result + "]";
    }
}
