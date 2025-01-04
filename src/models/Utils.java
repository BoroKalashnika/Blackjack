package models;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Utility class containing methods for image conversion, password hashing, and
 * retrieving a username from a configuration file.
 */
public class Utils {

	/**
	 * Converts a Base64-encoded string into a PNG image and returns it as an
	 * ImageIcon.
	 * 
	 * @param base64 The Base64-encoded string representing the image.
	 * @param height The desired height of the scaled image (maintains aspect
	 *               ratio).
	 * @return An ImageIcon of the decoded and scaled image, or null if an error
	 *         occurs.
	 */
	public static ImageIcon convertBase64ToPng(String base64, int height) {
		try {
			byte[] btDataFile = Base64.decodeBase64(base64);
			ByteArrayInputStream bais = new ByteArrayInputStream(btDataFile);

			BufferedImage originalImage = ImageIO.read(bais);

			Image scaledImage = originalImage.getScaledInstance(-1, height, Image.SCALE_SMOOTH);

			BufferedImage bufferedScaledImage = new BufferedImage(scaledImage.getWidth(null),
					scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

			bufferedScaledImage.getGraphics().drawImage(scaledImage, 0, 0, null);

			ImageIO.write(bufferedScaledImage, "png", new File("imatge.png"));

			return new ImageIcon(bufferedScaledImage);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
		}
		return null;
	}

	/**
	 * Hashes a password using the SHA-256 algorithm and returns the hashed value as
	 * a hexadecimal string.
	 * 
	 * @param password The password to be hashed.
	 * @return A hexadecimal string representing the hashed password.
	 * @throws RuntimeException if the hashing algorithm is not available.
	 */
	public static String hashPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] encodedHash = digest.digest(password.getBytes());

			StringBuilder hexString = new StringBuilder();
			for (byte b : encodedHash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
			throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the username from a local configuration JSON file named
	 * "config_local.json".
	 * 
	 * @return The username as a string, or null if an error occurs.
	 */
	public static String getUsername() {
		String username = null;
		try {
			JSONObject config = new JSONObject(new JSONTokener(new FileReader("config_local.json")));

			username = config.getString("username");
		} catch (JSONException | FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
		}
		return username;
	}
}
