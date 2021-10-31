package display;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class HintTextField extends JTextField implements FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3273577542523350615L;
	private final String hint;
	private boolean showingHint;
	public HintTextField(final String hint, int columns, String value) {
		super(hint, columns);
	    this.hint = hint;
	    this.showingHint = true;
	    if(!value.equals("")) {
	    	showingHint = false;
	    	super.setText(value);
	    }
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
	    if(showingHint) {
	    	return ""; 
	    }
	    else {
	    	return super.getText();
	    }
	}
}
