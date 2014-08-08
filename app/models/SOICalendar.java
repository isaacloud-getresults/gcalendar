package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class SOICalendar {

	public DateTime lastSynchronizationTime;
	public String emailToGivePoints;
	public long timeToGivePoints;

	public List<Event> items;

	public SOICalendar() {
		// utworzyc czas synchronizacji gdzieś musi być zapisany. gdzie????
	}

	public boolean getDeleteEvent(
			com.google.api.services.calendar.Calendar service) {
		String pageToken = null;
		try {
			/*
			 * GET ALL EVENTS FROM LAST SYNCHRONIZATION AND CHECK IF IS
			 * CANCELLED, GET CREATOR EMAIL AND TIME BETWEEN START TIME AND
			 * CURRENT TIME
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
			e.printStackTrace();
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
			 * AND 0 AND IS IN MEETING ROOM, CHECK IF USER IS AN ATTENDEE AND
			 * RETURN TRUE IF ALL CONDITIONS ALL TRUE
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
									.equals(attendeeEmail)
									&& event.getAttendees().get(i)
											.getResponseStatus()
											.equals("accepted")) {
								return true;
							}
						}
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(SOICalendar.class.getName()).log(Level.SEVERE,
					null, ex);
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
			 * AND 0 AND IS IN MEETING ROOM, CHECK IF USER IS AN ATTENDEE AND
			 * RETURN TRUE IF ALL CONDITIONS ALL TRUE
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
						usersList.add(user);
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(SOICalendar.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return usersList;
	}
}
