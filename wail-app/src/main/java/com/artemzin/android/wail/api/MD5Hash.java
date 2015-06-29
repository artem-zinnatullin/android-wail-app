package com.artemzin.android.wail.api;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculates MD5 hash, results should be equals to PHP md5() function
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class MD5Hash {

    /**
     * Calculates MD5 hash from input string <br/>
     * Result should be equals to PHP md5() function
     * @param input string md5 from which should be calculated
     * @return calculated md5 hash from input string
     * @throws NoSuchAlgorithmException if md5 could not be calculated, because algorithm not founded
     */
    public static String calculateMD5(String input) throws NoSuchAlgorithmException {
        try {
            final byte[] digest = MessageDigest.getInstance("MD5").digest(input.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, digest));
        } catch (UnsupportedEncodingException e) {
            // Should not happen: UTF-8 always available
            throw new RuntimeException(e);
        }
    }
}
