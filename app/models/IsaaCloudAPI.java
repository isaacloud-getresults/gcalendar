package models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.codec.binary.Base64;

import com.isaacloud.sdk.Isaacloud;
import com.isaacloud.sdk.IsaacloudConnectionException;

public class IsaaCloudAPI {

	private Isaacloud isaac;

	public IsaaCloudAPI(String isaaBase64) {
		try {
			byte[] decoded = Base64.decodeBase64(isaaBase64);
			String decodedBase64 = new String(decoded, "UTF-8");
			if (decodedBase64.contains(":")) {
				String[] token = decodedBase64.split(":");

			}
		} catch (UnsupportedEncodingException e) {
		}

		Map<String, String> config = new HashMap<>();
		config.put("clientId", "179");
		config.put("secret", "cb7de01c3f1d6d3d5ed2acb1580a997");
		isaac = new Isaacloud(config);
	}

	public void addPointsForDelete(String userEmail, long timeToGivePoints) {
		if (timeToGivePoints > 0) {
			JSONObject body = new JSONObject();
			if (timeToGivePoints > 3600000)
				body.put("addPoints", "10");
			else if (timeToGivePoints > 1800000)
				body.put("addPoints", "5");
			else
				body.put("addPoints", "1");

			try {
				SortedMap<String, String> query = new TreeMap<>();
				query.put("email", userEmail);
				JSONArray users = (JSONArray) isaac.path("/cache/users")
						.withQuery(query).withFields("id").get().getJson();

				int id = Integer.parseInt(((JSONObject) users.get(0)).get("id")
						.toString());
				isaac.event(id, "USER", "PRIORITY_NORMAL", 1, "NORMAL", body);
			} catch (IOException e) {
			} catch (IsaacloudConnectionException e) {
			}
		}
	}

	public void addPointsForAttendance(String userEmail) {
		JSONObject body = new JSONObject();
		body.put("addPoints", "2");

		try {
			SortedMap<String, String> query = new TreeMap<>();
			query.put("email", userEmail);
			JSONArray users = (JSONArray) isaac.path("/cache/users")
					.withQuery(query).withFields("id").get().getJson();
			if (!users.isEmpty()) {
				int id = Integer.parseInt(((JSONObject) users.get(0)).get("id")
						.toString());
				isaac.event(id, "USER", "PRIORITY_NORMAL", 1, "NORMAL", body);
			}
		} catch (IOException e) {
		} catch (IsaacloudConnectionException e) {
		}
	}

	public void putUserInfo(ArrayList<Users> usersList, int idAL) {
		try {
			SortedMap<String, String> query = new TreeMap<>();
			query.put("email", usersList.get(idAL).userEmail);
			JSONArray users = (JSONArray) isaac.path("/cache/users")
					.withQuery(query).get().getJson();

			if (!users.isEmpty()) {
				usersList.get(idAL).ID = ((JSONObject) users.get(0)).get("id")
						.toString();
				/*
				 * usersList.get(idAL).userFirstName = ((JSONObject)
				 * users.get(0)) .get("firstName").toString();
				 * usersList.get(idAL).userLastName = ((JSONObject)
				 * users.get(0)) .get("lastName").toString();
				 */
				JSONArray counters = (JSONArray) ((JSONObject) users.get(0))
						.get("counterValues");
				if (!counters.isEmpty())
					for (int i = 0; i < counters.size(); i++) {
						if (((JSONObject) counters.get(i)).get("counter")
								.toString().equals("1")) { // COUNTER PLACE ID=1
							int groupId = Integer
									.parseInt(((JSONObject) counters.get(i))
											.get("value").toString());
							JSONObject group = (JSONObject) isaac
									.path("/cache/users/groups/" + groupId)
									.get().getJson();
							usersList.get(idAL).userPlace = group.get("label")
									.toString();
						}
					}
			}
		} catch (IOException e) {
		} catch (IsaacloudConnectionException e) {
		}
	}

}
