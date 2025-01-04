package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.DefaultComboBoxModel;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnLoadCards;
	private JButton btnRegister;
	private JButton btnLogin;
	private JButton btnStrart;
	private JButton btnSave;
	private JButton btnHallOfFame;
	private JButton btnLogout;
	@SuppressWarnings("rawtypes")
	private JComboBox cmbIidioma;
	private JLabel lblNewLabel;
	private JButton imgCrupier;
	private JButton imgPlayer;
	private JButton btnNewCard;
	private JButton btnStand;
	private JLabel lblTotalScoreAi;
	private JLabel lblScoreHistoryAi;
	private JLabel lblTotalScoreHuman;
	private JLabel lblScoreHistoryHuman;

	public JPanel getContentPane() {
		return contentPane;
	}

	public JButton getBtnLoadCards() {
		return btnLoadCards;
	}

	public JButton getBtnRegister() {
		return btnRegister;
	}

	public JButton getBtnLogin() {
		return btnLogin;
	}

	public JButton getBtnStrart() {
		return btnStrart;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	public JButton getBtnHallOfFame() {
		return btnHallOfFame;
	}

	public JButton getBtnLogout() {
		return btnLogout;
	}

	@SuppressWarnings("rawtypes")
	public JComboBox getCmbIidioma() {
		return cmbIidioma;
	}

	public JLabel getLblNewLabel() {
		return lblNewLabel;
	}

	public JButton getImgCrupier() {
		return imgCrupier;
	}

	public JButton getImgPlayer() {
		return imgPlayer;
	}

	public JButton getBtnNewCard() {
		return btnNewCard;
	}

	public JButton getBtnStand() {
		return btnStand;
	}

	public JLabel getLblTotalScoreAi() {
		return lblTotalScoreAi;
	}

	public JLabel getLblScoreHistoryAi() {
		return lblScoreHistoryAi;
	}

	public JLabel getLblTotalScoreHuman() {
		return lblTotalScoreHuman;
	}

	public JLabel getLblScoreHistoryHuman() {
		return lblScoreHistoryHuman;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Main() {
		setTitle("Blackjack");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1190, 760);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnLoadCards = new JButton("Load cards");
		btnLoadCards.setBounds(26, 11, 135, 28);
		contentPane.add(btnLoadCards);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(171, 11, 135, 28);
		contentPane.add(btnRegister);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(320, 11, 135, 28);
		contentPane.add(btnLogin);

		btnStrart = new JButton("Strart");
		btnStrart.setBounds(591, 11, 135, 28);
		contentPane.add(btnStrart);

		btnSave = new JButton("Save");
		btnSave.setBounds(734, 11, 135, 28);
		contentPane.add(btnSave);

		btnHallOfFame = new JButton("Hall of fame");
		btnHallOfFame.setBounds(881, 11, 135, 28);
		contentPane.add(btnHallOfFame);

		btnLogout = new JButton("Logout");
		btnLogout.setBounds(1025, 11, 135, 28);
		contentPane.add(btnLogout);

		cmbIidioma = new JComboBox();
		cmbIidioma.setModel(new DefaultComboBoxModel(new String[] { "ES", "FR" }));
		cmbIidioma.setBounds(529, 14, 52, 22);
		contentPane.add(cmbIidioma);

		lblNewLabel = new JLabel("Cards suit:");
		lblNewLabel.setBounds(464, 11, 76, 28);
		contentPane.add(lblNewLabel);

		imgCrupier = new JButton("");
		imgCrupier.setBounds(42, 91, 350, 508);
		imgCrupier.setBorderPainted(false);
		imgCrupier.setFocusPainted(false);
		imgCrupier.setContentAreaFilled(false);
		contentPane.add(imgCrupier);

		imgPlayer = new JButton("");
		imgPlayer.setBounds(688, 91, 350, 508);
		imgPlayer.setBorderPainted(false);    
		imgPlayer.setFocusPainted(false);     
		imgPlayer.setContentAreaFilled(false);
		contentPane.add(imgPlayer);

		lblTotalScoreAi = new JLabel("TOTOAL SCORE:");
		lblTotalScoreAi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTotalScoreAi.setBounds(52, 614, 340, 22);
		contentPane.add(lblTotalScoreAi);

		lblTotalScoreHuman = new JLabel("TOTOAL SCORE:");
		lblTotalScoreHuman.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTotalScoreHuman.setBounds(698, 610, 340, 22);
		contentPane.add(lblTotalScoreHuman);

		lblScoreHistoryAi = new JLabel("Score history:");
		lblScoreHistoryAi.setBounds(51, 634, 341, 28);
		contentPane.add(lblScoreHistoryAi);

		lblScoreHistoryHuman = new JLabel("Score history:");
		lblScoreHistoryHuman.setBounds(697, 634, 341, 28);
		contentPane.add(lblScoreHistoryHuman);

		btnNewCard = new JButton("New card");
		btnNewCard.setBounds(698, 673, 188, 28);
		contentPane.add(btnNewCard);

		btnStand = new JButton("Stand");
		btnStand.setBounds(896, 673, 188, 28);
		contentPane.add(btnStand);

	}
}
