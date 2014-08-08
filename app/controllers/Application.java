package controllers;

import java.util.ArrayList;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static Result deleteEvent() {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();
		IsaaCloudAPI isaa = new IsaaCloudAPI();

		if(calendar != null && calendar.service != null)
		calendar.soiCalendar.getDeleteEvent(calendar.service);
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
		//

		if (calendar.soiCalendar.checkCalendarMeetings(calendar.service,
				userEmail)) {
			isaa.addPointsForAttendance(userEmail);
		}

		return ok("ok");
	}

	public static Result meetingBoard() {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();
		IsaaCloudAPI isaa = new IsaaCloudAPI();

		ArrayList<Users> usersList = calendar.soiCalendar
				.putUserEmails(calendar.service);

		String xx = "";

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
	
	public static Result a(){
		
		return ok("a");
	}
	
	public static Result b(){
		
		return ok("b");
	}
}
