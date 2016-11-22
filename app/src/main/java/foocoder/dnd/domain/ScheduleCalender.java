package foocoder.dnd.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by xuechi.
 * Time: 2016 十月 31 21:36
 * Project: dnd
 */

public final class ScheduleCalender {

    private final List<Integer> days = new ArrayList<>(7);

    private final Calendar calendar = Calendar.getInstance();

    private Schedule schedule;

    @Override
    public String toString() {
        return "ScheduleCalendar[days=" + days + "]";
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        if (Objects.equals(this.schedule, schedule)) return;
        this.schedule = schedule;
        days.addAll(this.schedule.days);
    }

    public boolean isInSchedule(long time) {
        if (schedule.days.size() == 0) return false;
        final long start = getTime(time, schedule.startHour, schedule.startMinute);
        long end = getTime(time, schedule.endHour, schedule.endMinute);
        if (end <= start) {
            end = addDays(end, 1);
        }
        return isInSchedule(-1, time, start, end) || isInSchedule(0, time, start, end);
    }

    private boolean isInSchedule(int daysOffset, long time, long start, long end) {
        final int n = Calendar.SATURDAY;
        final int day = ((getDayOfWeek(time) - 1) + (daysOffset % n) + n) % n + 1;
        start = addDays(start, daysOffset);
        end = addDays(end, daysOffset);
        return days.get(day - 1) > 0 && time >= start && time < end;
    }

    public long getNextChangeTime(long now) {
        if (schedule == null) return 0;
        final long nextStart = getNextTime(now, schedule.startHour, schedule.startMinute);
        final long nextEnd = getNextTime(now, schedule.endHour, schedule.endMinute);
        return Math.min(nextStart, nextEnd);
    }

    private long getNextTime(long now, int hr, int min) {
        final long time = getTime(now, hr, min);
        return time <= now ? addDays(time, 1) : time;
    }


    private int getDayOfWeek(long time) {
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_WEEK);

    }

    private long getTime(long millis, int hour, int min) {
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long addDays(long time, int days) {
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DATE, days);
        return calendar.getTimeInMillis();
    }
}
