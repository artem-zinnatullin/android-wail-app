package com.artemzin.android.wail.util;

/**
 * Utils for dates difference
 * @author Artem Zinnatullin
 *
 */
public class WordFormUtil {

    /**
     * Returning needed word form
     * @param count of objects
     * @param wordForms array with 3 elements of word to return
     * @return needed word form
     * Example:
     *     wordForms = {"Волк", "Волка", "Волков"}
     *     count = 1    -> return "Волк"
     *     count = 2..4 -> return "Волка"
     *     count = 5... -> return "Волков"
     */
    public static String getWordForm(final long count, String[] wordForms) {
        final long countAbs = Math.abs(count);

        if (wordForms.length == 1) {
            return wordForms[0];
        } else if (wordForms.length == 2) {
            return getWordFormFor2WordForms(countAbs, wordForms);
        } else if (wordForms.length >= 3) {
            return getWordFormFor3WordForms(countAbs, wordForms);
        } else {
            return null;
        }
    }

    private static String getWordFormFor2WordForms(final long countAbs, String[] wordForms) {
        if (countAbs == 1) {
            return wordForms[0];
        } else {
            return wordForms[1];
        }
    }

    private static String getWordFormFor3WordForms(final long countAbs, String[] wordForms) {
        if (countAbs == 1) {
            return wordForms[0];
        } else if(countAbs > 1 & countAbs < 5) {
            return wordForms[1];
        } else if (countAbs > 20) {
            if(countAbs > 100) {
                long tempCount = countAbs % 100;
                if(tempCount < 20) {
                    return getWordFormFor3WordForms(tempCount, wordForms);
                } else {
                    return getWordFormFor3WordForms(countAbs % 10, wordForms);
                }
            } else {
                return getWordFormFor3WordForms(countAbs % 10, wordForms);
            }
        } else {
            return wordForms[2];
        }
    }
}
