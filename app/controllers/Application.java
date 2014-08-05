package controllers;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Your new application is ready"));
	}

	public static Result deleteEvent() {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();
		IsaaCloudAPI isaa = new IsaaCloudAPI();

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
}
