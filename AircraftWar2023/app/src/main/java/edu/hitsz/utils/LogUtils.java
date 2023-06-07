package edu.hitsz.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Objects;

public class LogUtils {
    public static final void logException(String TAG, Exception e){
        Log.e(TAG, e.getMessage());
        Log.e(TAG, String.valueOf(Arrays.stream(e.getStackTrace()).map(Objects::toString).reduce((a, b)->(a + "\n"+ b))));
    }
}
