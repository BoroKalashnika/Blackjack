package models;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Represents a Blackjack game logic class for handling cards, points, and
 * player interaction.
 */
public class Blackjack {
	private MongoCollection<Document> usersCollection;
	private static int cardPos;
	private int points;
	private int currentCardValue;
	private boolean human;

	/**
	 * Gets the total points accumulated by the player.
	 *
	 * @return Total points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Gets the value of the most recently drawn card.
	 *
	 * @return Value of the current card
	 */
	public int getCurrentCardValue() {
		return currentCardValue;
	}

	/**
	 * Constructor to initialize a Blackjack player instance.
	 *
	 * @param usersCollection MongoDB collection of card documents
	 * @param human           Indicates whether this player is human
	 */
	public Blackjack(MongoCollection<Document> usersCollection, boolean human) {
		this.usersCollection = usersCollection;
		Blackjack.cardPos = 0;
		this.human = human;
		points = 0;
		currentCardValue = 0;
	}

	/**
	 * Checks if this player is human.
	 *
	 * @return True if the player is human, false otherwise
	 */
	public boolean getHuman() {
		return human;
	}

	/**
	 * Randomizes the order of the cards in the collection to simulate shuffling.
	 */
	public void randomizeUsersCollection() {
		List<Document> userList = new ArrayList<>();

		try (MongoCursor<Document> cursor = usersCollection.find().iterator()) {
			while (cursor.hasNext()) {
				userList.add(cursor.next());
			}
		}

		Collections.shuffle(userList);

		usersCollection.drop();
		usersCollection.insertMany(userList);
	}

	/**
	 * Draws the next card from the collection, updates points, and handles special
	 * card logic.
	 *
	 * @return The drawn card document or null if no more cards are available
	 */
	public Document getCard() {
		try (MongoCursor<Document> cursor = usersCollection.find().skip(cardPos).limit(1).iterator()) {
			if (cursor.hasNext()) {
				Document card = cursor.next();
				cardPos++;
				int point = card.getInteger("points");

				if (point == 1) {
					if (human) {
						Object[] options = { "1", "11" };

						int choice = JOptionPane.showOptionDialog(null, "", "Choose your points:", 0, -1, null, options,
								options[0]);
						if (choice == 0) {
							points++;
							currentCardValue = 1;
						} else if (choice == 1) {
							points += 11;
							currentCardValue = 11;
						} else {
							JOptionPane.showMessageDialog(null, "No selection made.");
						}
					} else {
						if (points + 11 < 22) {
							points += 11;
						} else {
							points++;
						}
					}
				} else if (point > 10) {
					points += 10;
					currentCardValue = 10;
				} else {
					points += point;
					currentCardValue = point;
				}

				return card;
			} else {
				return null;
			}
		}
	}
}
