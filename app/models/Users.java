package models;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Users {
	public String ID;
	public String userEmail = "noInDb";
	public String userFirstName = "noInDb";
	public String userLastName = "noInDb";
	public String userPlace = "noInDb";
	public String userStatus = "unaviable";
	public String userInfo = "";
	public long time;

	public void calculateStatus() {
		switch (this.userPlace) {
		case "Meeting Room": {
			this.userStatus = "present";
			break;
		}
		case "Kitchen": {
			long time = (this.time - new Date().getTime() - 300000);
			String prefix = "";
			if (time < 0)
				prefix = " late";
			else
				prefix = " to go";
			time = Math.abs(time);
			this.userStatus = ""
					+ TimeUnit.MILLISECONDS.toMinutes(time)
					+ "min "
					+ (TimeUnit.MILLISECONDS.toSeconds(time)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(time)) + "sec" + prefix);

			break;
		}
		case "Restaurant": {
			long time = (this.time - new Date().getTime() - 900000);
			String prefix = "";
			if (time < 0)
				prefix = " late";
			else
				prefix = " to go";
			time = Math.abs(time);
			this.userStatus = ""
					+ TimeUnit.MILLISECONDS.toMinutes(time)
					+ "min "
					+ (TimeUnit.MILLISECONDS.toSeconds(time)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(time)) + "sec" + prefix);

			break;
		}
		case "Office": {
			long time = (this.time - new Date().getTime() - 150000);
			String prefix = "";
			if (time < 0)
				prefix = " late";
			else
				prefix = " to go";
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
			this.userStatus = "unavailable";
			break;
		}

		}
	}
}
