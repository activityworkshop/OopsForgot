package tim.oops;

/**
 * Sortable warning message
 */
public class BirthdayWarning implements Comparable<BirthdayWarning>
{
	private final Birthday _birthday;
	private final int _numDays;


	/**
	 * Constructor
	 */
	public BirthdayWarning(Birthday inBirthday, int inNumDays)
	{
		_birthday = inBirthday;
		_numDays = inNumDays;
	}

	public int getNumDays() { return _numDays; }

	public boolean isUrgent() { return (Math.abs(_numDays) <= 2); }

	/**
	 * @return a warning string
	 */
	public String getWarning(Date inToday)
	{
		String message;
		if (_numDays == 0)
			message = "TODAY!";
		else if (_numDays == -1)
			message = "Yesterday";
		else if (_numDays < -1)
			message = (-_numDays) + " days ago";
		else if (_numDays == 1)
			message = "TOMORROW!";
		else message = "In " + _numDays + " days";

		message += " - " + _birthday.getFirstName() + " " + _birthday.getLastName();
		if (_birthday.getDescription() != null && !_birthday.getDescription().equals("")) {
			message += (" (" + _birthday.getDescription() + ")");
		}
		int age = _birthday.getAge(inToday);
		if (age >= 0)
		{
			if (_numDays == 0)
				message += (" - " + age);
			else if (_numDays > 0)
				message += (" - will be " + (age+1));
			else message += (" - was " + age);
		}
		return message;
	}

	/**
	 * Sort according to day offset
	 */
	public int compareTo(BirthdayWarning inOther)
	{
		int compare = Math.abs(getNumDays()) - Math.abs(inOther.getNumDays());
		if (compare == 0) {
			compare = _birthday.getName().compareTo(inOther._birthday.getName());
			if (compare == 0) {
				// Names identical, let's hope the description or year can separate them
				compare = BirthdayFile.write(_birthday).compareTo(BirthdayFile.write(inOther._birthday));
			}
		}
		return compare;
	}
}