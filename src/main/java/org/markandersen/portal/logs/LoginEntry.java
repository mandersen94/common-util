package org.markandersen.portal.logs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.markandersen.text.MessageFormatter;

/**
 * 
 * @author e63582
 */
public class LoginEntry implements Comparable<LoginEntry> {

	private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";

	private String userid;

	private Date loginTime;

	/**
	 * 2007/10/30 13:52:27.112| |Servlet.Engine.Transports :
	 * 65|StatsLogger|LOGIN - SWALIFE: user e70794 successfully logged in.
	 * internalNetworkUser = true, ip address = 172.19.54.246, preferredFullName =
	 * PreferredFullName NA, deptDesc = Ground Ops, Attributes[sn = Williams,
	 * PreferredFullName = PreferredFullName NA, cn = e70794, preferredName =
	 * BrianD, Preferred First Name = Preferred First Name NA, swaUniqueID =
	 * 70794, uid = e70794, swaDeptCode = 03, mail = BrianD.Williams@wnco.com,
	 * swaDeptDesc = Ground Ops, swaLocation = PBI, ], Groups[GO Station
	 * Ldrs_SR, GO Ramp Sup_R, GROUND OPS Employees_SR, Portal Users_D,
	 * SWA_Leaders_D, Ground Ops Weather_D, SWA Employees_D, Kronos_GroundOps_D,
	 * Exchange Users_D, GO_STATION_LDRS_D, Brutis_D, ]
	 * 
	 * @param line
	 * @throws ParseException
	 */
	public void parse(String line) throws ParseException {
		String[] pipedUnits = line.split("\\|");
		String dateString = pipedUnits[0];
		String end = pipedUnits[4];
		try {
			SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
			loginTime = df.parse(dateString);

			String matchingText = "LOGIN - SWALIFE: user ";
			int indexOf = end.indexOf(matchingText);
			if (indexOf < 0) {
				matchingText = "LOGIN - RETIREE: user ";
				indexOf = end.indexOf(matchingText);
			}

			int userNameEndIndex = end.indexOf(" ", indexOf
					+ matchingText.length());
			String username = end.substring(matchingText.length(),
					userNameEndIndex);
			userid = username;

		} catch (RuntimeException ex) {
			System.out.println(MessageFormatter.format("log entry {}", end));
			throw ex;
		}
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String toString() {
		return userid;
	}

	public int compareTo(LoginEntry o) {

		return userid.compareTo(o.getUserid());
	}

}
