package pubsub.io.java.example;

import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.json.JSONException;
import org.json.JSONObject;

import pubsub.io.java.Pubsub;
import pubsub.io.java.PubsubListener;

public class Main extends JFrame implements ActionListener, ChangeListener,
		PubsubListener {

	private JPanel contentPane;

	private Pubsub mPubsub;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setResizable(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		mPubsub = new Pubsub();
		mPubsub.addPubsubListener(this);
		mPubsub.connect("java");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 310, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel panel = new JPanel();

		JLabel lblNewLabel_1 = new JLabel("Send:");

		JLabel lblNewLabel = new JLabel("Recieved:");

		JTextArea textArea = new JTextArea();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane.createSequentialGroup()
										.addComponent(lblNewLabel_1)
										.addContainerGap(214, Short.MAX_VALUE))
						.addGroup(
								gl_contentPane.createSequentialGroup()
										.addComponent(lblNewLabel)
										.addContainerGap())
						.addGroup(
								Alignment.TRAILING,
								gl_contentPane
										.createSequentialGroup()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																textArea,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																282,
																Short.MAX_VALUE)
														.addComponent(
																panel,
																GroupLayout.DEFAULT_SIZE,
																282,
																Short.MAX_VALUE))
										.addGap(16)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addComponent(lblNewLabel_1)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 113,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblNewLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 180,
								Short.MAX_VALUE).addContainerGap()));

		JButton btnNewButton = new JButton("Button 1");
		btnNewButton.addActionListener(this);

		JButton btnButton = new JButton("Button 2");
		btnButton.addActionListener(this);

		JSlider slider = new JSlider();
		slider.addChangeListener(this);

		JComboBox comboBox = new JComboBox();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addComponent(btnNewButton,
										GroupLayout.PREFERRED_SIZE, 138,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, 8,
										Short.MAX_VALUE)
								.addComponent(btnButton,
										GroupLayout.PREFERRED_SIZE, 138,
										GroupLayout.PREFERRED_SIZE))
				.addComponent(slider, GroupLayout.DEFAULT_SIZE, 284,
						Short.MAX_VALUE)
				.addComponent(comboBox, Alignment.TRAILING, 0, 298,
						Short.MAX_VALUE));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
				Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(btnNewButton)
												.addComponent(btnButton))
								.addGap(18)
								.addComponent(slider,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(18)
								.addComponent(comboBox,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(39, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Button 1")) {
			JSONObject doc = new JSONObject();
			try {
				doc.put("value", "button1");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			mPubsub.publish(doc);
		} else if (e.getActionCommand().equals("Button 2")) {
			JSONObject doc = new JSONObject();
			try {
				doc.put("value", "button2");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			mPubsub.publish(doc);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSONObject doc = new JSONObject();
		try {
			doc.put("value", ((JSlider) e.getSource()).getValue());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		mPubsub.publish(doc);
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(JSONObject arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(int arg0, JSONObject arg1) {
		System.out.println(arg1);
	}

	@Override
	public void onOpen() {
		// TODO Auto-generated method stub

	}
}
