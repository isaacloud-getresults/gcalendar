package controllers;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	public static Result index() {

		IsaaCloudAPI isaa = new IsaaCloudAPI();
		GoogleCalendarAPI calendar = new GoogleCalendarAPI();

		return ok(index.render("Your new application is "));
	}

}
