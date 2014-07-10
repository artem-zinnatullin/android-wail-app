package com.artemzin.android.wail.test.unit.storage.db;

import com.artemzin.android.wail.storage.db.AppDBManager;
import com.artemzin.android.wail.test.unit.BaseAndroidTestCase;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class AppDBManagerTest extends BaseAndroidTestCase {

    public void testGetInstanceIsSingleton() {
        assertSame(AppDBManager.getInstance(getContext()), AppDBManager.getInstance(getContext()));
    }

    public void testConvertIntegerToBoolean1() {
        assertTrue(AppDBManager.convertIntegerToBoolean(1));
    }

    public void testConvertIntegerToBoolean2() {
        assertTrue(AppDBManager.convertIntegerToBoolean(2));
    }

    public void testConvertIntegerToBooleanRandomPositive() {
        assertTrue(AppDBManager.convertIntegerToBoolean(getRandom().nextInt(10000) + 1));
    }

    public void testConvertIntegerToBooleanMinus1() {
        assertFalse(AppDBManager.convertIntegerToBoolean(-1));
    }

    public void testConvertIntegerToBooleanMinus2() {
        assertFalse(AppDBManager.convertIntegerToBoolean(-2));
    }

    public void testConvertIntegerToBooleanBigNegative() {
        assertFalse(AppDBManager.convertIntegerToBoolean((-1) * (getRandom().nextInt(10000) + 1)));
    }

    public void testConvertIntegerToBooleanZero() {
        assertNull(AppDBManager.convertIntegerToBoolean(0));
    }

    public void testConvertBooleanToIntegerNull() {
        assertEquals(0, AppDBManager.convertBooleanToInteger(null));
    }

    public void testConvertBooleanToIntegerTrue() {
        assertEquals(1, AppDBManager.convertBooleanToInteger(true));
    }

    public void testConvertBooleanToIntegerFalse() {
        assertEquals(-1, AppDBManager.convertBooleanToInteger(false));
    }
}
