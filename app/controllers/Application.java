package controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;

import org.apache.commons.codec.binary.Base64;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	// AVAILABLE IN v2
	public static Result deleteEvent() {
		// IsaaCloudAPI isaa = new IsaaCloudAPI();
		// if (calendar.soiCalendar.getDeleteEvent(calendar.service))
		// isaa.addPointsForDelete(calendar.soiCalendar.emailToGivePoints,
		// calendar.soiCalendar.timeToGivePoints);

		String base64 = "Mjc4OmI0MzU5YWEzZTA3YjgwNjg3OTE4ODQyYTMyOTIxNmJk";
		String decodedBase64;
		String id = "";
		String secret = "";
		byte[] decoded = Base64.decodeBase64(base64);
		try {
			decodedBase64 = new String(decoded, "UTF-8");
			if (decodedBase64.contains(":")) {
				String[] parts = decodedBase64.split(":");
				id = parts[0];
				secret = parts[1];
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return ok("id: " + id + " secret: " + secret);
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

	public static Result meetingBoard() {
		String board = "";

		String isaaBase64 = "MTc5OmNiN2RlMDFjM2YxZDZkM2Q1ZWQyYWNiMTU4MGE5OTc=";
		String calendarBase64 = "OTg4NzIyODY5NDU0LWRibWd2cHMwYWpoam9kamNjZ2dxcnM1bWFpdjd0NDg5LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tOiBjSmhqTmVPT2dDbGRkWkNoTzNqYTR3SXY=";
		IsaaCloudAPI isaa = new IsaaCloudAPI(isaaBase64);
		GoogleCalendarAPI calendar = new GoogleCalendarAPI(calendarBase64);

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
