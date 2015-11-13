package main.java;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.Calendar.Freebusy;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;


public class CalendarQuickstart {

    private static final String APPLICATION_NAME =  "Google CalendarIDs API Java";

    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;



    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);



    static {

        try {

            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

        } catch (Throwable t) {

            t.printStackTrace();

            System.exit(1);

        }

    }



    public static Credential authorize() throws IOException {

        // Load client secrets.

        URL url = new URL("file:///Users/chaitany/Documents/Meet/src/main/resources/client_secret.json");

        InputStream in = url.openStream();


        //InputStream in = CalendarQuickstart.class.getResourceAsStream("/Macintosh HD/Users/chaitany/Documents/Meet/src/main/resources/client_secret.json");


        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));


        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                                                                          .setDataStoreFactory(DATA_STORE_FACTORY)
                                                                          .setAccessType("offline")
                                                                          .build();

        Credential credential = null;
        try {
            credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

        return credential;
    }



    public static com.google.api.services.calendar.Calendar

    getCalendarService() throws IOException {

        Credential credential = authorize();

        return new com.google.api.services.calendar.Calendar.Builder(

                HTTP_TRANSPORT, JSON_FACTORY, credential)

                .setApplicationName(APPLICATION_NAME)

                .build();

    }



    public static void main(String[] args) throws IOException {


        Scanner input = new Scanner(System.in);
        System.out.println("------Enter the name of the meeting-------");
        String meetingName = input.next();
        System.out.println("------Enter the duration of the meeting in minutes------");
        int meetingDuration = input.nextInt();

        String roomId = CalendarIDs.SANTA_ANA_ID;

        CalendarQuickstart calendarQuickstart = new CalendarQuickstart();
        calendarQuickstart.createEvent(meetingName, roomId, meetingDuration);
    }

    public void createEvent(String meetingName, String roomId, long meetingDuration) throws IOException {
        com.google.api.services.calendar.Calendar service = getCalendarService();

        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 60000*meetingDuration);
        DateTime startDateTime = new DateTime(startDate);
        DateTime endDateTime = new DateTime(endDate);

        FreeBusyRequest req = new FreeBusyRequest();
        req.setTimeMin(startDateTime);
        req.setTimeMax(endDateTime);
        List<FreeBusyRequestItem> requestItems = new ArrayList<>();
        requestItems.add(new FreeBusyRequestItem().setId(roomId));
        req.setItems(requestItems);

        Freebusy.Query fbq = service.freebusy().query(req);

        FreeBusyResponse resp = fbq.execute();

        System.out.println("-------------");
        System.out.println(resp);
        System.out.println("-------------");


        Event event = new Event().setSummary(meetingName).setDescription("Instant meeting");



        EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Calcutta");
        event.setStart(start);

        EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Calcutta");
        event.setEnd(end);





        EventAttendee[] attendees = new EventAttendee[] {

                new EventAttendee().setEmail("prasanas@thoughtworks.com"),

                new EventAttendee().setEmail(roomId)

        };

        event.setAttendees(Arrays.asList(attendees));



        EventReminder[] reminderOverrides = new EventReminder[] {

                new EventReminder().setMethod("email").setMinutes(10),

                new EventReminder().setMethod("popup").setMinutes(10),
        };



        Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
                                                         .setOverrides(Arrays.asList(reminderOverrides));

        event.setReminders(reminders);



        String calendarId = "primary";

        event = service.events().insert(calendarId, event).execute();

        System.out.printf("Event created: %s\n", event.getHtmlLink());



    }

    public boolean isRoomBusy(String roomId) throws IOException {

        com.google.api.services.calendar.Calendar service= getCalendarService();

        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 60000*30);
        DateTime startDateTime = new DateTime(startDate);
        DateTime endDateTime = new DateTime(endDate);
        FreeBusyRequest req = new FreeBusyRequest();
        req.setTimeMin(startDateTime).setTimeZone("Asia/Calcutta");
        req.setTimeMax(endDateTime).setTimeZone("Asia/Calcutta");
        List<FreeBusyRequestItem> requestItems = new ArrayList<>();
        requestItems.add(new FreeBusyRequestItem().setId(roomId));
        req.setItems(requestItems);
        com.google.api.services.calendar.Calendar.Freebusy.Query fbq = service.freebusy().query(req);
        FreeBusyResponse resp = fbq.execute();

        String JSONString =resp.toString();
        try {
            JSONObject json = new JSONObject(JSONString);
            JSONObject getCalendars = json.getJSONObject("calendars");
            JSONObject getCalId = getCalendars.getJSONObject(roomId);
            String busyTime = getCalId.getString("busy");

            System.out.println(busyTime);

            if(busyTime.equals("[]")){
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }



}


