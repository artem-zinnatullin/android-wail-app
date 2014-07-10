package com.artemzin.android.bytes.common;

/**
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class WordFormUtil {

    /**
     * Returning required word form
     * @param count of objects
     * @param wordForms array with 1, 2 or 3 word forms to return
     * @return required word form
     * <p>Example for English:
     * wordForms = { "track", "tracks" }
     * count = 1 -> return "track"
     * count > 1 -> return "tracks"
     * </p>
     *
     * <p>
     * Example for Russian and some other languages with 3 word forms: <br>
     *     wordForms = {"Волк", "Волка", "Волков"}<br/>
     *     count = 1    -> return "Волк" <br/>
     *     count = 2..4 -> return "Волка" <br/>
     *     count = 5... -> return "Волков" <br/>
     * </p>
     */
    public static String wordFormForCount(final long count, String[] wordForms) {
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
