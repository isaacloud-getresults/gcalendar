package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.api.services.calendar.model.Channel;
import com.isaacloud.sdk.IsaacloudConnectionException;

public class Application extends Controller {

	public static Result createCalendarNotification(String iB64, String cB64,
			String name) {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI(cB64);
		Map<String, String> params = new HashMap<String, String>();
		params.put("server", "getresults.isaacloud.com");
		Channel request = new Channel()
				.setId("1234567890987654321")
				.setType("web_hook")
				.setAddress(
						String.format("http://getresults.isaacloud.com:8080/deleteNotification"))
				.setParams(params).set("isaaBase64", iB64)
				.set("calendarBase64", cB64);
		try {
			calendar.service.events().watch(name, request).execute();
		} catch (IOException e) {
		}

		return ok("ok");
	}

	// AVAILABLE IN v2
	public static Result deleteEvent() {
		System.out.println("test");
		String isaaBase64 = ""
				+ request().body().asJson().get("isaaBase64").asText();
		String calendarBase64 = ""
				+ request().body().asJson().get("calendarBase64").asText();

		IsaaCloudAPI isaa = new IsaaCloudAPI(isaaBase64);
		GoogleCalendarAPI calendar = new GoogleCalendarAPI(calendarBase64);

		if (calendar.soiCalendar.getDeleteEvent(calendar.service))
			isaa.addPointsForDelete(calendar.soiCalendar.emailToGivePoints,
					calendar.soiCalendar.timeToGivePoints);

		return ok("ok");
	}

	public static Result meetingCheck() {
		String userEmail = "" + request().body().asJson().get("data").asText();
		String isaaBase64 = ""
				+ request().body().asJson().get("isaaBase64").asText();
		String calendarBase64 = ""
				+ request().body().asJson().get("calendarBase64").asText();
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

		String roomLabel = isaa.getRoomLabel(id);
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

	public static Result deleteRoom(String iB64, String name) {

		IsaaCloudAPI isaa = new IsaaCloudAPI(iB64);
		try {
			isaa.deleteRoom(name);
		} catch (IOException e) {
		} catch (IsaacloudConnectionException e) {
		}
		return ok("ok");
	}
}
