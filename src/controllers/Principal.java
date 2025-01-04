package controllers;

import gui.Main;
import models.MongoDBSingleton;

/**
 * The entry point of the application.
 * 
 * This class initializes the main graphical user interface (GUI) and the main
 * controller, providing access to the MongoDB database through a singleton
 * instance.
 */
public class Principal {

	/**
	 * The main method serves as the starting point of the application.
	 * 
	 * @param args Command-line arguments (not used in this application).
	 */
	public static void main(String[] args) {
		Main main = new Main();

		new MainController(main, MongoDBSingleton.getInstance());
	}
}
