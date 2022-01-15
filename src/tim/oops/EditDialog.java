package tim.oops;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Class for the 'Edit birthday' dialog
 */
public class EditDialog extends AddDialog
{
	private Birthday _origBirthday = null;

	/** Constructor */
	public EditDialog(Frame inParentFrame, ActionListener inAddListener) {
		super(inParentFrame, inAddListener);
	}

	protected void setTitle() {
		setTitle("Edit birthday");
		_okButton.setText("Edit");
	}

	/**
	 * Initialise the fields and show the dialog for the given Birthday
	 */
	public void initAndShow(Birthday inBday)
	{
		setTitle();
		_origBirthday = inBday;
		_firstNameField.setText(inBday.getFirstName());
		_lastNameField.setText(inBday.getLastName());
		_descriptionField.setText(inBday.getDescription());
		if (inBday.getDate().hasYear()) {
			_yearField.setValue(inBday.getDate().getYearNum());
		}
		else {
			_yearField.clearValue();
		}
		_dayDropDown.setSelectedIndex(inBday.getDate().getDayNum() - 1);
		_monthDropDown.setSelectedIndex(inBday.getDate().getMonthNum() - 1);
		_firstNameField.requestFocusInWindow();
		_okButton.setEnabled(true);
		setVisible(true);
	}

	/** @return original birthday from before edit */
	public Birthday getOriginalBirthday() {
		return _origBirthday;
	}
}
