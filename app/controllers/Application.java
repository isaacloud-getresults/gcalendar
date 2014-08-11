package controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class Application extends Controller {

	public static Result deleteEvent() {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();
		IsaaCloudAPI isaa = new IsaaCloudAPI();

		if (calendar.soiCalendar.getDeleteEvent(calendar.service))
			isaa.addPointsForDelete(calendar.soiCalendar.emailToGivePoints,
					calendar.soiCalendar.timeToGivePoints);

		return ok("ok");
	}

	public static Result meetingCheck() {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();
		IsaaCloudAPI isaa = new IsaaCloudAPI();

		// dostaje skądś maila (ISAACLOUD) że pojawił się w meeting room.
		String userEmail = "mnowicki@sosoftware.pl";
		//

		if (calendar.soiCalendar.checkCalendarMeetings(calendar.service,
				userEmail))
			isaa.addPointsForAttendance(userEmail);

		return ok("ok");
	}

	public static Result meetingBoard() {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();
		IsaaCloudAPI isaa = new IsaaCloudAPI();

		String xx = "";

		ArrayList<Users> usersList = calendar.soiCalendar
				.putUserEmails(calendar.service);
		if (usersList != null)
			for (int i = 0; i < usersList.size(); i++) {
				isaa.putUserInfo(usersList, i);
				usersList.get(i).calculateStatus();
				xx += usersList.get(i).userFirstName + " ";
				xx += usersList.get(i).userLastName + " is in ";
				xx += usersList.get(i).userPlace + "	- ";

				xx += usersList.get(i).userStatus + "\n";
			}

		return ok("" + xx);
	}

	public static Result a() {
		Events events = null;
		String x = "ss ";

		try {

			GoogleCredential credential = new GoogleCredential.Builder()
					.setTransport(GoogleNetHttpTransport.newTrustedTransport())
					.setJsonFactory(new GsonFactory())
					.setServiceAccountId(
							"338968387608-tis8g8m1pb5lrvbe7578ga98ustigrps@developer.gserviceaccount.com")
					.setServiceAccountScopes(
							Arrays.asList("https://www.googleapis.com/auth/calendar"))
					.setClientSecrets(
							"338968387608-tis8g8m1pb5lrvbe7578ga98ustigrps.apps.googleusercontent.com",
							"BdPouz_18JA1qIYsFp2PZ6tB").build();
			x += "asdsadsadasd";
			com.google.api.services.calendar.Calendar service = new Calendar.Builder(
					GoogleNetHttpTransport.newTrustedTransport(),
					new GsonFactory(), credential).build();

			String pageToken = null;
			events = service.events().list("primary").setPageToken(pageToken)
					.execute();

			List<Event> myEvents = events.getItems();

			for (Event event : myEvents) {

				long time = event.getStart().getDateTime().getValue()
						- new Date().getTime();
				x += time;
			}

		} catch (IOException e) {
		} catch (Throwable t) {
		}

		return ok(x);
	}

	private static final String APPLICATION_NAME = "SOI Calendar";

	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".store/Calendars");

	private static FileDataStoreFactory dataStoreFactory;

	private static HttpTransport httpTransport;

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	public static Result b() {
		Events events = null;
		String x = "0 ";

		try {

			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			x += "1 "
					+ GoogleCalendarAPI.class.getProtectionDomain()
							.getCodeSource().getLocation().getPath();

			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
					JSON_FACTORY,
					new InputStreamReader(GoogleCalendarAPI.class
							.getResourceAsStream("/client_secrets.json")));
			x += "2 ";
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, JSON_FACTORY, clientSecrets,
					Collections.singleton(CalendarScopes.CALENDAR))
					.setDataStoreFactory(dataStoreFactory).build();
			x += "3 ";
			Credential credential = new AuthorizationCodeInstalledApp(flow,
					new LocalServerReceiver()).authorize("user");
			x += "4 ";
			com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
					httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
			x += "5 ";
			String pageToken = null;
			events = service.events().list("primary").setPageToken(pageToken)
					.execute();
			x += "6 ";
			List<Event> myEvents = events.getItems();
			x += "7 ";
			for (Event event : myEvents) {
				x += "8 ";
				long time = event.getStart().getDateTime().getValue()
						- new Date().getTime();
				x += time;
			}

		} catch (IOException e) {
		} catch (Throwable t) {
		}

		return ok(x);
	}
}
