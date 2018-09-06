import de.zdf.service.pwValidation.client.util.TrainDataServiceUtil;
import org.json.JSONString;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(trainDataServiceUtil.mapSeatListToJsonString(Arrays.asList("foo","bar")))
                .isEqualTo("[\"foo\",\"bar\"]");
    }
}