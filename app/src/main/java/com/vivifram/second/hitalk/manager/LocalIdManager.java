package com.vivifram.second.hitalk.manager;

import java.util.Random;

/**
 * Created by zuowei on 16-8-24.
 */
public class LocalIdManager {

    private static LocalIdManager sInstance;
    private static Object LOCK = new Object();
    private final Random random;
    private LocalIdManager(){
        random = new Random();
    }
    public static LocalIdManager getInstance(){
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new LocalIdManager();
                }
            }
        }
        return sInstance;
    }

    public boolean isLocalId(String localId) {
        if (!localId.startsWith("local_")) {
            return false;
        }
        for (int i = 6; i < localId.length(); ++i) {
            char c = localId.charAt(i);
            if (!(c >= '0' && c <= '9') && !(c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    public synchronized String createLocalId() {
        long localIdNumber = random.nextLong();
        String localId = "local_" + Long.toHexString(localIdNumber);

        if (!isLocalId(localId)) {
            throw new IllegalStateException("Generated an invalid local id: \"" + localId + "\". "
                    + "This should never happen. Contact us at https://parse.com/help");
        }

        return localId;
    }
}
