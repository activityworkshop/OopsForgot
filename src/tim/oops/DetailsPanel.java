package tim.oops;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Calendar;


/**
 * Class for the details panel on the right-hand side.
 * Able to delegate instructions to the parent dialog
 * via the Forwarder interface
 */
public class DetailsPanel extends JPanel
{
	private final Forwarder _parent;
	private final JEditorPane _pane;
	private final JButton _deleteButton;
	private final JButton _editButton;
	private Birthday _currBirthday = null;

	public DetailsPanel(Forwarder inParent) {
		_parent = inParent;
		_pane = new JEditorPane();
		_pane.setContentType("text/html");
		_pane.setEditable(false);
		_pane.setOpaque(false);
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Details"));
		setLayout(new BorderLayout());
		add(_pane, BorderLayout.CENTER);
		JPanel lowerPanel = new JPanel();
		_deleteButton = new JButton("Delete");
		_deleteButton.addActionListener(actionEvent -> deleteCurrentBirthday());
		lowerPanel.add(_deleteButton);
		_editButton = new JButton("Edit");
		_editButton.addActionListener(actionEvent -> editCurrentBirthday());
		lowerPanel.add(_editButton);
		add(lowerPanel, BorderLayout.SOUTH);
		setBirthday(null);
	}

	public void setBirthday(Birthday inBirthday) {
		_currBirthday = inBirthday;
		refresh();
	}

	public void refresh() {
		final boolean gotOne = (_currBirthday != null);
		if (gotOne) {
			StringBuilder buff = new StringBuilder();
			buff.append("<html><h1>").append(_currBirthday.getFirstName()).append(' ').append(_currBirthday.getLastName()).append("</h1>");
			if (!_currBirthday.getDescription().isEmpty()) {
				buff.append("<p>").append(_currBirthday.getDescription()).append("</p>");
			}
			buff.append("<p>").append(_currBirthday.getDate().getDescription()).append("</p>");
			if (_currBirthday.getDate().hasYear()) {
				buff.append("<p>Age: ").append(describeAge(_currBirthday)).append("</p>");
			}
			if (_currBirthday.isModified()) {
				buff.append("<p>(<i>modified</i>)</p>");
			}
			buff.append("</html>");
			_pane.setText(buff.toString());
		}
		else {
			_pane.setText("<html><i>Select a birthday to see the details</i></html>");
		}
		_deleteButton.setVisible(gotOne);
		_editButton.setVisible(gotOne);
	}

	private String describeAge(Birthday inBirthday) {
		Calendar calToday = Calendar.getInstance();
		Date today = new Date(calToday.get(Calendar.DATE), calToday.get(Calendar.MONTH) + 1, calToday.get(Calendar.YEAR));
		if (inBirthday.isToday(today)) {
			return inBirthday.getAge(today) + " today!";
		}
		double exactAge = inBirthday.getAgeAsDouble();
		int wholeAge = (int) Math.floor(exactAge);
		double fraction = exactAge - wholeAge;
		if (fraction < 0.2) {
			return "recently " + wholeAge;
		}
		if (fraction > 0.8) {
			return "nearly " + (wholeAge + 1);
		}
		return wholeAge + (wholeAge < 10 ? " years old" : "");
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension original = super.getPreferredSize();
		TitledBorder titleBorder = (TitledBorder) getBorder();
		int width = (int)Math.max( original.getWidth(), 60 + (int)titleBorder.getMinimumSize(this).getWidth());
		return new Dimension( width, (int)original.getHeight() );
	}

	private void deleteCurrentBirthday() {
		if (_currBirthday != null) {
			_parent.startDelete(_currBirthday);
		}
	}

	private void editCurrentBirthday() {
		if (_currBirthday != null) {
			_parent.startEdit(_currBirthday);
		}
	}

	public void showEmptyMessage() {
		_pane.setText("<html><p>No birthdays found.</p><p><i>Use the 'Add birthday' button to get started.</p></html>");
	}
}
