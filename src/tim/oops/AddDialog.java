package tim.oops;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class for the 'Add birthday' dialog
 */
public class AddDialog extends JDialog
{
	/**
	 * Inner class for closing the dialog
	 */
	class Escaper extends KeyAdapter
	{
		public void keyReleased(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				AddDialog.this.setVisible(false);
			}
		}
	}

	// instance variables
	protected JTextField _firstNameField, _lastNameField;
	protected JTextField _descriptionField;
	protected YearField _yearField;
	protected JComboBox<String> _dayDropDown, _monthDropDown;
	protected JButton _okButton;


	/**
	 * Make the add birthday dialog
	 */
	public AddDialog(Frame inParentFrame, ActionListener inAddListener)
	{
		super(inParentFrame, "no title yet", true);

		getContentPane().add(makeContentsPanel(inAddListener));
		getRootPane().setDefaultButton(_okButton);
		setResizable(false);
		pack();
	}

	protected void setTitle() {
		setTitle("Add new birthday");
		_okButton.setText("Add");
	}

	/**
	 * Make the contents of the dialog box
	 */
	private Container makeContentsPanel(ActionListener inAddListener)
	{
		Escaper escaper = new Escaper();
		JPanel addPanel = new JPanel();
		addPanel.setLayout(new BorderLayout(10, 20));
		// Two buttons at the bottom
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		cancelButton.addKeyListener(escaper);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		_okButton = new JButton("Add / Edit");
		_okButton.addActionListener(actionEvent -> {
			AddDialog.this.setVisible(false);
			inAddListener.actionPerformed(actionEvent);
		});
		_okButton.addKeyListener(escaper);
		bottomPanel.add(_okButton);
		bottomPanel.add(cancelButton);
		addPanel.add(bottomPanel, BorderLayout.SOUTH);

		// Main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 6, 3, 6);
		mainPanel.add(makeRightAlignedLabel("First name"), gbc);
		_firstNameField = new NameField();
		_firstNameField.addKeyListener(escaper);
		_firstNameField.addActionListener(actionEvent -> namesUpdated());
		gbc.gridx = 1; gbc.gridwidth = 2;
		mainPanel.add(_firstNameField, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
		mainPanel.add(makeRightAlignedLabel("Last name"), gbc);
		_lastNameField = new NameField();
		_lastNameField.addKeyListener(escaper);
		_lastNameField.addActionListener(actionEvent -> namesUpdated());
		gbc.gridx = 1; gbc.gridwidth = 2;
		mainPanel.add(_lastNameField, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
		mainPanel.add(makeRightAlignedLabel("Description", "(optional)"), gbc);
		_descriptionField = new JTextField();
		_descriptionField.addKeyListener(escaper);
		gbc.gridx = 1; gbc.gridwidth = 2;
		mainPanel.add(_descriptionField, gbc);

		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
		mainPanel.add(makeRightAlignedLabel("Date"), gbc);
		String[] dateNames = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
		 "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		 "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
		_dayDropDown = new JComboBox<>(dateNames);
		_dayDropDown.addKeyListener(escaper);
		gbc.gridx = 1;
		mainPanel.add(_dayDropDown, gbc);
		String[] monthNames = {"January", "February", "March", "April", "May", "June",
		 "July", "August", "September", "October", "November", "December"};
		_monthDropDown = new JComboBox<>(monthNames);
		_monthDropDown.addKeyListener(escaper);
		gbc.gridx = 2;
		mainPanel.add(_monthDropDown, gbc);

		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
		mainPanel.add(makeRightAlignedLabel("Year", "(optional)"), gbc);
		_yearField = new YearField();
		_yearField.addKeyListener(escaper);
		gbc.gridx = 1;
		mainPanel.add(_yearField, gbc);
		addPanel.add(mainPanel, BorderLayout.CENTER);

		return addPanel;
	}

	private void namesUpdated() {
		_okButton.setEnabled(!_firstNameField.getText().isEmpty()
			&& !_lastNameField.getText().isEmpty());
	}

	private static Component makeRightAlignedLabel(String labelText) {
		return makeRightAlignedLabel(labelText, null);
	}

	private static Component makeRightAlignedLabel(String line1, String line2) {
		String labelText = (line2 == null ?
			line1 : ("<html>" + line1 + "<br>" + line2 + "</html>"));
		JLabel label = new JLabel(labelText);
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}

	/**
	 * Initialise the fields and show the dialog
	 */
	public void initAndShow(Date inToday)
	{
		setTitle();
		_firstNameField.setText("");
		_lastNameField.setText("");
		_descriptionField.setText("");
		_yearField.setText("");
		if (inToday != null && inToday.isValid())
		{
			_dayDropDown.setSelectedIndex(inToday.getDayNum() - 1);
			_monthDropDown.setSelectedIndex(inToday.getMonthNum() - 1);
		}
		else
		{
			_dayDropDown.setSelectedIndex(0);
			_monthDropDown.setSelectedIndex(0);
		}
		_firstNameField.requestFocusInWindow();
		_okButton.setEnabled(false);
		setVisible(true);
	}

	public Birthday getResults() {
		if (_yearField.hasValue()) {
			return new Birthday(_firstNameField.getText(), _lastNameField.getText(),
				_descriptionField.getText(), _dayDropDown.getSelectedIndex() + 1,
				_monthDropDown.getSelectedIndex()+1, _yearField.getValue());
		}
		// No year given, that's also ok
		return new Birthday(_firstNameField.getText(), _lastNameField.getText(),
			_descriptionField.getText(), _dayDropDown.getSelectedIndex() + 1,
			_monthDropDown.getSelectedIndex()+1);
	}
}