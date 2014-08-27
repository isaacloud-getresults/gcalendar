package controllers;

import java.io.IOException;
import java.util.ArrayList;

import models.GoogleCalendarAPI;
import models.IsaaCloudAPI;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.api.services.calendar.model.Channel;
import com.isaacloud.sdk.IsaacloudConnectionException;

public class Application extends Controller {

	// AVAILABLE IN v2
	public static Result createCalendarNotification(String iB64, String cB64,
			String name) {
		GoogleCalendarAPI calendar = new GoogleCalendarAPI(cB64);

		Channel request = new Channel()
				.setId("13cb26cr82yrcgb3uircbg23ir62b3rx7i612r7i162fbrx76")
				.setType("web_hook")
				.setAddress(
						"http://getresults.isaacloud.com:8080/deleteNotification")
				.set("isaaBase64", iB64).set("calendarBase64", cB64);
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

	// //////////////////////////////////////////////////////////////////////////////////////////

	public static Result meetingCheck() {
		String roomLabel = "Meeting Room";

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

		ArrayList<Users> usersList = calendar.soiCalendar.putUserEmails(
				calendar.service, roomLabel);

		int counter = 0, a = 0, b = 0;

		for (int i = 0; i < usersList.size(); i++) {
			isaa.putUserInfo(usersList, i);
			if (usersList.get(i).userPlace.equals(roomLabel)) {
				counter++;
				if (counter == 1) {
					a = i;
				} else if (counter == 2) {
					b = i;
				} else if (counter == 3) {
					isaa.addAchievementForPunktualMeeting(usersList.get(a).userEmail);
					isaa.addAchievementForPunktualMeeting(usersList.get(b).userEmail);
					isaa.addAchievementForPunktualMeeting(usersList.get(i).userEmail);
					System.out.println(i);
				} else if (counter > 3) {
					isaa.addAchievementForPunktualMeeting(usersList.get(i).userEmail);
					System.out.println(i);
				}
			}
		}

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
