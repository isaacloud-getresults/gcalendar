package models;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class SOICalendar {

	public List<Event> items;

	public SOICalendar(com.google.api.services.calendar.Calendar client) {
		try {
			// GET ALL EVENTS AFTER APPLICATION STARTS AND KEEP THEM IN OBJECT
			String pageToken = null;
			Events events = client.events().list("primary")
					.setPageToken(pageToken).execute();
			items = events.getItems();
		} catch (IOException ex) {
			Logger.getLogger(SOICalendar.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void checkCalendarMeetings(
			com.google.api.services.calendar.Calendar client) {
		try {
			// GET ALL EVENTS, CHECK IF SOME WAS DELETED, REPLACE THE OLD ONES
			String pageToken = null;
			Events events = client.events().list("primary")
					.setPageToken(pageToken).execute();
			List<Event> tempItems = events.getItems();

			for (Event event : items) {
				if (event.getLocation().equals("Meeting room")) {
					int tempCounter = 0;
					for (Event tempEvent : tempItems) {
						if (event.getSummary().equals(tempEvent.getSummary())) {
							tempCounter++;
						}
					}
					if (tempCounter == 0) {
						// OPERATION AFTER FOUND DELETE ONE
						long time = event.getStart().getDateTime().getValue()
								- new Date().getTime();
						String email = event.getCreator().getEmail();
						if (time > 3600000) {

							// give points ++100
						} else if (time > 1800000) {
							// give points ++50
						}

						// System.out.println("Zanalazlem usunięte wydarzenie wydarzenie "
						// + email + " " + time);
					}
				}
			}
			items = tempItems;

		} catch (IOException ex) {
			Logger.getLogger(SOICalendar.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void addPointsForPresence() {
		for (Event event : items) {
			long time = event.getStart().getDateTime().getValue()
					- new Date().getTime();
			if (time < 300000) {
				for (int i = 0; i < event.getAttendees().size(); i++) {
					String email = event.getAttendees().get(i).getEmail();
					String place = "Meeting room"; // funkcja
					// comunication with isaa - where is my user. place =
					// function(daje event.addtendecnce)
					if (place == "Meeting room" && time > 120000) {
						// give 2pkt
					} else if (place == "Meeting room" && time > 0) {
						// give 1pkt
					}
				}
			}
		}
	}

}
