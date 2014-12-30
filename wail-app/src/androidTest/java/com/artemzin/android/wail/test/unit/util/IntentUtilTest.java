package com.artemzin.android.wail.test.unit.util;

import android.content.Intent;

import com.artemzin.android.wail.test.unit.BaseAndroidTestCase;
import com.artemzin.android.wail.util.IntentUtil;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class IntentUtilTest extends BaseAndroidTestCase {

    public void testGetLongOrIntExtraNullIntent() {
        assertEquals(-1, IntentUtil.getLongOrIntExtra(null, -1, "WOW"));
    }

    public void testGetLongOrIntExtraNullExtraName() {
        assertEquals(-1, IntentUtil.getLongOrIntExtra(new Intent(), -1, null));
    }

    public void testGetLongOrIntNoExtra() {
        Intent intent = new Intent();
        assertEquals(-1, IntentUtil.getLongOrIntExtra(intent, -1, "NO_SUCH_EXTRA"));
    }

    public void testGetLongOrIntExtraShortExtra() {
        Intent intent = new Intent();
        short value = 143;
        intent.putExtra("SHORT_EXTRA", value);
        assertEquals(value, IntentUtil.getLongOrIntExtra(intent, -1, "SHORT_EXTRA"));
    }

    public void testGetLongOrIntExtraIntExtra() {
        Intent intent = new Intent();
        intent.putExtra("INT_EXTRA", 3);
        assertEquals(3, IntentUtil.getLongOrIntExtra(intent, -1, "INT_EXTRA"));
    }

    public void testGetLongOrIntExtraLongExtra() {
        Intent intent = new Intent();
        intent.putExtra("LONG_EXTRA", 1424125151214L);
        assertEquals(1424125151214L, IntentUtil.getLongOrIntExtra(intent, -1, "LONG_EXTRA"));
    }

    public void testGetLongOrIntExtraStringExtra() {
        Intent intent = new Intent();
        intent.putExtra("STRING_EXTRA", "someValue");
        assertEquals(-1, IntentUtil.getLongOrIntExtra(intent, -1, "STRING_EXTRA"));
    }

    public void testGetLongOrIntExtraPossibilities() {
        Intent intent = new Intent();
        intent.putExtra("REAL_LONG_EXTRA", 2145125L);
        assertEquals(2145125L, IntentUtil.getLongOrIntExtra(intent, -1, "INCORRECT_EXTRA", "ANOTHER_EXTRA", "REAL_LONG_EXTRA", "OTHER_EXTRA"));
    }

    public void testGetBoolOrNumberAsBoolNullIntent() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(null, false, ""));
    }

    public void testGetBoolOrNumberAsBoolNoExtras() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent(), false, "asd"));
    }

    public void testGetBoolOrNumberAsBoolFalse() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("BOOL_VALUE", false), true, "BOOL_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolTrue() {
        assertEquals(Boolean.TRUE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("BOOL_VALUE", true), false, "BOOL_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolIntFalse() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("INT_VALUE", -1), true, "INT_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolIntTrue() {
        assertEquals(Boolean.TRUE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("INT_VALUE", 1), false, "INT_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolLongFalse() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("LONG_VALUE", -1L), true, "LONG_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolLongTrue() {
        assertEquals(Boolean.TRUE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("LONG_VALUE", 1L), false, "LONG_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolShortFalse() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("SHORT_VALUE", new Short("-1")), true, "SHORT_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolShortTrue() {
        assertEquals(Boolean.TRUE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("SHORT_VALUE", new Short("1")), false, "SHORT_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolByteFalse() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("BYTE_VALUE", new Byte("0")), true, "BYTE_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolByteTrue() {
        assertEquals(Boolean.TRUE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent().putExtra("BYTE_VALUE", new Byte("1")), false, "BYTE_VALUE"));
    }

    public void testGetBoolOrNumberAsBoolPossibleNamesFalse() {
        assertEquals(Boolean.FALSE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent()
                .putExtra("ASD", -1)
                .putExtra("ANOTHER", 145)
                .putExtra("ASDadfaf", "adfa"),
                false,
                "dkgds", "dgsgs", "dsgsfg")
        );
    }

    public void testGetBoolOrNumberAsBoolPossibleNamesTrue() {
        assertEquals(Boolean.TRUE, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent()
                .putExtra("ASD", -1)
                .putExtra("ANOTHER", 145)
                .putExtra("ASDadfaf", "adfa"),
                false,
                "dkgds", "ANOTHER", "dsgsfg")
        );
    }

    public void testGetBoolOrNumberAsBoolPossibleNamesNull() {
        assertEquals(null, IntentUtil.getBoolOrNumberAsBoolExtra(new Intent()
                .putExtra("ASD", -1)
                .putExtra("ANOTHER", 145)
                .putExtra("ASDadfaf", "adfa"),
                null,
                "dkgds", "ANOTHERasf", "dsgsfg")
        );
    }

    public void testGetIntentAsStringNullIntent() {
        assertEquals("null intent", IntentUtil.getIntentAsString(null));
    }

    public void testGetIntentAsStringNoExtras() {
        assertEquals("Intent action: null, no extras", IntentUtil.getIntentAsString(new Intent()));
    }

    public void testGetIntentAsString() {
        String intentAsString = IntentUtil.getIntentAsString(new Intent()
                .putExtra("1", "value1")
                .putExtra("3", 4));
        assertTrue(intentAsString.contains("Intent action: null, extras: "));
        assertTrue(intentAsString.contains("(1, value1)"));
        assertTrue(intentAsString.contains("(3, 4)"));
    }
}
