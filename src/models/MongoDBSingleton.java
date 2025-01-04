package models;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Singleton class for managing MongoDB connections and operations. Ensures a
 * single instance of the database connection is used throughout the
 * application.
 */
public class MongoDBSingleton {

	private static MongoDBSingleton instance;
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> usersCollection;

	/**
	 * Private constructor to initialize the MongoDB connection.
	 */
	private MongoDBSingleton() {
		try {
			JSONObject config = new JSONObject(new JSONTokener(new FileReader("config_remote.json")));
			String ip = config.getString("ip");
			String dbName = config.getString("database");
			String collectionName = config.getString("collection");

			String connectionString = "mongodb://" + ip;
			mongoClient = MongoClients.create(connectionString);
			database = mongoClient.getDatabase(dbName);
			usersCollection = database.getCollection(collectionName);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
		}
	}

	/**
	 * Retrieves the single instance of the MongoDBSingleton class.
	 * 
	 * @return The singleton instance.
	 */
	public static synchronized MongoDBSingleton getInstance() {
		if (instance == null) {
			instance = new MongoDBSingleton();
		}
		return instance;
	}

	/**
	 * Retrieves the users collection.
	 * 
	 * @return The MongoDB collection for users.
	 */
	public MongoCollection<Document> getUsersCollection() {
		return usersCollection;
	}

	/**
	 * Retrieves the French cards collection.
	 * 
	 * @return The MongoDB collection for French cards.
	 */
	public MongoCollection<Document> getCardsFrCollection() {
		return database.getCollection("cards_fr");
	}

	/**
	 * Retrieves the Spanish cards collection.
	 * 
	 * @return The MongoDB collection for Spanish cards.
	 */
	public MongoCollection<Document> getCardsEsCollection() {
		return database.getCollection("cards_es");
	}

	/**
	 * Retrieves the scores collection.
	 * 
	 * @return The MongoDB collection for scores.
	 */
	public MongoCollection<Document> getScores() {
		return database.getCollection("scores");
	}

	/**
	 * Logs in a user by verifying their username and password.
	 * 
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return True if login is successful, false otherwise.
	 */
	public boolean loginUser(String username, String password) {
		Document user = usersCollection.find(Filters.eq("username", username)).first();
		if (user != null && user.getString("password").equals(password)) {
			JOptionPane.showMessageDialog(null, "Login successful!", "Info", 1);
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Failed to login, check the username and password!", "Error", 0);
			return false;
		}
	}

	/**
	 * Registers a new user.
	 * 
	 * @param username The username of the new user.
	 * @param password The password of the new user.
	 * @return True if registration is successful, false if the user already exists.
	 */
	public boolean registerUser(String username, String password) {
		if (usersCollection.find(Filters.eq("username", username)).first() != null) {
			JOptionPane.showMessageDialog(null, "User already exists!", "Error", 0);
			return false;
		}
		Document newUser = new Document("username", username).append("password", password);
		usersCollection.insertOne(newUser);
		JOptionPane.showMessageDialog(null, "User registered successfully!", "New user", 1);
		return true;
	}

	/**
	 * Loads French and Spanish card collections into the database.
	 */
	public void loadCardCollections() {
		if (loadFrenchCards() && loadSpanishCards()) {
			JOptionPane.showMessageDialog(null, "Images have been 'monged' successfully", "Info", 1);
		} else {
			JOptionPane.showMessageDialog(null, "An error occurred while 'mongging' the images", "Error", 0);
		}
	}

	/**
	 * Saves a game result to the scores collection.
	 * 
	 * @param points The points scored.
	 * @param suit   The suit associated with the result.
	 */
	public void saveResult(int points, String suit) {
		try {
			JSONObject config = new JSONObject(new JSONTokener(new FileReader("config_local.json")));
			String username = config.getString("username");

			MongoCollection<Document> scores = database.getCollection("scores");
			Document result = new Document("name", username).append("points", points).append("suit", suit)
					.append("date", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
			scores.insertOne(result);
		} catch (JSONException | FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
		}
	}

	/**
	 * Loads French cards from the local directory into the database.
	 * 
	 * @return True if successful, false otherwise.
	 */
	private boolean loadFrenchCards() {
		String collectionName = "cards_fr";
		MongoCollection<Document> cardCollection = database.getCollection(collectionName);

		cardCollection.drop();

		List<Document> cards = new ArrayList<>();
		Path directoryPath = Paths.get("./img/cards_fr");

		try {
			Files.list(directoryPath).forEach(filePath -> {
				if (Files.isRegularFile(filePath)
						&& filePath.toString().matches(".*(clubs|diamonds|hearts|spades)_\\d{2}\\.png$")) {
					String suit = filePath.getFileName().toString().split("_")[0];
					int points = Integer.parseInt(filePath.getFileName().toString().split("_")[1].replace(".png", ""));
					String base64 = encodeImageToBase64(filePath);

					Document card = new Document("suit", suit).append("points", points).append("image", base64);
					cards.add(card);
				}
			});

			if (!cards.isEmpty()) {
				cardCollection.insertMany(cards);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
		}
		return false;
	}

	/**
	 * Loads Spanish cards from the local directory into the database.
	 * 
	 * @return True if successful, false otherwise.
	 */
	private boolean loadSpanishCards() {
		String collectionName = "cards_es";
		MongoCollection<Document> cardCollection = database.getCollection(collectionName);

		cardCollection.drop();

		List<Document> cards = new ArrayList<>();
		Path directoryPath = Paths.get("./img/cards_es");

		try {
			Files.list(directoryPath).forEach(filePath -> {
				if (Files.isRegularFile(filePath)
						&& filePath.toString().matches(".*(bastos|copas|espadas|oros)_\\d{2}\\.jpg$")) {
					String suit = filePath.getFileName().toString().split("_")[0];
					int points = Integer.parseInt(filePath.getFileName().toString().split("_")[1].replace(".jpg", ""));
					String base64 = encodeImageToBase64(filePath);

					Document card = new Document("suit", suit).append("points", points).append("image", base64);
					cards.add(card);
				}
			});

			if (!cards.isEmpty()) {
				cardCollection.insertMany(cards);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
		}
		return false;
	}

	/**
	 * Encodes an image file to a Base64 string.
	 * 
	 * @param filePath The path to the image file.
	 * @return The Base64-encoded string of the image.
	 */
	private String encodeImageToBase64(Path filePath) {
		try {
			byte[] fileContent = Files.readAllBytes(filePath);
			return Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
			throw new RuntimeException("Failed to encode image to Base64: " + filePath, e);
		}
	}

	/**
	 * Closes the MongoDB connection.
	 */
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

}
