package tim.oops;

import java.util.Comparator;

/**
 * Class to compare two Birthday objects for sorting
 */
public class BirthdaySorter implements Comparator<Birthday>
{
	private enum SortType {FIRST_NAME, LAST_NAME, DATE}
	public static final BirthdaySorter SORT_BY_FIRST_NAME = new BirthdaySorter(SortType.FIRST_NAME);
	public static final BirthdaySorter SORT_BY_LAST_NAME  = new BirthdaySorter(SortType.LAST_NAME);
	public static final BirthdaySorter SORT_BY_DATE       = new BirthdaySorter(SortType.DATE);

	private final SortType _sortType;

	/**
	 * Constructor, giving type of sort
	 */
	private BirthdaySorter(SortType inType) {
		_sortType = inType;
	}

	/**
	 * Compare two Birthday objects
	 */
	public int compare(Birthday b1, Birthday b2)
	{
		int result = 0;
		switch (_sortType)
		{
			case FIRST_NAME:
				// First compare first name, then last name
				result = b1.getFirstName().compareTo(b2.getFirstName());
				if (result == 0)
					result = b1.getLastName().compareTo(b2.getLastName());
				break;

			case LAST_NAME:
				// First compare last name, then first name
				result = b1.getLastName().compareTo(b2.getLastName());
				if (result == 0)
					result = b1.getFirstName().compareTo(b2.getFirstName());
				break;

			case DATE:
				// First compare the months, then compare day if necessary
				result = b1.getDate().getMonthNum() - b2.getDate().getMonthNum();
				if (result == 0)
					result = b1.getDate().getDayNum() - b2.getDate().getDayNum();
				break;
		}
		if (result == 0) {
			result = b1.toString().compareTo(b2.toString());
		}
		return result;
	}
}