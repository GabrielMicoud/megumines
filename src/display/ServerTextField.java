package display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.Colors;
import javax.swing.JButton;

public class ServerTextField extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1646449553602747996L;
	private static final int MAX_NAME_LENGTH = 25;
	private static final String ICON = "img/megumin.png";
	private JPanel panel1;
	private JPanel panel2;
	private JLabel label;
	private JButton submit;
	private JButton cancel;
	private JTextField ip;
	private JTextField port;
	private JTextField name;
	private MainFrame frame;
	private String errorText = "Error : Can't connect to server.";
	
	private String defaultIp = "IP address";
	private String defaultPort = "PORT";
	private String defaultName = "Nickname";

	public ServerTextField (MainFrame frame) {
		setTitle("Enter your desired IP adress, PORT and nickname here.");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setIconImage(Toolkit.getDefaultToolkit().getImage(ICON));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/2, height/2);
		setLocationRelativeTo(null);
		
		//define the variables
		this.frame = frame;
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel1.setBackground(Colors.BACKGROUND.color);
		panel2.setBackground(Colors.BACKGROUND.color);
		panel2.setLayout(new FlowLayout());
		label = new JLabel();
		frame.getGame().readServerPresets();
		ip = new HintTextField(defaultIp, 20, frame.getGame().getServerPresets()[0]);
		port = new HintTextField(defaultPort, 10, frame.getGame().getServerPresets()[1]);
		name = new HintTextField(defaultName, 10, frame.getGame().getServerPresets()[2]);
		submit = new JButton("Submit");
		cancel = new JButton ("Cancel");
		
		//action listeners
		submit.addActionListener(this);
		cancel.addActionListener(this);
		
		panel1.add(ip, BorderLayout.NORTH);
		panel1.add(port, BorderLayout.NORTH);
		panel1.add(name, BorderLayout.NORTH);
		
		panel2.add(submit);
		panel2.add(cancel);
		add(label, BorderLayout.SOUTH);
		add(panel1, BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == submit) {
			String ipAdress = ip.getText();
			int p = Integer.parseInt(port.getText());
			String nickname = name.getText();
			//troncature des pseudos trop longs
			if(nickname.length() > MAX_NAME_LENGTH) {
				nickname = nickname.substring(0, MAX_NAME_LENGTH - 3) + "...";
			}
			try {
				frame.getGame().connect(ipAdress, p, nickname);
				frame.setOnlineDisplay();
				if(!ipAdress.equals(frame.getGame().getServerPresets()[0]) || !port.getText().equals(frame.getGame().getServerPresets()[1]) || !nickname.equals(frame.getGame().getServerPresets()[2])) {
					frame.getGame().setServerPresets(ipAdress, port.getText(), nickname);
					frame.getGame().writeServerPresets();
				}
				setVisible(false);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				label.setText(errorText);
				frame.getGame().offline();
				pack();
				repaint();
			}
		}
		if(e.getSource() == cancel) {
			setVisible(false);
		}
	}
}
