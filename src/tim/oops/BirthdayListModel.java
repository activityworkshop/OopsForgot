package tim.oops;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class to hold a sortable list of birthdays
 */
public class BirthdayListModel extends AbstractListModel<Birthday>
{
	private final ArrayList<Birthday> _birthdays = new ArrayList<>();

	public void clear() {_birthdays.clear();}

	public void addBirthday(Birthday b) {
		_birthdays.add(b);
	}

	public void sort(Comparator<Birthday> comp) {
		_birthdays.sort(comp);
		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public int getSize() {
		return _birthdays.size();
	}

	@Override
	public Birthday getElementAt(int i) {
		if (i < 0) {return null;}
		return _birthdays.get(i);
	}
}