package org.tucantest.tucan_exercise_android;

import android.util.Log;
import android.view.MotionEvent;

import java.lang.reflect.Method;

public class MotionEventExtensions {

    /**
     * Gets the timestamp of the event in nano seconds.
     * @param event The MotionEvent to get the timestamp in nanoseconds.
     * @return The timestamp of the event in nanoseconds.
     */
    public static long getEventTimeNano(MotionEvent event) {
        long t_nanos = -1;
        try {
            Class cls = Class.forName("android.view.MotionEvent");
            Method myTimeGetter = cls.getMethod("getEventTimeNano");
            t_nanos = (long) myTimeGetter.invoke(event);
        } catch (Exception e) {
            Log.i("WALT.MsMotionEvent", e.getMessage());
        }

        return t_nanos;
    }

    /**
     * Gets the timestamp of the historical event in nano seconds.
     * @param event The MotionEvent to get the timestamp of the specified historical event in nanoseconds.
     * @param pos The position of the historical event within the motion event.
     * @return The timestamp of the historical event in nanoseconds.
     */
    public static long getHistoricalEventTimeNano(MotionEvent event, int pos) {
        long t_nanos = -1;
        try {
            Class cls = Class.forName("android.view.MotionEvent");
            Method myTimeGetter = cls.getMethod("getHistoricalEventTimeNano", new Class[]{int.class});
            t_nanos = (long) myTimeGetter.invoke(event, new Object[]{pos});
        } catch (Exception e) {
            Log.i("WALT.MsMotionEvent", e.getMessage());
        }

        return t_nanos;
    }
}
