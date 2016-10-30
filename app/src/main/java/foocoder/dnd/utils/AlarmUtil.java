package foocoder.dnd.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.presentation.App;
import foocoder.dnd.services.ListenerService;
import foocoder.dnd.receivers.TimeReceiver;

import static android.app.AlarmManager.RTC_WAKEUP;
import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static foocoder.dnd.presentation.App.AUTO_TIME_SCHEDULE;
import static org.joda.time.DateTimeConstants.FRIDAY;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;

public class AlarmUtil {

    private static App context;

    private static SharedPreferenceUtil spUtil;

    private static AudioManager audioManager;

    private static AlarmManager alarmManager;

    private static int[] daysOfWeek;

    static {
        context = App.getContext();
        audioManager = context.getAudioManager();
        alarmManager = context.getAlarmManager();
        spUtil = context.getSharedPreferenceUtil();
        daysOfWeek = new int[]{MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
    }

    public static PendingIntent getPendingIntent(Context context, int id, Intent intent) {
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     *
     * @param sound_enable if or not make silence. false means silent
     * @param schedule current mute schedule
     */
    public static void setSilent(boolean sound_enable, Schedule schedule) {
        if (!sound_enable) {
            if (spUtil.getRunningId() == -1 || spUtil.getRunningId() == schedule._id) {
                spUtil.enable(true);
                audioManager.setRingerMode(spUtil.isVib() ? RINGER_MODE_VIBRATE : RINGER_MODE_SILENT);
                spUtil.setRunningId(schedule._id);
                Intent intent = new Intent(context, ListenerService.class);
                intent.putExtra("note", context.getString(R.string.auto_task) + schedule.to + context.getString(R.string.auto_task_over));
                context.startService(intent);
            }
        } else {
            if (spUtil.getRunningId() == schedule._id) {
                spUtil.setRunningId(-1);
                spUtil.enable(false);
                audioManager.setRingerMode(RINGER_MODE_NORMAL);
                context.startService(new Intent(context, ListenerService.class));
            }
        }
    }

    public static void startSchedule(Schedule schedule) {
        if (!schedule.checked.contains(1)) {
            cancelOldAlarm(schedule);
        } else {
            DateTime now = DateTime.now();
            int today = now.getDayOfWeek();

            String[] start = schedule.from.split(":");
            DateTime from = DateTime.now()
                    .withDayOfWeek(today)
                    .withHourOfDay(Integer.parseInt(start[0]))
                    .withMinuteOfHour(Integer.parseInt(start[1]))
                    .withSecondOfMinute(0);
            String[] end = schedule.to.split(":");
            DateTime to = DateTime.now()
                    .withDayOfWeek(today)
                    .withHourOfDay(Integer.parseInt(end[0]))
                    .withMinuteOfHour(Integer.parseInt(end[1]))
                    .withSecondOfMinute(0);

            int daysTillNext = daysTillNext(schedule);
            boolean cross_night = false;

            cancelOldAlarm(schedule);

            /**
             * 如果结束时间小于起始时间,则说明结束时间为第二天的时间
             * 如果昨天在计划中并且当前时间小于静音结束时间则开启静音, 否则将结束时间设置为第二天
             */
            if (to.compareTo(from) <= 0) {
                cross_night = true;
                if (daysEnabled(schedule).contains(today == MONDAY ? SUNDAY : today - 1)) {
                    if (now.compareTo(to) < 0) {
                        setSilent(false, schedule);
                    } else {
                        to = to.plusDays(1);
                    }
                } else {
                    to = to.plusDays(1);
                }
            }

            /**
             * 如果计划表中包含今天则判断下当前时间跟计划时间的大小:
             * 如果当前时间处于计划起始时间以及结束时间之间则开启静音, 并且将起始时间设置为下一次静音的时间
             * 如果当前时间小于起始时间或者大于结束时间则不开启静音, 并且将起始/结束时间均设置为下一次静音的时间
             *
             * 如果计划表中不包含今天则将起始/结束时间均设置为下一次静音的时间
             */
            if (daysEnabled(schedule).contains(today)) {
                if (from.compareTo(now) <= 0 && to.compareTo(now) > 0) {
                    setSilent(false, schedule);
                    from = from.plusDays(daysTillNext);
                } else {
                    from = from.plusDays(daysTillNext);
                    to = to.plusDays(daysTillNext);
                }
            } else {
                from = from.plusDays(daysTillNext);
                to = to.plusDays(daysTillNext);
            }

            Intent startIntent = new Intent(context, TimeReceiver.class).setAction(AUTO_TIME_SCHEDULE);
            startIntent.putExtra("id", schedule._id);
            startIntent.putExtra("sound_enable", false);
            startIntent.putExtra("cross_night", cross_night);
            alarmManager.set(RTC_WAKEUP, from.getMillis(), getPendingIntent(context, schedule._id, startIntent));

            Intent endIntent = new Intent(context, TimeReceiver.class).setAction(AUTO_TIME_SCHEDULE);
            endIntent.putExtra("id", schedule._id + 1);
            endIntent.putExtra("sound_enable", true);
            endIntent.putExtra("cross_night", cross_night);
            alarmManager.set(RTC_WAKEUP, to.getMillis(), getPendingIntent(context, schedule._id + 1, endIntent));
        }
    }

    public static int daysTillNext(Schedule schedule) {
        int daysTillNext = 7;
        int today = DateTime.now().getDayOfWeek();

        if (schedule != null) {
            List<Integer> daysEnabled = daysEnabled(schedule);
            if (daysEnabled.size() > 0) {
                for (int dayEnabled : daysEnabled) {
                    if (dayEnabled <= today) {
                        if (daysTillNext == 7) {
                            daysTillNext = 7 - (today - dayEnabled);
                        }
                    } else {
                        daysTillNext = dayEnabled - today;
                        break;
                    }
                }
            }
        }

        return daysTillNext;
    }

    public static int daysTillNext(Schedule schedule, boolean cross_night) {
        if (cross_night) {
            int today = DateTime.now().getDayOfWeek();
            if (daysEnabled(schedule).contains(today)) {
                return 1;
            } else {
                return daysTillNext(schedule) + 1;
            }
        } else {
            return daysTillNext(schedule);
        }
    }

    private static List<Integer> daysEnabled(Schedule schedule) {
        List<Integer> daysEnabled = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (schedule.checked.get(i) == 1) {
                daysEnabled.add(daysOfWeek[i]);
            }
        }

        return daysEnabled;
    }

    public static void cancelOldAlarm(Schedule schedule) {
        Intent temp = new Intent(context, TimeReceiver.class).setAction(AUTO_TIME_SCHEDULE);
        PendingIntent cancel = getPendingIntent(context, schedule._id, temp);
        cancel.cancel();
        alarmManager.cancel(cancel);
        PendingIntent newCancel = getPendingIntent(context, schedule._id + 1, temp);
        newCancel.cancel();
        alarmManager.cancel(newCancel);
        if (spUtil.getRunningId() != -1 && spUtil.isUsable()) {
            setSilent(true, schedule);
        }
    }

    public static void pauseAlarm(Context context, Schedule schedule) {
        cancelOldAlarm(schedule);
        Calendar calNow = Calendar.getInstance();
        int currentDay = calNow.get(Calendar.DAY_OF_WEEK);
        if (currentDay == Calendar.SUNDAY) {
            currentDay = SUNDAY;
        }

        String[] start = schedule.from.split(":");
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(Calendar.DAY_OF_WEEK, currentDay);
        calFrom.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
        calFrom.set(Calendar.MINUTE, Integer.parseInt(start[1]));
        calFrom.set(Calendar.SECOND, 0);
        String[] end = schedule.to.split(":");
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.DAY_OF_WEEK, currentDay);
        calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end[0]));
        calEnd.set(Calendar.MINUTE, Integer.parseInt(end[1]));
        calEnd.set(Calendar.SECOND, 0);

        if (calEnd.compareTo(calFrom) <= 0) {
            calEnd.add(Calendar.DATE, 1);
        }

        calEnd.add(Calendar.HOUR_OF_DAY, 1);
        Intent intent = new Intent(context, TimeReceiver.class).setAction(AUTO_TIME_SCHEDULE);
        intent.putExtra("notify", true);
        PendingIntent pi = PendingIntent.getBroadcast(context, -2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(RTC_WAKEUP, calEnd.getTimeInMillis(), pi);
        context.startService(new Intent(context, ListenerService.class).putExtra("note", context.getString(R.string.auto_task) + calEnd.get(Calendar.HOUR_OF_DAY) + ":" + calEnd.get(Calendar.MINUTE) + context.getString(R.string.auto_task_over)));
    }
}
