package com.artemzin.android.bytes.common;

/**
 * Util methods to effectively work with strings
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class StringUtil {

    private StringUtil() {}

    /**
     * Checks is string null or its length == 0, very useful
     *
     * @param string
     *          object to check, can be null :D
     * @return {@code true} if string is null or its length == 0, {@code false} otherwise
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * Checks is string null or its length == 0 or it contains only whitespaces, very useful
     *
     * @param string
     *          object to check, can be null :D
     * @return true if string is null or its length == 0 or it contains only whitespaces,
     *          false otherwise
     */
    public static boolean isNullOrEmptyOrOnlyWhitespaces(String string) {
        return isNullOrEmpty(string) ? true : string.trim().length() == 0;
    }

    /**
     * Awesome way to get only digits from your string
     *
     * @param stringWithDigits
     *          string you want to get digits from, can be null or empty or even without digits
     * @return String with only digits from stringWithDigits or empty string "" if stringWithDigits was null or did not contain any digits.
     *          Example: stringWithDigits = "afb1et2fnvf3fs4", result = "1234"
     */
    public static String getOnlyDigitsFromString(String stringWithDigits) {
        return isNullOrEmpty(stringWithDigits) ? "" : stringWithDigits.replaceAll("\\D", "");
    }

    /**
     * Comparing two strings, returns true if they are equal, if both are null -> result == true
     *
     * @param str1
     *          first string to compare, can be null
     * @param str2
     *          second string to compare, can be null
     * @return true if string are equal or if both of references are null, compare is case sensitive
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }
}
