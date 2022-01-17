package tim.oops;

import java.io.*;
import java.util.*;

/**
 * Main class for the Birthday Reminder Tool
 */
public class Forgot implements Reminder
{
	public int _advanceWarning = 21;
	public int _missedWarning = -7;
	private File _dataFile = getDefaultDataFile();
	private boolean _justCheck = false;
	private final ArrayList<Birthday> _birthdays = new ArrayList<>();
	private ArrayList<BirthdayWarning> _upcomingWarningList = null;
	private ArrayList<BirthdayWarning> _recentWarningList = null;
	private Date _lastCalculationDate = null;


	/**
	 * Main method to launch program
	 */
	public static void main(String[] args) {
		Forgot tool = new Forgot();
		try {
			tool.processOptions(args);
		}
		catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			showHelp();
			throw e;
		}
		tool.readFile();
		tool.calculateWarnings();
		tool.showResults();
	}

	private void calculateWarnings() {
		Calendar calToday = Calendar.getInstance();
		Date today = new Date(calToday.get(Calendar.DATE), calToday.get(Calendar.MONTH) + 1, calToday.get(Calendar.YEAR));
		if (today.equals(_lastCalculationDate)) {return;}
		_upcomingWarningList = new ArrayList<>();
		_recentWarningList = new ArrayList<>();
		for (Birthday b : _birthdays) {
			final int numDays = today.getDaysUntil(b.getDate());
			if (numDays <= _advanceWarning && numDays >= 0) {
				_upcomingWarningList.add(new BirthdayWarning(b, numDays));
			} else if (numDays < 0 && numDays > _missedWarning) {
				_recentWarningList.add(new BirthdayWarning(b, numDays));
			}
		}
		Collections.sort(_upcomingWarningList);
		Collections.sort(_recentWarningList);
		_lastCalculationDate = today;
	}

	private void showResults()
	{
		if (!_upcomingWarningList.isEmpty() || !_recentWarningList.isEmpty() || !_justCheck) {
			launchGui();
		}
	}

	private void launchGui() {
		if (!_upcomingWarningList.isEmpty() || !_recentWarningList.isEmpty() || !_justCheck)
		{
			// Make a GUI Frame to show the results
			ReminderGui gui = new ReminderGui(this);
			gui.launch();
			if (!_dataFile.exists() || !_dataFile.canRead()) {
				gui.showFileNotFoundWarning(_dataFile);
			}
		}
	}

	/**
	 * Read the specified file and check it
	 */
	private void readFile()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(_dataFile));
			String s;
			do
			{
				s = reader.readLine();
				Birthday b = BirthdayFile.parse(s);
				if (b != null && b.isOk()) {
					_birthdays.add(b);
				}
			}
			while (s != null);
		}
		catch (Exception e) {
			System.out.println("oops: " + e.getMessage());
		}
	}


	/**
	 * Process command line options, to set behaviour flags
	 */
	private void processOptions(String[] inOptions)
	{
		// exit if no options given
		if (inOptions == null || inOptions.length < 1) return;
		boolean takeForwardDays = false;
		boolean takeBackwardDays = false;
		boolean takeFilename = false;

		// loop through array
		for (String option : inOptions) {
			if (option != null) {
				if (takeForwardDays) {
					int dayParam = 0;
					try {
						dayParam = Integer.parseInt(option);
					} catch (NumberFormatException ignored) {
					}
					if (dayParam <= 0)
						throw new IllegalArgumentException("Invalid number of days: " + option);
					_advanceWarning = dayParam;
					takeForwardDays = false;
				} else if (takeBackwardDays) {
					int dayParam = 0;
					try {
						dayParam = Integer.parseInt(option);
					} catch (NumberFormatException ignored) {}
					if (dayParam > 0) dayParam = -dayParam;
					if (dayParam == 0)
						throw new IllegalArgumentException("Invalid number of days: " + option);
					_missedWarning = dayParam;
					takeBackwardDays = false;
				} else if (takeFilename) {
					_dataFile = new File(option);
					takeFilename = false;
				}
				else
				{
					if (option.equals("-c") || option.equals("--check"))
						_justCheck = true;
					else if (option.equals("-f") || option.equals("--forward"))
						takeForwardDays = true;
					else if (option.equals("-b") || option.equals("--backward"))
						takeBackwardDays = true;
					else if (option.equals("-?") || option.equals("-h") || option.equals("--help"))
						showHelp();
					else if (option.equals("--file"))
						takeFilename = true;
					else
						throw new IllegalArgumentException("Unrecognised parameter: " + option);
				}
			}
		}
		if (takeFilename || takeBackwardDays || takeForwardDays) {
			throw new IllegalArgumentException("Expected parameter for " + (takeFilename ? "filename":"number of days"));
		}
	}


	/**
	 * Show help information about command line parameters
	 */
	private static void showHelp()
	{
		System.out.println("OopsForgot - a birthday reminder tool\n-------------------------------------");
		System.out.println("version: 2022-01-17");
		System.out.println("Options:");
		System.out.println("  -c --check           Only check for birthdays, don't show GUI if none found");
		System.out.println("  -f --forward <days>  Specify the number of days forward to check");
		System.out.println("  -b --backward <days> Specify the number of days in the past to check");
		System.out.println("     --file <filename> Specify birthday file to load from and save to\n");
	}

	@Override
	public List<Birthday> getBirthdays() {
		return _birthdays;
	}

	@Override
	public List<BirthdayWarning> getUpcomingWarnings() {
		calculateWarnings();
		return _upcomingWarningList;
	}

	@Override
	public List<BirthdayWarning> getRecentWarnings() {
		calculateWarnings();
		return _recentWarningList;
	}

	@Override
	public void finishAdd(Birthday b) {
		b.setAdded();
		_birthdays.add(b);
	}

	@Override
	public void finishDelete(Birthday b) {
		// TODO: Add command to stack for possible undo
		b.setDeleted();
	}

	@Override
	public void finishEdit(Birthday orig, Birthday edit) {
		if (orig.equals(edit)) {return;}
		for (Birthday b : _birthdays) {
			if (b == orig) {
				b.copyFrom(edit);
				break;
			}
		}
	}

	@Override
	public boolean needToSave() {
		for (Birthday b : _birthdays) {
			if (b.isModified()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean saveAll() {
		System.out.println("Saving to: " + _dataFile.getAbsolutePath());
		if (_dataFile.exists()) {
			if (_dataFile.isDirectory() || !_dataFile.canWrite()) {
				return false;
			}
			if (!_dataFile.renameTo(new File(_dataFile.getParent(), _dataFile.getName() + ".bak"))) {
				return false;
			}
		}
		try (FileWriter writer = new FileWriter(_dataFile)) {
			for (Birthday b : _birthdays) {
				if (b.isActive()) {
					writer.write(BirthdayFile.write(b));
					writer.write('\n');
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** @return default data file to use */
	private static File getDefaultDataFile() {
		return new File(System.getProperty("user.home"), ".oopsdata");
	}

	/**
	 * Is an edit allowed or would it clash with others
	 */
	public List<Birthday> getDupeListForEdit(Birthday inRequested, Birthday inOrig) {
		ArrayList<Birthday> foundDupes = new ArrayList<>();
		if (inRequested != null) {
			for (Birthday b : getBirthdays()) {
				// Don't match the original if it was given
				if (b != inOrig && b.matches(inRequested)) {
					foundDupes.add(b);
				}
			}
		}
		return foundDupes;
	}
}
