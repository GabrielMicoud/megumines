package util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class HintTextField extends JTextField implements FocusListener {
	private final String hint;
	private boolean showingHint;
	public HintTextField(final String hint, int columns) {
		super(hint, columns);
	    this.hint = hint;
	    this.showingHint = true;
	    super.addFocusListener(this);
	}
	
	@Override
	public void focusGained(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	      super.setText("");
	      showingHint = false;
	    }
	}
	@Override
	public void focusLost(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	      super.setText(hint);
	      showingHint = true;
	    }
	}
	
	@Override
	public String getText() {
	    return showingHint ? "" : super.getText();
	}
}
