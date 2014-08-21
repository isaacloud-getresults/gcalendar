package models;

import java.io.IOException;
import java.util.Collections;

import org.apache.commons.codec.binary.Base64;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;

public class GoogleCalendarAPI {

	public SOICalendar soiCalendar;

	private static final String APPLICATION_NAME = "SOI Calendar";

	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".store/Calendars");

	private static FileDataStoreFactory dataStoreFactory;

	private static HttpTransport httpTransport;

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	public com.google.api.services.calendar.Calendar service = null;

	private static Credential authorize(String id, String secret)
			throws Exception {

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, id, secret,
				Collections.singleton(CalendarScopes.CALENDAR))
				.setDataStoreFactory(dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize("user");
	}

	public GoogleCalendarAPI(String calendarBase64) {
		soiCalendar = new SOICalendar();
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			byte[] decoded = Base64.decodeBase64(calendarBase64);
			String decodedBase64 = new String(decoded, "UTF-8");
			if (decodedBase64.contains(":")) {
				String[] token = decodedBase64.split(":");
				Credential credential = authorize(token[0], token[1]);

				service = new com.google.api.services.calendar.Calendar.Builder(
						httpTransport, JSON_FACTORY, credential)
						.setApplicationName(APPLICATION_NAME).build();

			}
		} catch (IOException e) {
		} catch (Throwable t) {
		}
	}
}
