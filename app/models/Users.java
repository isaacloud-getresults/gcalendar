package models;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Users {
	public String ID;
	public String userEmail = "noInDatabase";
	public String userFirstName = "noInDatabase";
	public String userLastName = "noInDatabase";
	public String userPlace = "noInDatabase";
	public String userStatus = "unaviable";
	public long time;

	public void calculateStatus() {
		switch (this.userPlace) {
		case "Meeting Room": {
			this.userStatus = "PRESENT;";
			break;
		}
		case "Kitchen": {
			long time = (this.time - new Date().getTime() - 300000);
			String prefix = "";
			if (time < 0)
				prefix = " LATE;";
			else
				prefix = " TO GO;";
			time = Math.abs(time);
			this.userStatus = ""
					+ TimeUnit.MILLISECONDS.toMinutes(time)
					+ "min "
					+ (TimeUnit.MILLISECONDS.toSeconds(time)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(time)) + "sec" + prefix);

			break;
		}
		default: {
			this.userStatus = "UNAVAILABLE;";
			break;
		}

		}
	}
}
