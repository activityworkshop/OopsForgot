package tim.oops;


/**
 * Responsible for reading from and writing to files
 */
public abstract class BirthdayFile
{
	/**
	 * Parse a line read from file
	 * @param inString line from file, hopefully with version number
	 * @return Birthday object if possible, otherwise null
	 */
	public static Birthday parse(String inString) {
		if (inString == null || inString.trim().length() < 2) {
			return null;
		}

		if (inString.charAt(1) != '\t') {
			return parse(inString, 1);
		}
		return parse(inString.substring(2), getVersion(inString.charAt(0)));
	}

	private static int getVersion(char v) {
		// This could be extended later with a-z, A-Z
		return "0123456789".indexOf(v);
	}

	private static Birthday parse(String inString, int inVersion)
	{
		// Currently only version 1 is supported
		if (inVersion != 1) {return null;}

		// Version 1 expected format: name, surname, day, month, year?, description?, separated by tabs
		String[] args = inString.split("\t");
		if (args.length < 4) {
			if (inString.indexOf('\t') < 0 && inString.indexOf(',') > 0) {
				args = inString.split(",");
			}
			if (args.length < 4) {
				return null;
			}
		}

		String firstName = args[0];
		String lastName = args[1];
		Date date;
		try
		{
			int dayNum = Integer.parseInt(args[2]);
			int monthNum = Integer.parseInt(args[3]);
			if (args.length >= 5)
			{
				int yearNum = Integer.parseInt(args[4]);
				date = new Date(dayNum, monthNum, yearNum);
			}
			else {
				date = new Date(dayNum, monthNum);
			}
		}
		catch (NumberFormatException ignored) {
			System.err.println("Failed to parse birthday: '" + inString + "'");
			// TODO: Log this error somehow?
			return null;
		}
		String description = (args.length >= 6 ? args[5] : null);
		return new Birthday(firstName, lastName, description, date);
	}

	/**
	 * Convert birthday to a versioned form for output
	 * @param b birthday object
	 * @return contents as string, or null
	 */
	public static String write(Birthday b) {
		if (b == null || !b.isOk()) {return null;}
		// Always use the current version, which is now always 1
		StringBuilder buffer = new StringBuilder();
		buffer.append('1').append('\t');
		buffer.append(b.getFirstName()).append('\t');
		buffer.append(b.getLastName()).append('\t');
		buffer.append(b.getDate().getDayNum()).append('\t');
		buffer.append(b.getDate().getMonthNum()).append('\t');
		if (b.getDate().hasYear()) {buffer.append(b.getDate().getYearNum());}
		if (!b.getDescription().isEmpty()) {
			buffer.append('\t');
			buffer.append(b.getDescription());
		}
		return buffer.toString();
	}
}
