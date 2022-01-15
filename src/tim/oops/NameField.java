package tim.oops;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * text field for holding a name
 */
public class NameField extends JTextField
{
	/** Constructor */
	public NameField()
	{
		super(16);
		getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent arg0) {fireActionPerformed();}
			public void insertUpdate(DocumentEvent arg0) {fireActionPerformed();}
			public void changedUpdate(DocumentEvent arg0) {fireActionPerformed();}
		});
	}
}
