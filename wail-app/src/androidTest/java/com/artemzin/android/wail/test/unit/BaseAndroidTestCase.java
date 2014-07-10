package com.artemzin.android.wail.test.unit;

import android.content.Context;
import android.test.AndroidTestCase;

import com.artemzin.android.wail.storage.db.AppDBManager;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.PlayersDBHelper;
import com.artemzin.android.wail.storage.db.TracksDBHelper;

import java.util.Random;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class BaseAndroidTestCase extends AndroidTestCase {

    private final Random random = new Random(System.currentTimeMillis());

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearAllWAILData(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        clearAllWAILData(getContext());
        super.tearDown();
    }

    protected Random getRandom() {
        return random;
    }

    public static void clearAllWAILData(Context context) {
        WAILSettings.clearAllSettings(context);
        AppDBManager.getInstance(context).clearAll();

        //assertEquals(0, PlayersDBHelper.getInstance(context).getAll().size());
        assertEquals(0, TracksDBHelper.getInstance(context).getAllDesc().getCount());
    }
}
