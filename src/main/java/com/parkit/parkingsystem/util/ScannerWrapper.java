package com.parkit.parkingsystem.util;

import java.util.Scanner;

public class ScannerWrapper {

    private static final Scanner scanner = new Scanner(System.in, "UTF-8");

    public String nextLine() {
        return scanner.nextLine();
    }
}
