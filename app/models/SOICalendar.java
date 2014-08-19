package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class SOICalendar {

	public DateTime lastSynchronizationTime;
	public String emailToGivePoints;
	public long timeToGivePoints;

	public List<Event> items;

	public SOICalendar() {
		// SYNC TIME
		this.lastSynchronizationTime = new DateTime(new Date());
	}

	public boolean getDeleteEvent(
			com.google.api.services.calendar.Calendar service) {
		String pageToken = null;
		try {
			/*
			 * GET ALL EVENTS FROM LAST SYNCHRONIZATION AND CHECK IF THERE IS
			 * CANCELLED ONE, GET CREATOR EMAIL AND TIME BETWEEN MEETING START
			 * AND CURRENT TIME, UPDATE LAST SYNCHRONIZATION TIME
			 */
			DateTime newSynchronizationTime = new DateTime(new Date());
			Events events = service.events().list("primary")
					.setPageToken(pageToken)
					.setUpdatedMin(lastSynchronizationTime)
					.setShowDeleted(true).execute();
			List<Event> myEvents = events.getItems();
			for (Event event : myEvents) {
				if (event.getStatus().equals("cancelled")) {
					this.emailToGivePoints = event.getCreator().getEmail();
					this.timeToGivePoints = event.getStart().getDateTime()
							.getValue()
							- new Date().getTime();
					this.lastSynchronizationTime = newSynchronizationTime;
					return true;
				}
			}

		} catch (IOException e) {
		}
		return false;
	}

	public boolean checkCalendarMeetings(
			com.google.api.services.calendar.Calendar service,
			String attendeeEmail) {
		String pageToken = null;
		try {
			/*
			 * GET ALL EVENTS, CHECK IF THERE IS SOME THAT TIME IS BEETWEN 10min
			 * AND 0, IS IN MEETING ROOM, CHECK IF USER IS AN ATTENDEE, RETURN
			 * TRUE IF SO
			 */
			Events events = service.events().list("primary")
					.setPageToken(pageToken).execute();
			List<Event> myEvents = events.getItems();
			for (Event event : myEvents) {
				if (event.getLocation() != null && event.getAttendees() != null) {
					long time = event.getStart().getDateTime().getValue()
							- new Date().getTime();
					// 10 minutes = 600 000 milliseconds
					if (time < 600000 && time > 0
							&& event.getLocation().equals("Meeting room")
							&& event.getStatus().equals("confirmed")) {
						for (int i = 0; i < event.getAttendees().size(); i++) {
							if (event.getAttendees().get(i).getEmail()
									.equals(attendeeEmail)) {
								return true;
							}
						}
					}
				}
			}
		} catch (IOException ex) {
		}
		return false;
	}

	public ArrayList<Users> putUserEmails(
			com.google.api.services.calendar.Calendar service) {
		ArrayList<Users> usersList = new ArrayList<Users>();
		String pageToken = null;
		try {
			/*
			 * GET ALL EVENTS, CHECK IF THERE IS SOME THAT TIME IS BEETWEN 10min
			 * BEFORE START AND 0 BEFORE END, IS IN MEETIN, IF TRUE GET USER
			 * EMAIL, RESPONSE STATUS AND MEETING START TIME
			 */
			Events events = service.events().list("primary")
					.setPageToken(pageToken).execute();
			List<Event> myEvents = events.getItems();
			for (Event event : myEvents) {
				long timeStart = event.getStart().getDateTime().getValue()
						- new Date().getTime();
				long timeEnd = event.getEnd().getDateTime().getValue()
						- new Date().getTime();
				// 10 minutes = 600 000 milliseconds
				if (timeStart < 600000 && timeEnd > 0
						&& event.getLocation().equals("Meeting room")) {
					for (int i = 0; i < event.getAttendees().size(); i++) {
						Users user = new Users();
						user.userEmail = event.getAttendees().get(i).getEmail();
						user.time = event.getStart().getDateTime().getValue();
						user.userInfo = event.getAttendees().get(i)
								.getResponseStatus();
						usersList.add(user);
					}
				}
			}
		} catch (IOException ex) {
		}
		return usersList;
	}
}
