package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
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
							"338968387608-s82p0kk2e2utvki5lipbo19cg3c46eqv@developer.gserviceaccount.com")
					.setServiceAccountScopes(
							Arrays.asList("https://www.googleapis.com/auth/calendar"))
					.setClientSecrets(
							"338968387608-s82p0kk2e2utvki5lipbo19cg3c46eqv.apps.googleusercontent.com",
							"iDGxLmcmJZ7mmRMCqEHWfx6d").build();

			com.google.api.services.calendar.Calendar service = new Calendar.Builder(
					GoogleNetHttpTransport.newTrustedTransport(),
					new GsonFactory(), credential).build();

			String pageToken = null;
			events = service.events().list("primary").setPageToken(pageToken)
					.execute();

			List<Event> myEvents = events.getItems();
			for (Event event : myEvents) {
				if (event.getLocation() != null && event.getAttendees() != null) {
					long time = event.getStart().getDateTime().getValue()
							- new Date().getTime();
					x += time;
				}
			}

		} catch (IOException e) {
		} catch (Throwable t) {
		}

		return ok(x);
	}
}
