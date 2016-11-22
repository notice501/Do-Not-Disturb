package foocoder.dnd.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import java.util.List;

import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.ScheduleCalender;
import foocoder.dnd.presentation.App;
import foocoder.dnd.receivers.TimeReceiver;
import foocoder.dnd.services.ListenerService;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static foocoder.dnd.presentation.App.AUTO_TIME_SCHEDULE;

public class AlarmUtil {

    private static final int REQUEST_CODE = 1;
    private static App context;
    private static SharedPreferenceUtil spUtil;
    private static AudioManager audioManager;
    private static AlarmManager alarmManager;
    private static long nextAlarmTime;

    static {
        context = App.getContext();
        audioManager = context.getAudioManager();
        alarmManager = context.getAlarmManager();
        spUtil = context.getSharedPreferenceUtil();
    }

    public static PendingIntent getPendingIntent(Context context, int id, Intent intent) {
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * @param sound_enable if or not make silence. false means silent
     * @param schedule     current mute schedule
     */
    public static void setSilent(boolean sound_enable, Schedule schedule) {
        if (!sound_enable) {
            spUtil.enable(true);
            audioManager.setRingerMode(spUtil.isVib() ? RINGER_MODE_VIBRATE : RINGER_MODE_SILENT);
            Intent intent = new Intent(context, ListenerService.class);
            intent.putExtra("note", context.getString(R.string.auto_task) + schedule.to + context.getString(R.string.auto_task_over));
            context.startService(intent);
        } else {
            spUtil.enable(false);
            audioManager.setRingerMode(RINGER_MODE_NORMAL);
            context.startService(new Intent(context, ListenerService.class));
        }
    }

    public static void start(List<Schedule> schedules) {
        final long now = System.currentTimeMillis();
        boolean silent = false;
        ScheduleCalender enabled = new ScheduleCalender();
        for (Schedule schedule : schedules) {
            ScheduleCalender cal = new ScheduleCalender();
            cal.setSchedule(schedule);
            final long nextChangeTime = cal.getNextChangeTime(now);
            if (nextChangeTime > 0 && nextChangeTime > now) {
                if (nextAlarmTime == 0 || nextChangeTime < nextAlarmTime) {
                    nextAlarmTime = nextChangeTime;
                }
            }
            if (cal.isInSchedule(now)) {
                silent = true;
                if (enabled.getSchedule() == null) {
                    enabled = cal;
                } else {
                    if (nextChangeTime < enabled.getNextChangeTime(now)) {
                        enabled = cal;
                    }
                }
            }
        }
        setSilent(!silent, enabled.getSchedule());
        updateAlarm(now, nextAlarmTime);
    }

    private static void updateAlarm(long now, long time) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                new Intent(AUTO_TIME_SCHEDULE), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        if (time > now) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
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
//        cancelOldAlarm(schedule);
//        Calendar calNow=Calendar.getInstance();
//        int currentDay=calNow.get(Calendar.DAY_OF_WEEK);
//        if(currentDay==Calendar.SUNDAY){
//        currentDay=SUNDAY;
//        }
//
//        String[]start=schedule.from.split(":");
//        Calendar calFrom=Calendar.getInstance();
//        calFrom.set(Calendar.DAY_OF_WEEK,currentDay);
//        calFrom.set(Calendar.HOUR_OF_DAY,Integer.parseInt(start[0]));
//        calFrom.set(Calendar.MINUTE,Integer.parseInt(start[1]));
//        calFrom.set(Calendar.SECOND,0);
//        String[]end=schedule.to.split(":");
//        Calendar calEnd=Calendar.getInstance();
//        calEnd.set(Calendar.DAY_OF_WEEK,currentDay);
//        calEnd.set(Calendar.HOUR_OF_DAY,Integer.parseInt(end[0]));
//        calEnd.set(Calendar.MINUTE,Integer.parseInt(end[1]));
//        calEnd.set(Calendar.SECOND,0);
//
//        if(calEnd.compareTo(calFrom)<=0){
//        calEnd.add(Calendar.DATE,1);
//        }
//
//        calEnd.add(Calendar.HOUR_OF_DAY,1);
//        Intent intent=new Intent(context,TimeReceiver.class).setAction(AUTO_TIME_SCHEDULE);
//        intent.putExtra("notify",true);
//        PendingIntent pi=PendingIntent.getBroadcast(context,-2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.set(RTC_WAKEUP,calEnd.getTimeInMillis(),pi);
//        context.startService(new Intent(context,ListenerService.class).putExtra("note",context.getString(R.string.auto_task)+calEnd.get(Calendar.HOUR_OF_DAY)+":"+calEnd.get(Calendar.MINUTE)+context.getString(R.string.auto_task_over)));
//        }
    }
}
