package controllers;

import java.util.ArrayList;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static GoogleCalendarAPI calendar = new GoogleCalendarAPI();
	public static IsaaCloudAPI isaa = new IsaaCloudAPI();

	public static Result deleteEvent() {
		if (calendar.soiCalendar.getDeleteEvent(calendar.service))
			isaa.addPointsForDelete(calendar.soiCalendar.emailToGivePoints,
					calendar.soiCalendar.timeToGivePoints);

		return ok("ok");
	}

	public static Result meetingCheck() {
		String userEmail = ""
				+ request().body().asJson().get("body").get("data").asText();

		if (calendar.soiCalendar.checkCalendarMeetings(calendar.service,
				userEmail))
			isaa.addPointsForAttendance(userEmail);

		return ok("ok");
	}

	public static Result meetingBoard() {
		String board = "";

		ArrayList<Users> usersList = calendar.soiCalendar
				.putUserEmails(calendar.service);
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
