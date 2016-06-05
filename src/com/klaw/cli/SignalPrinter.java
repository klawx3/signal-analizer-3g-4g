/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klaw.cli;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Klaw Strife
 */
public class SignalPrinter {

    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    
    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void print(float signal) {

        StringBuilder sb = new StringBuilder();
        Calendar instance = Calendar.getInstance();
        sb.append(ANSI_WHITE).append(DATE_TIME_FORMAT.format(instance.getTime())).append(" ");
        if (signal >= -73f) { // verde
            sb.append(ANSI_GREEN).append(DECIMAL_FORMAT.format(signal));
        } else if (signal < -73f && signal >= -79f) { // amarillo
            sb.append(ANSI_YELLOW).append(DECIMAL_FORMAT.format(signal));
        } else if (signal < -79f) { // rojo
            sb.append(ANSI_RED).append(DECIMAL_FORMAT.format(signal));
        } else {
            sb.append(ANSI_CYAN).append(DECIMAL_FORMAT.format(signal));
        }
        sb.append("dBm");
        System.out.println(sb.toString());

    }

}
