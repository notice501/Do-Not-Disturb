package foocoder.dnd.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by xuechi.
 * Time: 2016 十一月 01 15:30
 * Projet: dnd
 */

public final class TimeUtil {

    @Nullable
    public static int[] tryParseHourAndMinute(String value) {
        if (TextUtils.isEmpty(value)) return null;
        final int i = value.indexOf(':');
        if (i < 1 || i >= value.length() - 1) return null;
        final int hour = tryParseInt(value.substring(0, i), -1);
        final int minute = tryParseInt(value.substring(i + 1), -1);
        return isValidHour(hour) && isValidMinute(minute) ? new int[] { hour, minute } : null;
    }

    private static int tryParseInt(String value, int defValue) {
        if (TextUtils.isEmpty(value)) return defValue;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    private static boolean isValidHour(int val) {
        return val >= 0 && val < 24;
    }

    private static boolean isValidMinute(int val) {
        return val >= 0 && val < 60;
    }
}
