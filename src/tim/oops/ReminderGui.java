package tim.oops;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Calendar;
import java.util.List;


/**
 * Main GUI class controlling the dialog
 */
public class ReminderGui implements Forwarder
{
	private final Reminder _parent;

	private final JFrame _mainFrame;
	private BirthdayListModel _modelForNames;
	private BirthdayListModel _modelForMonths;
	private JList<Birthday> _peopleListBox = null;
	private JList<Birthday> _monthListBox = null;

	private JPanel _warningsPanel = null;
	private WarningPanel _upcomingPanel = null, _recentPanel = null;
	private BirthdaySorter _sorter = BirthdaySorter.SORT_BY_FIRST_NAME;
	private JComboBox<String> _monthDropdown = null;
	private JSplitPane _horizSplitPane = null;
	private DetailsPanel _detailsPanel = null;
	private JLabel _todayDescLabel = null;
	private AddDialog _addEditDialog = null;


	/**
	 * Inner class to listen for sorting events
	 */
	class SortListener implements ActionListener
	{
		private final boolean _sortFirst;
		public SortListener(boolean inFirst) { _sortFirst = inFirst; }
		public void actionPerformed(ActionEvent e)
		{
			sortPeople(_sortFirst);
		}
	}

	/**
	 * Constructor
	 */
	public ReminderGui(Reminder inParent) {
		_parent = inParent;
		// Make a GUI Frame to show the results
		_mainFrame = new JFrame("Oops Forgot");
		_mainFrame.add(makeContents(), BorderLayout.CENTER);
		_mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Add a window listener to close it
		_mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				boolean shouldExit = true;
				if (_parent.needToSave())
				{
					int confirm = JOptionPane.showConfirmDialog(
						_mainFrame, "Do you want to save your changes to the birthday file?",
						"Birthday information changed", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
					if (confirm == JOptionPane.YES_OPTION && !_parent.saveAll()) {
						System.err.println("Could not save birthday file!");
						shouldExit = false;
					}
					else if (confirm == JOptionPane.CANCEL_OPTION || confirm == JOptionPane.CLOSED_OPTION) {
						shouldExit = false;
					}
				}
				if (shouldExit) {
					System.exit(0);
				}
			}
		});
	}

	public void launch()
	{
		updateDailyInformation(getToday());
		_mainFrame.pack();
		_mainFrame.setVisible(true);
		sortPeople();
		_monthDropdown.setSelectedIndex(getToday().getMonthNum() - 1);
		_horizSplitPane.setDividerLocation(0.7);
		if (_parent.getBirthdays().isEmpty()) {
			_detailsPanel.showEmptyMessage();
		}
		new Thread(this::dailyRefresh).start();
	}

	public void showFileNotFoundWarning(File dataFile) {
		JOptionPane.showMessageDialog(_mainFrame, "Specified file can not be read: '" + dataFile.getAbsolutePath() + "'",
			"File not readable", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Make the contents of the GUI Frame
	 */
	private Container makeContents()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(10, 10));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		_todayDescLabel = new JLabel("Today is ...");
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		labelPanel.add(_todayDescLabel);
		bottomPanel.add(labelPanel, BorderLayout.WEST);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		JButton addButton = new JButton("Add birthday");
		addButton.addActionListener(e -> startAdd());
		buttonPanel.add(addButton);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		_warningsPanel = new JPanel();
		_warningsPanel.setLayout(new BoxLayout(_warningsPanel, BoxLayout.X_AXIS));
		_upcomingPanel = new WarningPanel();
		_upcomingPanel.setTitle("Upcoming birthdays");
		_warningsPanel.add(_upcomingPanel);
		_recentPanel = new WarningPanel();
		_recentPanel.setTitle("Recent birthdays");
		_warningsPanel.add(_recentPanel);
		_warningsPanel.add(Box.createHorizontalGlue());
		mainPanel.add(_warningsPanel, BorderLayout.NORTH);
		_warningsPanel.setVisible(_upcomingPanel.hasWarnings() || _recentPanel.hasWarnings());

		// In the middle-left pane, add the tab control
		JTabbedPane tabs = new JTabbedPane();
		// people pane
		JPanel peoplePanel = new JPanel();
		peoplePanel.setLayout(new BorderLayout());
		JPanel sortPanel = new JPanel();
		sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.Y_AXIS));
		sortPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JRadioButton firstnameSort = new JRadioButton("Sort by first name");
		firstnameSort.setSelected(true);
		JRadioButton lastnameSort = new JRadioButton("Sort by last name");
		ButtonGroup sortGroup = new ButtonGroup();
		sortGroup.add(firstnameSort); sortGroup.add(lastnameSort);
		sortPanel.add(firstnameSort); sortPanel.add(lastnameSort);
		peoplePanel.add(sortPanel, BorderLayout.WEST);
		firstnameSort.addActionListener(new SortListener(true));
		lastnameSort.addActionListener(new SortListener(false));
		// people list
		_modelForNames = new BirthdayListModel();
		_peopleListBox = new JList<>(_modelForNames);
		_peopleListBox.addListSelectionListener(e -> _detailsPanel.setBirthday(_modelForNames.getElementAt(_peopleListBox.getSelectedIndex())));
		peoplePanel.add(new JScrollPane(_peopleListBox), BorderLayout.CENTER);
		tabs.addTab("People", null, peoplePanel, "Lists birthdays according to each person's name");
		// months
		JPanel monthsPanel = new JPanel();
		monthsPanel.setLayout(new BorderLayout());
		String[] monthNames = {"January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December"};
		_monthDropdown = new JComboBox<>(monthNames);
		_monthDropdown.addActionListener(actionEvent -> refreshMonthList());
		JPanel padPanel = new JPanel();
		padPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		padPanel.add(_monthDropdown);
		monthsPanel.add(padPanel, BorderLayout.WEST);
		_modelForMonths = new BirthdayListModel();
		_monthListBox = new JList<>(_modelForMonths);
		_monthListBox.addListSelectionListener(listSelectionEvent -> _detailsPanel.setBirthday(_modelForMonths.getElementAt(_monthListBox.getSelectedIndex())));
		monthsPanel.add(new JScrollPane(_monthListBox), BorderLayout.CENTER);
		tabs.addTab("Months", null, monthsPanel, "Lists birthdays by month");
		tabs.addChangeListener(changeEvent -> {_peopleListBox.clearSelection(); _monthListBox.clearSelection();});

		_detailsPanel = new DetailsPanel(this);
		_horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, _detailsPanel);
		mainPanel.add(_horizSplitPane, BorderLayout.CENTER);

		return mainPanel;
	}

	private void sortPeople(boolean inSortFirst)
	{
		if (inSortFirst)
			_sorter = BirthdaySorter.SORT_BY_FIRST_NAME;
		else
			_sorter = BirthdaySorter.SORT_BY_LAST_NAME;
		sortPeople();
		_peopleListBox.clearSelection();
	}

	private void sortPeople()
	{
		_modelForNames.clear();
		for (Birthday b : _parent.getBirthdays()) {
			if (b.isActive()) {
				_modelForNames.addBirthday(b);
			}
		}
		_modelForNames.sort(_sorter);
	}

	void refreshMonthList()
	{
		final int monthNum = _monthDropdown.getSelectedIndex() + 1;
		_modelForMonths.clear();
		for (Birthday b : _parent.getBirthdays()) {
			if (b.getDate().getMonthNum() == monthNum && b.isActive()) {
				_modelForMonths.addBirthday(b);
			}
		}
		_modelForMonths.sort(BirthdaySorter.SORT_BY_DATE);
		_monthListBox.clearSelection();
	}

	public void startAdd() {
		_addEditDialog = new AddDialog(_mainFrame, actionEvent -> finishAdd());
		_addEditDialog.initAndShow(getToday());
	}

	private static Date getToday() {
		Calendar calToday = Calendar.getInstance();
		return new Date(calToday.get(Calendar.DATE), calToday.get(Calendar.MONTH) + 1, calToday.get(Calendar.YEAR));
	}

	@Override
	public void startDelete(Birthday b) {
		if (b != null
			&& JOptionPane.showConfirmDialog(_mainFrame, "Delete '" + b.getName() + "' ?", "Delete?",
			JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			_parent.finishDelete(b);
			refreshAll();
		}
	}

	@Override
	public void startEdit(Birthday b) {
		EditDialog edit = new EditDialog(_mainFrame, actionEvent -> finishEdit(b));
		_addEditDialog = edit;
		edit.initAndShow(b);
	}

	private void finishAdd() {
		Birthday added = (_addEditDialog == null ? null : _addEditDialog.getResults());
		if (added == null || !added.isOk()) {return;}
		// Check if adding this would create a (near-)dupe
		List<Birthday> dupes = _parent.getDupeListForAdd(added);
		final boolean isPlural = dupes.size() > 1;
		final String message = (isPlural ? "Entries already exist for " : "Entry already exists for ")
			+ describeList(dupes) + "\nAdd anyway?";
		if (dupes.isEmpty()
			|| JOptionPane.showConfirmDialog(_mainFrame, message, "Add", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			_parent.finishAdd(added);
			refreshAll();
		}
	}

	private String describeList(List<Birthday> dupes) {
		StringBuilder builder = new StringBuilder();
		for (Birthday b : dupes) {
			if (builder.length() > 0) {builder.append(", ");}
			builder.append(b.getName());
		}
		return builder.toString();
	}

	private void finishEdit(Birthday b) {
		Birthday edit = (_addEditDialog == null ? null : _addEditDialog.getResults());
		if (edit == null || !edit.isOk()) {return;}
		// Check if this edit would create a (near-)dupe
		Birthday originalBirthday = (_addEditDialog instanceof EditDialog ? ((EditDialog) _addEditDialog).getOriginalBirthday() : null);
		if (edit.equals(originalBirthday)) {return;}
		List<Birthday> dupes = _parent.getDupeListForEdit(edit, originalBirthday);
		final boolean isPlural = dupes.size() > 1;
		final String message = (isPlural ? "Entries already exist for " : "Entry already exists for ")
			+ describeList(dupes) + "\nEdit anyway?";
		if (dupes.isEmpty()
			|| JOptionPane.showConfirmDialog(_mainFrame, message, "Edit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			_parent.finishEdit(b, edit);
			refreshAll();
		}
	}

	private void refreshAll() {
		sortPeople();
		refreshMonthList();
		_detailsPanel.refresh();
	}

	private void dailyRefresh() {
		while (true) {
			try {
				Thread.sleep(getMillisUntilMidnight());
			} catch (InterruptedException ie) {
				System.out.println("Interrupted exception thrown: " + ie.getMessage());
			}
			updateDailyInformation(getToday());
		}
	}

	private void updateDailyInformation(Date inToday) {
		// System.out.println("Updating information for: " + inToday.getDescription());
		_todayDescLabel.setText("Today is " + inToday.getDescription());
		_upcomingPanel.setWarnings(_parent.getUpcomingWarnings(), inToday);
		_recentPanel.setWarnings(_parent.getRecentWarnings(), inToday);
		_warningsPanel.setVisible(_upcomingPanel.hasWarnings() || _recentPanel.hasWarnings());
	}

	private long getMillisUntilMidnight() {
		Calendar calNow = Calendar.getInstance();
		final long hourNow = calNow.get(Calendar.HOUR_OF_DAY);
		final long minNow = calNow.get(Calendar.MINUTE);
		final long secNow = calNow.get(Calendar.SECOND);
		return (((23 - hourNow) * 60L + (59 - minNow)) * 60L + (60 - secNow)) * 1000L;
	}
}
