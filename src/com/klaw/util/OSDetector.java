/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klaw.util;

/**
 *
 * @author Klaw Strife
 */
public class OSDetector {
        
        private static final String OS = System.getProperty("os.name").toLowerCase();

        public static boolean isWindows() {
            return (OS.contains("win"));
        }

        public static boolean isMac() {
            return (OS.contains("mac"));
        }

        public static boolean isUnix() {
            return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
        }

        public static boolean isSolaris() {
            return (OS.contains("sunos"));
        }

        public static String getOS() {
            if (isWindows()) {
                return "win";
            } else if (isMac()) {
                return "osx";
            } else if (isUnix()) {
                return "uni";
            } else if (isSolaris()) {
                return "sol";
            } else {
                return "err";
            }
        }
    }
