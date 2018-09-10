import de.zdf.service.pwValidation.client.TrainDataServiceClient;
import de.zdf.service.pwValidation.client.util.TrainDataServiceUtil;
import de.zdf.service.pwValidation.data.SeatAvailabilityInformation;
import de.zdf.service.pwValidation.data.TrainResponse;
import org.json.JSONString;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.setRemoveAssertJRelatedElementsFromStackTrace;

public class TrainDataServiceUtilTest {
    TrainDataServiceUtil trainDataServiceUtil = new TrainDataServiceUtil();

    @Test
    public void emptyList_shouldGiveEmptyArray() {
        assertThat(trainDataServiceUtil.mapSeatListToJsonString(Collections.emptyList()))
                .isEqualTo("[]");
    }

    @Test
    public void oneItemInList_ShouldGiveSingleSeatResult() {
        assertThat(trainDataServiceUtil.mapSeatListToJsonString(Arrays.asList("1A")))
                .isEqualTo("[\"1A\"]");
    }

    @Test
    public void twoItemsInList_ShouldGiveDoubleSeatResult() {
        assertThat(trainDataServiceUtil.mapSeatListToJsonString(Arrays.asList("foo", "bar")))
                .isEqualTo("[\"foo\",\"bar\"]");
    }

    @Test
    public void oneCoachOnlyTrain_shouldReturnSaidCoach() {
        TrainResponse trainTest = new TrainResponse();
        trainTest.setSeats(new HashMap<>());
        trainTest.getSeats().put("1A", buildSeatInfo("1", "A", false));
        trainTest.getSeats().put("2A", buildSeatInfo("2", "A", false));
        trainTest.getSeats().put("3A", buildSeatInfo("3", "A", false));

        assertThat(TrainDataServiceUtil.selectCoachWithMaximumSeatsAvailable(trainTest))
                .isEqualTo("A");
    }

    @Test
    public void twoCoachSecondOneLonger_shouldReturnBiggerOne() {
        TrainResponse trainTest = new TrainResponse();
        trainTest.setSeats(new HashMap<>());
        trainTest.getSeats().put("1A", buildSeatInfo("1", "A", false));
        trainTest.getSeats().put("1B", buildSeatInfo("1", "B", false));
        trainTest.getSeats().put("2B", buildSeatInfo("2", "B", false));

        assertThat(TrainDataServiceUtil.selectCoachWithMaximumSeatsAvailable(trainTest))
                .isEqualTo("B");
    }

    @Test
    public void threeCoachSecondOneLongest_shouldReturnSecondOne() {
        TrainResponse trainTest = new TrainResponse();
        trainTest.setSeats(new HashMap<>());
        trainTest.getSeats().put("1A", buildSeatInfo("1", "A", false));
        trainTest.getSeats().put("2A", buildSeatInfo("2", "A", false));
        trainTest.getSeats().put("1B", buildSeatInfo("1", "B", false));
        trainTest.getSeats().put("2B", buildSeatInfo("2", "B", false));
        trainTest.getSeats().put("3B", buildSeatInfo("3", "B", false));
        trainTest.getSeats().put("1C", buildSeatInfo("1", "C", false));
        trainTest.getSeats().put("2C", buildSeatInfo("2", "C", false));

        assertThat(TrainDataServiceUtil.selectCoachWithMaximumSeatsAvailable(trainTest))
                .isEqualTo("B");
    }

    @Test
    public void seatsTakenInLongestCar_shouldSelectShorterCar() {
        TrainResponse trainTest = new TrainResponse();
        trainTest.setSeats(new HashMap<>());
        trainTest.getSeats().put("1A", buildSeatInfo("1", "A", false));
        trainTest.getSeats().put("1B", buildSeatInfo("1", "B", true));
        trainTest.getSeats().put("2B", buildSeatInfo("2", "B", true));

        assertThat(TrainDataServiceUtil.selectCoachWithMaximumSeatsAvailable(trainTest))
                .isEqualTo("A");
    }

    private SeatAvailabilityInformation buildSeatInfo(String seat, String coach, boolean besetzt) {
        SeatAvailabilityInformation result = new SeatAvailabilityInformation();
        result.setCoach(coach);
        result.setSeat_number(seat);
        if (besetzt) {
            result.setBooking_reference("sdfghjkoiuzgv");
        } else {
            result.setBooking_reference("");
        }
        return result;
    }
}