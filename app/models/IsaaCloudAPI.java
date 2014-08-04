package models;

import java.util.HashMap;
import java.util.Map;

import com.isaacloud.sdk.Isaacloud;

public class IsaaCloudAPI {

	public Isaacloud isaac;

	public IsaaCloudAPI() {
		Map<String, String> config = new HashMap<>();
		config.put("clientId", "179");
		config.put("secret", "cb7de01c3f1d6d3d5ed2acb1580a997");
		isaac = new Isaacloud(config);
	}

}
