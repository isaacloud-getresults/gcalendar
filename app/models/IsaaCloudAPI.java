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

	public void addAchievementForPunktualMeeting(String userEmail) {
		JSONObject body = new JSONObject();
		body.put("status", "punctual");

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
			if (id != null) {
				JSONObject group = (JSONObject) isaac
						.path("/cache/users/groups/" + id).get().getJson();
				return group.get("label").toString();
			}
		} catch (IOException e) {
		} catch (IsaacloudConnectionException e) {
		}
		return "";
	}

	public void deleteRoom(String name) throws IOException,
			IsaacloudConnectionException {
		Map<String, Object> params = new HashMap<>();
		params.put("limit", "0");
		SortedMap<String, String> query = new TreeMap<>();
		query.put("name", name);
		if (!((JSONArray) isaac.path("/cache/segments").withQuery(query).get()
				.getJson()).isEmpty()) {
			int segmentId = Integer.parseInt(((JSONObject) ((JSONArray) isaac
					.path("/cache/segments").withQuery(query).get().getJson())
					.get(0)).get("id").toString());
			isaac.path("/admin/segments/" + segmentId).delete();
			isaac.path("/admin/segments/" + (segmentId + 1)).delete();
			isaac.path("/admin/segments/" + (segmentId + 2)).delete();
		}

		if (!((JSONArray) isaac.path("/admin/notifications/types")
				.withQuery(query).get().getJson()).isEmpty()) {
			int nType = Integer.parseInt(((JSONObject) ((JSONArray) isaac
					.path("/admin/notifications/types").withQuery(query).get()
					.getJson()).get(0)).get("id").toString());
			isaac.path("/admin/notifications/types/" + nType).delete();
		}

		JSONArray conditions = (JSONArray) isaac.path("/admin/conditions")
				.withQueryParameters(params).get().getJson();
		for (int i = 0; i < conditions.size(); i++)
			if (((JSONObject) conditions.get(i)).get("name").toString()
					.contains(name + "_visit"))
				isaac.path(
						"/admin/conditions/"
								+ ((JSONObject) conditions.get(i)).get("id"))
						.delete();
			else if (((JSONObject) conditions.get(i)).get("name").toString()
					.contains(name + "_exit_"))
				isaac.path(
						"/admin/conditions/"
								+ ((JSONObject) conditions.get(i)).get("id"))
						.delete();

		JSONArray counters = (JSONArray) isaac.path("/cache/counters")
				.withQueryParameters(params).get().getJson();
		for (int i = 0; i < counters.size(); i++)
			if (((JSONObject) counters.get(i)).get("name").toString()
					.contains(name + "_counter")
					|| ((JSONObject) counters.get(i)).get("name").toString()
							.contains(name + "_group_counter"))
				isaac.path(
						"/admin/counters/"
								+ ((JSONObject) counters.get(i)).get("id"))
						.delete();

		JSONArray achievement = (JSONArray) isaac.path("/cache/achievements")
				.withQueryParameters(params).get().getJson();
		for (int i = 0; i < achievement.size(); i++)
			if (((JSONObject) achievement.get(i)).get("name").toString()
					.contains(name + "_visit"))
				isaac.path(
						"/admin/achievements/"
								+ ((JSONObject) achievement.get(i)).get("id"))
						.delete();

		JSONArray games = (JSONArray) isaac.path("/cache/games")
				.withQueryParameters(params).get().getJson();
		for (int i = 0; i < games.size(); i++)
			if (((JSONObject) games.get(i)).get("name").toString()
					.contains(name + "_exit_"))
				isaac.path(
						"/admin/games/" + ((JSONObject) games.get(i)).get("id"))
						.delete();
			else if (((JSONObject) games.get(i)).get("name").toString()
					.contains(name + "_visit_")) {
				for (int j = 0; j < ((JSONArray) ((JSONObject) games.get(i))
						.get("notifications")).size(); j++)
					isaac.path(
							"/admin/notifications/"
									+ ((JSONArray) ((JSONObject) games.get(i))
											.get("notifications")).get(j))
							.delete();
				isaac.path(
						"/admin/games/" + ((JSONObject) games.get(i)).get("id"))
						.delete();
			}

		JSONArray group = (JSONArray) isaac.path("/cache/users/groups")
				.withQueryParameters(params).get().getJson();
		for (int i = 0; i < group.size(); i++)
			if (((JSONObject) group.get(i)).get("name").toString().equals(name))
				isaac.path(
						"/admin/users/groups/"
								+ ((JSONObject) group.get(i)).get("id"))
						.delete();
	}
}
