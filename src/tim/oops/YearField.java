package tim.oops;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * text field for holding a single integer with validation
 */
public class YearField extends JTextField
{
	/** Num digits to allow */
	private static final int maxDigits = 4;

	/**
	 * Inner class to act as document for validation
	 */
	protected static class WholeNumberDocument extends PlainDocument
	{
		/**
		 * Override the insert string method
		 * @param offs offset
		 * @param str string
		 * @param a attributes
		 * @throws BadLocationException on insert failure
		 */
		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
		{
			if (getLength() >= maxDigits) return;
			char[] source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;
			for (int i = 0; i < result.length && j < maxDigits; i++) {
				if (Character.isDigit(source[i]))
					result[j++] = source[i];
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}


	/** Constructor */
	public YearField()
	{
		super(maxDigits);
		setDocument(new WholeNumberDocument());
		getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent arg0) {fireActionPerformed();}
			public void insertUpdate(DocumentEvent arg0) {fireActionPerformed();}
			public void changedUpdate(DocumentEvent arg0) {fireActionPerformed();}
		});
	}

	public boolean hasValue() {
		return !getText().isEmpty();
	}

	/**
	 * @return integer value
	 */
	public int getValue() {
		return parseValue(getText());
	}

	/**
	 * @param inValue value to set
	 */
	public void setValue(int inValue)
	{
		setText("" + inValue);
	}

	public void clearValue()
	{
		setText("");
	}

	/**
	 * @param inText text to parse
	 * @return value as integer
	 */
	private static int parseValue(String inText)
	{
		int value = 0;
		try {
			value = Integer.parseInt(inText);
		}
		catch (NumberFormatException ignored) {}
		if (value < 0) {
			value = 0;
		}
		return value;
	}
}
