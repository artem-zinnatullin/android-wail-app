
package com.artemzin.android.wail.test.unit.api;

import com.artemzin.android.wail.api.MD5Hash;
import com.artemzin.android.wail.test.unit.BaseAndroidTestCase;

import java.security.NoSuchAlgorithmException;

/**
 * Tests for {@link MD5Hash}.
 */
public class MD5HashTest extends BaseAndroidTestCase {
    public void testCalculateMD5() throws NoSuchAlgorithmException {
        assertEquals("acbd18db4cc2f85cedef654fccc4a4d8", MD5Hash.calculateMD5("foo"));
        assertEquals("37b51d194a7513e45b56f6524f2d51f2", MD5Hash.calculateMD5("bar"));
        // With 0-padding
        assertEquals("0cc175b9c0f1b6a831c399e269772661", MD5Hash.calculateMD5("a"));
    }
}
