package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField pwdContrasenya;
	private JTextField txtUsuari;
	private JPasswordField pwdRepeat;
	private JButton btnOk;
	private JButton btnCancel;

	public JPasswordField getPwdContrasenya() {
		return pwdContrasenya;
	}

	public JTextField getTxtUsuari() {
		return txtUsuari;
	}

	public JPasswordField getPwdRepeat() {
		return pwdRepeat;
	}

	public JButton getBtnOk() {
		return btnOk;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}

	public Login(String title) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 495, 311);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		pwdContrasenya = new JPasswordField();
		pwdContrasenya.setFont(new Font("Tahoma", Font.PLAIN, 18));
		pwdContrasenya.setBounds(155, 90, 248, 39);
		contentPane.add(pwdContrasenya);

		txtUsuari = new JTextField();
		txtUsuari.setFont(new Font("Tahoma", Font.PLAIN, 18));
		txtUsuari.setBounds(155, 31, 248, 39);
		contentPane.add(txtUsuari);
		txtUsuari.setColumns(10);

		JLabel lblUsuari = new JLabel("User:");
		lblUsuari.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblUsuari.setBounds(10, 31, 97, 39);
		contentPane.add(lblUsuari);

		btnOk = new JButton("OK");
		btnOk.setBounds(135, 222, 89, 39);
		contentPane.add(btnOk);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(239, 222, 89, 39);
		contentPane.add(btnCancel);

		JLabel lblContrasenya = new JLabel("Password:");
		lblContrasenya.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblContrasenya.setBounds(10, 90, 143, 39);
		contentPane.add(lblContrasenya);

		if (title.equals("Register")) {
			JLabel lblRepeat = new JLabel("Repeat password:");
			lblRepeat.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblRepeat.setBounds(10, 154, 143, 39);
			contentPane.add(lblRepeat);

			pwdRepeat = new JPasswordField();
			pwdRepeat.setFont(new Font("Tahoma", Font.PLAIN, 18));
			pwdRepeat.setBounds(155, 154, 248, 39);
			contentPane.add(pwdRepeat);
		} else {
			btnOk.setBounds(135, 154, 89, 39);
			btnCancel.setBounds(239, 154, 89, 39);
		}

	}
}
