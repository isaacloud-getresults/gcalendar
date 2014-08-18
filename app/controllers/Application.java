package controllers;

import java.util.ArrayList;
import java.util.Map;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

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

		Map<String, String[]> formData = request().body().asFormUrlEncoded();
		// dostaje skądś maila (ISAACLOUD) że pojawił się w meeting room.
		// String userEmail = formData.get("email")[0];
		//

		// if (calendar.soiCalendar.checkCalendarMeetings(calendar.service,
		// userEmail))
		// isaa.addPointsForAttendance(userEmail);

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
				xx += usersList.get(i).ID + ", ";
				xx += usersList.get(i).userStatus + ", ";
				xx += usersList.get(i).userInfo + ";\n";
			}

		return ok("" + xx);
	}

}
