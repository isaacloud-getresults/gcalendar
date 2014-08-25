package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.isaacloud.sdk.Isaacloud;
import com.isaacloud.sdk.IsaacloudConnectionException;

public class IsaaCloudAPI {

	private Isaacloud isaac;

	public IsaaCloudAPI(String isaaBase64) {
		String decodedBase64 = StringUtils.newStringUtf8(Base64
				.decodeBase64(isaaBase64));
		if (decodedBase64.contains(":")) {
			String[] token = decodedBase64.split(":");
			String id = token[0].toString();
			String secret = token[1].toString();
			Map<String, String> config = new HashMap<>();
			config.put("clientId", id);
			config.put("secret", secret);
			isaac = new Isaacloud(config);
		}
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

	public String getRoomLabel(String id) {
		try {
			JSONObject group = (JSONObject) isaac
					.path("/cache/users/groups/" + id).get().getJson();
			return group.get("label").toString();
		} catch (IOException e) {
		} catch (IsaacloudConnectionException e) {
		}
		return null;
	}

}
