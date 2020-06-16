package com.parkit.parkingsystem.util;

import java.util.Scanner;

/**
 * Reads keyboard input.
 * As Scanner is final, the ScannerWrapper is used to help facilitate in the
 * writing tests.
 */
public class ScannerWrapper {

  /**
   * A Scanner initialization in order to read the inputs users.
   */
  private static final Scanner SCANNER = new Scanner(System.in, "UTF-8");

  /**
   * Reads keyboard input.
   *
   * @return the input entered by the user.
   */
  public String nextLine() {
    return SCANNER.nextLine();
  }
}
