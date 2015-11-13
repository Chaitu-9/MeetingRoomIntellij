package main.java;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;


public class CalendarIDsQuickstartTest {



    @Test
    public void shouldKnowIfSantaAnaIsBusyInNext30MinutesOrNot() throws IOException {
        CalendarQuickstart calendar = new CalendarQuickstart();
        calendar.createEvent("test", CalendarIDs.SANTA_ANA_ID,10);

        boolean isRoomBusyActual = calendar.isRoomBusy(CalendarIDs.SANTA_ANA_ID);

        Assert.assertThat(isRoomBusyActual, is(false));

    }
}