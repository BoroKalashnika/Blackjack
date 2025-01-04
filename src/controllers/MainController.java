package controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import gui.Login;
import gui.Main;
import models.Blackjack;
import models.MongoDBSingleton;
import models.Utils;

/**
 * MainController is responsible for handling the logic and user interactions of
 * the main application. It connects the GUI elements with the backend models,
 * such as user login, registration, game start, score saving, and more.
 */
public class MainController {
	private Main main;
	private ActionListener actionListener_login, actionListener_register, actionListener_loadCards,
			actionListener_logOut, actionListener_start, actionListener_save, actionListener_hallOfFame,
			actionListener_newCard, actionListener_stand;
	private MongoDBSingleton mongoDBSingleton;
	private Blackjack human;
	private Blackjack ai;
	private static MongoCollection<Document> usersCollection;

	/**
	 * Constructor to initialize the MainController with the main GUI and database
	 * singleton.
	 *
	 * @param main             Main GUI instance.
	 * @param mongoDBSingleton Singleton instance for MongoDB operations.
	 */
	public MainController(Main main, MongoDBSingleton mongoDBSingleton) {
		this.main = main;
		this.mongoDBSingleton = mongoDBSingleton;
		controller();
	}

	/**
	 * Sets up the controllers and action listeners for various buttons and user
	 * interactions.
	 */
	private void controller() {
		main.setVisible(true);

		actionListener_loadCards = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mongoDBSingleton.loadCardCollections();
			}
		};

		actionListener_login = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				main.setEnabled(false);
				ActionListener actionListener_ok, actionListener_cancel;

				Login login = new Login("Login");
				login.setVisible(true);

				actionListener_ok = new ActionListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void actionPerformed(ActionEvent e) {
						String user = login.getTxtUsuari().getText();
						String pwd = Utils.hashPassword(login.getPwdContrasenya().getText());

						if (!pwd.isEmpty() && !user.isEmpty()) {
							if (mongoDBSingleton.loginUser(user, pwd)) {
								try {
									JSONObject jsonDocument = new JSONObject();
									jsonDocument.put("username", user);
									jsonDocument.put("password", pwd);

									FileWriter writer = new FileWriter("./config_local.json");
									writer.write(jsonDocument.toString(4));
									writer.close();
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", 0);
								}
								main.getBtnLogin().setBackground(Color.green);
							}
						} else {
							JOptionPane.showMessageDialog(null, "You didn't write your username or password", "Error",
									0);
						}
						login.setVisible(false);
						main.setEnabled(true);
					}
				};

				actionListener_cancel = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						login.setVisible(false);
						main.setEnabled(true);
					}
				};

				login.getBtnOk().addActionListener(actionListener_ok);
				login.getBtnCancel().addActionListener(actionListener_cancel);
			}
		};

		actionListener_register = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				main.setEnabled(false);
				ActionListener actionListener_ok, actionListener_cancel;

				Login register = new Login("Register");
				register.setVisible(true);

				actionListener_ok = new ActionListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void actionPerformed(ActionEvent e) {
						if (!register.getTxtUsuari().getText().isEmpty()
								&& !register.getPwdContrasenya().getText().isEmpty()
								&& !register.getPwdRepeat().getText().isEmpty()) {
							if (register.getPwdContrasenya().getText().equals(register.getPwdRepeat().getText())) {
								mongoDBSingleton.registerUser(register.getTxtUsuari().getText(),
										Utils.hashPassword(register.getPwdContrasenya().getText()));
							} else {
								JOptionPane.showMessageDialog(null, "Passwords don't match", "Error", 0);
							}
						} else {
							JOptionPane.showMessageDialog(null, "You didn't write your username or password", "Error",
									0);
						}
						register.setVisible(false);
						main.setEnabled(true);
					}
				};

				actionListener_cancel = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						register.setVisible(false);
						main.setEnabled(true);
					}
				};

				register.getBtnOk().addActionListener(actionListener_ok);
				register.getBtnCancel().addActionListener(actionListener_cancel);
			}
		};

		actionListener_logOut = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				main.getBtnLogin().setBackground(UIManager.getColor("Button.background"));
				try {
					JSONObject jsonDocument = new JSONObject();
					jsonDocument.put("username", "");
					jsonDocument.put("password", "");

					FileWriter writer = new FileWriter("./config_local.json");
					writer.write(jsonDocument.toString(4));
					writer.close();
					clear();
					stopPlaying();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", 0);
				}
			}
		};

		actionListener_start = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Utils.getUsername().isEmpty()) {
					startGame();
				} else {
					JOptionPane.showMessageDialog(null, "Login to play", "INFO", 1);
				}
			}

		};

		actionListener_save = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Utils.getUsername().isEmpty()) {
					if (!main.getBtnNewCard().isEnabled()) {
						mongoDBSingleton.saveResult(human.getPoints(),
								main.getCmbIidioma().getSelectedItem().toString());
					} else {
						JOptionPane.showMessageDialog(null, "Game is not over yet!", "INFO", 1);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Login to play", "INFO", 1);
				}
			}
		};

		actionListener_hallOfFame = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Utils.getUsername().isEmpty()) {
					JTextArea textArea = new JTextArea(35, 35);
					textArea.setLineWrap(true);
					textArea.setWrapStyleWord(true);
					textArea.setEditable(false);
					textArea.setText("SCORES \n");

					MongoCursor<Document> cursor = mongoDBSingleton.getScores().find().iterator();
					while (cursor.hasNext()) {
						try {
							JSONObject config = new JSONObject(new JSONTokener(cursor.next().toJson()));
							String name = config.getString("name");
							int points = config.getInt("points");
							String suit = config.getString("suit");
							String date = config.getString("date");

							textArea.setText(textArea.getText() + "\n" + name + " " + points + " points (Suit " + suit
									+ ", " + date + ")");
						} catch (JSONException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", 0);
						}
					}

					JScrollPane scrollPane = new JScrollPane(textArea);

					JOptionPane.showMessageDialog(null, scrollPane, "Blackjack Hall of Fame",
							JOptionPane.PLAIN_MESSAGE);

				} else {
					JOptionPane.showMessageDialog(null, "Login to play", "INFO", 1);
				}
			}
		};

		actionListener_newCard = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (usersCollection != null) {
					drawCard(human);
					if (main.getBtnNewCard().isEnabled() && main.getBtnStand().isEnabled()) {
						drawCard(ai);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Start the game first");
				}
			}
		};

		actionListener_stand = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (usersCollection != null) {
					drawCard(ai);
				} else {
					JOptionPane.showMessageDialog(null, "Start the game first");
				}
			}
		};

		main.getBtnLogin().addActionListener(actionListener_login);
		main.getBtnRegister().addActionListener(actionListener_register);
		main.getBtnLoadCards().addActionListener(actionListener_loadCards);
		main.getBtnLogout().addActionListener(actionListener_logOut);
		main.getBtnStrart().addActionListener(actionListener_start);
		main.getBtnSave().addActionListener(actionListener_save);
		main.getBtnHallOfFame().addActionListener(actionListener_hallOfFame);
		main.getBtnNewCard().addActionListener(actionListener_newCard);
		main.getBtnStand().addActionListener(actionListener_stand);
	}

	/**
	 * Draws a card for the specified player and updates the UI accordingly.
	 *
	 * @param player The player (human or AI) drawing the card.
	 */
	private void drawCard(Blackjack player) {
		if (main.getBtnNewCard().isEnabled()) {
			if (player.getHuman()) {
				main.getImgPlayer().setIcon(Utils.convertBase64ToPng(player.getCard().getString("image"),
						main.getImgPlayer().getBounds().height));
				if (human.getPoints() == 21) {
					JOptionPane.showMessageDialog(null, "Game over. The winner is User");
					stopPlaying();
				} else if (human.getPoints() > 21) {
					JOptionPane.showMessageDialog(null, "Game over. The winner is Crupier");
					stopPlaying();
				} else {
					JOptionPane.showMessageDialog(null, "Crupier's turn");
				}
				main.getLblTotalScoreHuman().setText("TOTOAL SCORE: " + human.getPoints());
				main.getLblScoreHistoryHuman()
						.setText(main.getLblScoreHistoryHuman().getText() + " " + human.getCurrentCardValue());
			} else {
				if (ai.getPoints() > 16 && ai.getPoints() < 21) {
					JOptionPane.showMessageDialog(null, "Crupier stands");
					if (human.getPoints() > ai.getPoints()) {
						main.getBtnNewCard().setEnabled(false);
					}
				} else {
					main.getImgCrupier().setIcon(Utils.convertBase64ToPng(player.getCard().getString("image"),
							main.getImgPlayer().getBounds().height));
					if (ai.getPoints() == 21) {
						JOptionPane.showMessageDialog(null, "Game over. The winner is Crupier");
						stopPlaying();
					} else if (ai.getPoints() > 21) {
						JOptionPane.showMessageDialog(null, "Game over. The winner is User");
						stopPlaying();
					} else {
						JOptionPane.showMessageDialog(null, "User's turn");
					}
					main.getLblTotalScoreAi().setText("TOTOAL SCORE: " + ai.getPoints());
					main.getLblScoreHistoryAi()
							.setText(main.getLblScoreHistoryAi().getText() + " " + ai.getCurrentCardValue());
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Crupier stands");
			JOptionPane.showMessageDialog(null, "Game over. The winner is User");
			stopPlaying();
		}
	}

	/**
	 * Disables the game buttons to stop gameplay.
	 */
	private void stopPlaying() {
		main.getBtnNewCard().setEnabled(false);
		main.getBtnStand().setEnabled(false);
	}

	/**
	 * Starts the Blackjack game by initializing players and setting up the deck.
	 */
	private void startGame() {
		if (main.getCmbIidioma().getSelectedItem() == "ES")
			usersCollection = mongoDBSingleton.getCardsEsCollection();
		else
			usersCollection = mongoDBSingleton.getCardsFrCollection();
		human = new Blackjack(usersCollection, true);
		ai = new Blackjack(usersCollection, false);
		human.randomizeUsersCollection();
		clear();

		Object[] options = { "Crupier (A. I.)", "User (human)" };

		int choice = JOptionPane.showOptionDialog(null, "", "Who starts?:", 0, -1, null, options, options[0]);
		if (choice == 0) {
			JOptionPane.showMessageDialog(null, "Crupier starts");
			drawCard(ai);
		} else if (choice == 1) {
			JOptionPane.showMessageDialog(null, "User starts");
		}
	}

	/**
	 * Resets the game UI components to their default states.
	 */
	private void clear() {
		main.getBtnNewCard().setEnabled(true);
		main.getBtnStand().setEnabled(true);
		main.getLblTotalScoreAi().setText("TOTOAL SCORE:");
		main.getLblScoreHistoryAi().setText("Score history:");
		main.getLblTotalScoreHuman().setText("TOTOAL SCORE:");
		main.getLblScoreHistoryHuman().setText("Score history:");
		main.getImgCrupier().setIcon(null);
		main.getImgPlayer().setIcon(null);

	}
}
