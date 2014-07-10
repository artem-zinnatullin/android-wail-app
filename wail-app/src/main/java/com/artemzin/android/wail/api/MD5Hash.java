package com.artemzin.android.wail.api;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculates MD5 hash, results should be equals to PHP md5() function
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class MD5Hash {

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // better never happens
        }
    }

    /**
     * Calculates MD5 hash from input string <br/>
     * Result should be equals to PHP md5() function
     * @param input string md5 from which should be calculated
     * @return calculated md5 hash from input string
     * @throws NoSuchAlgorithmException if md5 could not be calculated, because algorithm not founded
     */
    public static String calculateMD5(String input) throws NoSuchAlgorithmException {
        try {
            final byte[] bytes = digest.digest(input.getBytes("UTF-8"));
            final StringBuilder b = new StringBuilder(32);

            for (byte aByte : bytes) {
                final String hex = Integer.toHexString((int) aByte & 0xFF);
                if (hex.length() == 1)
                    b.append('0');
                b.append(hex);
            }

            return b.toString();
        } catch (UnsupportedEncodingException e) {
            // utf-8 always available
        }

        return null;
    }
}
