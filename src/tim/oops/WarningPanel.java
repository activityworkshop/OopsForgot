package tim.oops;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class WarningPanel extends JPanel {
	private final JEditorPane _pane;
	private boolean _hasWarnings = false;

	public WarningPanel() {
		_pane = new JEditorPane();
		_pane.setContentType("text/html");
		_pane.setEditable(false);
		_pane.setOpaque(false);
	}

	public void setTitle(String inTitle) {
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), inTitle));
		add(_pane);
	}

	public void setWarnings(List<BirthdayWarning> inWarnings, Date inToday) {
		_hasWarnings = (inWarnings != null && !inWarnings.isEmpty());
		if (_hasWarnings) {
			StringBuilder b = new StringBuilder("<html><ul>");
			for (BirthdayWarning bw : inWarnings) {
				b.append("<li>");
				if (bw.isUrgent()) {
					b.append("<span style='background:orange'>");
				}
				b.append(bw.getWarning(inToday));
				if (bw.isUrgent()) {
					b.append("</span>");
				}
			}
			b.append("</ul></html>");
			_pane.setText(b.toString());
		}
		else {
			_pane.setText("<html><i>None</i></html>");
		}
	}

	public Dimension getPreferredSize() {
		Dimension original = super.getPreferredSize();
		TitledBorder titleBorder = (TitledBorder) getBorder();
		int width = (int) Math.max(original.getWidth(), 60 + (int) titleBorder.getMinimumSize(this).getWidth());
		return new Dimension(width, (int) original.getHeight());
	}

	public boolean hasWarnings() {
		return _hasWarnings;
	}
}
