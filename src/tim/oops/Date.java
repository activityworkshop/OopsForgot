package tim.oops;

/**
 * Class to represent a date, with day, month and year
 * Can represent a birthdate or today's date
 */
public class Date
{
	private static final String[] MONTH_NAMES = {null, "January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"};
	private static final int MISSING_YEAR = -1;

	private final int _dayNum, _monthNum, _yearNum;

	/**
	 * Constructor giving day, month and year
	 */
	public Date(int inDay, int inMonth, int inYear)
	{
		_dayNum = inDay;
		_monthNum = inMonth;
		_yearNum = inYear;
	}

	/**
	 * Constructor without a year, using default
	 */
	public Date(int inDay, int inMonth)
	{
		this(inDay, inMonth, MISSING_YEAR);
	}

	public int getDayNum() { return _dayNum; }
	public int getMonthNum() { return _monthNum; }
	public int getYearNum() { return _yearNum; }
	public boolean hasYear() { return (getYearNum() != MISSING_YEAR); }

	/**
	 * Returns true if the day and month seem valid
	 */
	public boolean isValid()
	{
		if (getMonthNum() < 1 || getMonthNum() > 12)
			return false;
		return getDayNum() >= 1 && getDayNum() <= getDaysInMonth(getMonthNum(), getYearNum());
	}

	/**
	 * Return Date as a String
	 */
	public String toString()
	{
		String answer = getDayNum() + "-" + getMonthNum();
		if (getYearNum() > 0)
			answer += ("-" + getYearNum());
		return ("(" + answer + ")");
	}

	public String getDescription() {
		String result = getDayNum() + " " + getMonthDescription();
		if (hasYear()) {result += " " + getYearNum();}
		return result;
	}

	private String getMonthDescription() {
		try {
			if (getMonthNum() > 0) {
				return MONTH_NAMES[getMonthNum()];
			}
		}
		catch (ArrayIndexOutOfBoundsException ignore) {}
		return "?";
	}

	/**
	 * Calculate the number of days until another date
	 * (can be positive (in the future) or negative (in the past)
	 */
	public int getDaysUntil(Date other)
	{
		if (other.getMonthNum() == getMonthNum()) {
			return other.getDayNum() - getDayNum();
		}
		int numDaysUntil = getDaysInMonth(getMonthNum(), getYearNum()) - getDayNum() + other.getDayNum();
		int month = getMonthNum()+1, year = getYearNum();
		if (month > 12) {
			month = 1; year++;
		}
		// loop forwards over months inbetween, adding getDaysInMonth()
		while (month != other.getMonthNum())
		{
			numDaysUntil += getDaysInMonth(month, year);
			month++;
			if (month > 12)
			{
				month = 1;
				year++;
			}
		}
		// Now calculate days since last one, in case it's nearer
		int numDaysSince = getDaysInMonth(other.getMonthNum(), getYearNum()) - other.getDayNum() + getDayNum();
		month = getMonthNum()-1; year = getYearNum();
		if (month < 1) {
			month = 12; year--;
		}
		// loop backwards over months inbetween, adding getDaysInMonth()
		while (month != other.getMonthNum())
		{
			numDaysSince += getDaysInMonth(month, year);
			month--;
			if (month < 1)
			{
				month = 12;
				year--;
			}
		}
		// System.out.println("*** From " + toString() + " to " + other.toString() + ": since=" + numDaysSince + ", until=" + numDaysUntil);
		if (numDaysUntil <= numDaysSince)
			return numDaysUntil;
		else
			return (-numDaysSince);
	}


	/**
	 * Get the number of days in the given month and year
	 */
	private static int getDaysInMonth(int inMonth, int inYear)
	{
		switch (inMonth)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12: return 31;
			case 4:
			case 6:
			case 9:
			case 11: return 30;
			case 2: if (inYear <= 0 || isLeapYear(inYear)) return 29; else return 28;
		}
		throw new IllegalArgumentException("MonthNum = " + inMonth);
	}


	/**
	 * Return true if the given year is a leap year
	 */
	public static boolean isLeapYear(int inYear)
	{
		if ((inYear % 4) != 0)
			return false;
		boolean isLeap = (inYear % 100) != 0;
		if ((inYear % 400) == 0)
			isLeap = true;
		return isLeap;
	}

	/**
	 * Compare two date objects
	 */
	public boolean equals(Date inOther) {
		if (inOther == null) {return false;}
		return _dayNum == inOther._dayNum
				&& _monthNum == inOther._monthNum
				&& _yearNum == inOther._yearNum;
	}
}