package tim.oops;


import java.util.Calendar;

/**
 * Class to hold an entry in the birthday list
 */
public class Birthday
{
	private String _firstName, _lastName, _description;
	private Date _date;
	private final boolean _ok;
	private boolean _added = false;
	private boolean _deleted = false;
	private Birthday _originalData = null;


	/**
	 * Constructor giving all parameters
	 */
	public Birthday(String inFirstName, String inLastName, String inDescription,
		int inDayNum, int inMonthNum, int inYearNum)
	{
		this(inFirstName, inLastName, inDescription, new Date(inDayNum, inMonthNum, inYearNum));
	}

	/**
	 * Constructor without the year
	 */
	public Birthday(String inFirstName, String inLastName, String inDescription,
		int inDayNum, int inMonthNum)
	{
		this(inFirstName, inLastName, inDescription, new Date(inDayNum, inMonthNum));
	}

	/**
	 * Constructor giving already parsed Date object
	 */
	public Birthday(String inFirstName, String inLastName, String inDescription, Date inDate)
	{
		_firstName = capitaliseString(inFirstName);
		_lastName = capitaliseString(inLastName);
		_description = capitaliseString(inDescription);
		_date = inDate;
		_ok = checkDetails();
	}

	public String getFirstName() { return (_firstName==null?"":_firstName); }
	public String getLastName() { return (_lastName==null?"":_lastName); }
	public String getDescription() { return (_description==null?"":_description); }
	public Date getDate() { return _date; }
	public boolean isOk() { return _ok; }

	/**
	 * Get the age today
	 */
	public int getAge(Date inToday)
	{
		if (!_date.hasYear()) {
			return -1;
		}
		int age = inToday.getYearNum() - _date.getYearNum();
		// Subtract one if they've not had their birthday yet
		if (inToday.getMonthNum() < _date.getMonthNum() ||
		 (inToday.getMonthNum() == _date.getMonthNum() && inToday.getDayNum() < _date.getDayNum())) {
			age--;
		}
		return age;
	}

	/**
	 * Get the age today as a double
	 */
	public double getAgeAsDouble()
	{
		if (!_date.hasYear()) {
			return -1.0;
		}
		Calendar calToday = Calendar.getInstance();
		final long nowMillis = calToday.getTimeInMillis();
		calToday.set(Calendar.YEAR, _date.getYearNum());
		calToday.set(Calendar.MONTH, _date.getMonthNum()-1);
		calToday.set(Calendar.DATE, _date.getDayNum());
		final long birthMillis = calToday.getTimeInMillis();
		long daysOld = (nowMillis - birthMillis) / 1000L / 60L / 60L / 24L;
		return daysOld / 365.25;
	}

	public boolean isToday(Date inToday) {
		return inToday.getMonthNum() == _date.getMonthNum()
			&& inToday.getDayNum() == _date.getDayNum();
	}

	/**
	 * Turn a string into a capitalised String unless it already
	 * contains mixed case
	 */
	private static String capitaliseString(String inString)
	{
		String str = (inString == null ? "" : inString.trim());
		// If the String is empty, return it
		if (str.equals("")) {
			return "";
		}
		// If the String is single character, upper case it
		if (str.length() == 1) return str.toUpperCase();

		// Loop through the characters of the String
		boolean hasLower = false;
		boolean hasUpper = false;
		int strLen = str.length();
		for (int i=0; i<strLen && (!hasLower || !hasUpper); i++)
		{
			char ch = str.charAt(i);
			if (ch >= 'a' && ch <= 'z')
				hasLower = true;
			if (ch >= 'A' && ch <= 'Z')
				hasUpper = true;
		}
		// if it's already mixed case, return the given String
		if (hasLower && hasUpper)
			return str;
		// It's single case, so return the modified String
		return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
	}


	/**
	 * Set the 'added' flag to true
	 */
	public void setAdded() {
		_added = true;
	}

	/**
	 * Set the 'deleted' flag to true
	 */
	public void setDeleted() {
		_deleted = true;
	}

	/**
	 * @return true if this hasn't been deleted
	 */
	public boolean isActive() {
		return !_deleted;
	}

	/**
	 * Mark the Birthday as modified, by saving current state
	 */
	public void setModified()
	{
		if (_originalData == null) {
			_originalData = new Birthday(getFirstName(), getLastName(), getDescription(),
				getDate().getDayNum(), getDate().getMonthNum(), getDate().getYearNum());
		}
	}

	/**
	 * Returns true if the Birthday has been modified (in any way)
	 */
	public boolean isModified()
	{
		if (_added) {return !_deleted;}
		if (_deleted) {return true;}
		return !equals(_originalData);
	}

	/**
	 * Check the fields and return true if they're all ok
	 */
	private boolean checkDetails()
	{
		_description = (_description == null ?
			"" : _description.trim().replaceAll("\t", " ").replaceAll(" +", " "));
		return _firstName != null && !_firstName.isEmpty()
				&& _lastName != null && !_lastName.isEmpty()
				&& _date != null && _date.isValid();
	}

	/**
	 * Return true if the other birthday could be a duplicate
	 * of this one (maybe not an exact match but close)
	 */
	public boolean matches(Birthday inOther)
	{
		if (inOther == null) return false;
		// Compare first name and last name
		int score = 0;
		String FN1 = getFirstName().toUpperCase();
		String FN2 = inOther.getFirstName().toUpperCase();
		if (FN1.contains(FN2) || FN2.contains(FN1)) {score++;}
		String LN1 = getLastName().toUpperCase();
		String LN2 = inOther.getLastName().toUpperCase();
		if (LN1.contains(LN2) || LN2.contains(LN1)) {score++;}
		// Compare day and month of birthday
		if ( (getDate().getDayNum() == inOther.getDate().getDayNum())
		  && (getDate().getMonthNum() == inOther.getDate().getMonthNum()) )
		{
			score++;
			// If the birthday is the same then also check year
			if (getDate().getYearNum() > 0 && (getDate().getYearNum() == inOther.getDate().getYearNum())) {
				score++;
			}
		}
		return (score >= 2);
	}

	/**
	 * Return a displayable String giving the basic details
	 */
	public String toString() {
		return getName() + " - " + getDate().getDescription();
	}

	/**
	 * Compare two Birthday objects
	 */
	public boolean equals(Birthday inOther) {
		if (inOther == null) return true;
		return _firstName.equals(inOther._firstName)
				&& _lastName.equals(inOther._lastName)
				&& areEqual(_description, inOther._description)
				&& _date.equals(inOther._date);
	}

	/**
	 * Compare two strings which might be null
	 */
	private static boolean areEqual(String description1, String description2) {
		if (description1 == null) {return description2 == null;}
		return description1.equals(description2);
	}

	public String getName() {
		return (getFirstName() + " " + getLastName()).trim();
	}

	public void copyFrom(Birthday edit) {
		if (edit.isOk()) {
			setModified();
			_firstName = edit._firstName;
			_lastName = edit._lastName;
			_description = edit._description;
			_date = edit._date;
		}
	}
}
