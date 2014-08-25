package controllers;

import java.util.ArrayList;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	// AVAILABLE IN v2
	public static Result deleteEvent() {
		// IsaaCloudAPI isaa = new IsaaCloudAPI();
		// if (calendar.soiCalendar.getDeleteEvent(calendar.service))
		// isaa.addPointsForDelete(calendar.soiCalendar.emailToGivePoints,
		// calendar.soiCalendar.timeToGivePoints);

		return ok("ok");
	}

	public static Result meetingCheck() {
		String userEmail = ""
				+ request().body().asJson().get("body").get("data").asText();
		String isaaBase64 = ""
				+ request().body().asJson().get("body").get("isaaBase64")
						.asText();
		String calendarBase64 = ""
				+ request().body().asJson().get("body").get("calendarBase64")
						.asText();

		IsaaCloudAPI isaa = new IsaaCloudAPI(isaaBase64);
		GoogleCalendarAPI calendar = new GoogleCalendarAPI(calendarBase64);

		if (calendar.soiCalendar.checkCalendarMeetings(calendar.service,
				userEmail))
			isaa.addPointsForAttendance(userEmail);

		return ok("ok");
	}

	public static Result meetingBoard(String iB64, String cB64, String id) {
		String board = "";
		String isaaBase64 = iB64;
		String calendarBase64 = cB64;
		IsaaCloudAPI isaa = new IsaaCloudAPI(isaaBase64);
		GoogleCalendarAPI calendar = new GoogleCalendarAPI(calendarBase64);

		String x = id;
		String roomLabel = isaa.getRoomLabel(x);
		ArrayList<Users> usersList = calendar.soiCalendar.putUserEmails(
				calendar.service, roomLabel);
		if (usersList != null)
			for (int i = 0; i < usersList.size(); i++) {
				isaa.putUserInfo(usersList, i);
				usersList.get(i).calculateStatus();
				board += usersList.get(i).ID + ", ";
				board += usersList.get(i).userStatus + ", ";
				board += usersList.get(i).userInfo + ";\n";
			}
		return ok(board);
	}
}
