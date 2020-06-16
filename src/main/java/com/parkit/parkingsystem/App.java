package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Launch Park'it Application.
 *
 * @author Laura
 */
public final class App {

  /**
   * App logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("App");

  /**
   * Empty constructor of class App.
   */
  private App() {
  }

  /**
   * Starts Park'it application.
   *
   * @param args no argument
   */
  public static void main(final String[] args) {
    LOGGER.info("Initializing Parking System");
    InteractiveShell.loadInterface();
  }
}
