package controllers;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	public static Result index() {

		// INITIALIZATION
		IsaaCloudAPI isaa = new IsaaCloudAPI();
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();

		// FUNCTION TEST

		calendar.soiCalendar.getDeleteEvent(calendar.service);

		boolean x = calendar.soiCalendar.checkCalendarMeetings(
				calendar.service, isaa.userEmail);

		return ok(index.render("Your new application is "));
	}
}
