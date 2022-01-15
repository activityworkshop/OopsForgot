package tim.oops;

import java.util.List;

public interface Reminder
{
	List<Birthday> getBirthdays();
	List<BirthdayWarning> getUpcomingWarnings();
	List<BirthdayWarning> getRecentWarnings();

	/** @return true if anything has been changed and needs to be saved */
	boolean needToSave();
	/** @return true on successful save */
	boolean saveAll();

	void finishAdd(Birthday edit);
	void finishEdit(Birthday orig, Birthday edit);
	void finishDelete(Birthday edit);

	/** Check for dupes before adding */
	default List<Birthday> getDupeListForAdd(Birthday inBirthday) {
		return getDupeListForEdit(inBirthday, null);
	}
	/** Check for dupes before editing */
	List<Birthday> getDupeListForEdit(Birthday inRequested, Birthday inOrig);
}
