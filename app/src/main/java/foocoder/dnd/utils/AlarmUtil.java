package foocoder.dnd.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import foocoder.dnd.R;
import foocoder.dnd.presentation.App;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.services.ListenerService;
import foocoder.dnd.services.TimeReceiver;

public class AlarmUtil {

    private static final int SUNDAY = 8;

    private static SharedPreferenceUtil spUtil = ((App) App.getContext()).getSharedPreferenceUtil();

    private static int[] daysOfWeek = new int[]{Calendar.MONDAY,Calendar.TUESDAY,Calendar.WEDNESDAY,Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY,SUNDAY};

    public static PendingIntent getPendingIntent (Context context,int id,Intent intent) {
        return PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void setSilent (Context context,boolean sound_enable,Schedule schedule) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (!sound_enable) {
            if (spUtil.getRunningId() == -1 || spUtil.getRunningId() == schedule.getId()) {
                spUtil.enable(true);
                audioManager.setRingerMode(spUtil.isVib() ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT);
                spUtil.setRunningId(schedule.getId());
                Intent intent = new Intent(context,ListenerService.class);
                intent.putExtra("note",context.getString(R.string.auto_task) + schedule.getTo()+ context.getString(R.string.auto_task_over));
                context.startService(intent);
            }
        } else {
            if (spUtil.getRunningId() == schedule.getId()) {
                spUtil.setRunningId(-1);
                spUtil.enable(false);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                context.stopService(new Intent(context, ListenerService.class));
            }
        }
    }

    public static void startSchedule (Context context,Schedule schedule) {
        if (!schedule.getChecked().contains(1)) {
            cancelOldAlarm(context,schedule);
        } else {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar calNow = Calendar.getInstance();
            int currentDay = calNow.get(Calendar.DAY_OF_WEEK);
            if (currentDay == Calendar.SUNDAY) {
                currentDay = SUNDAY;
            }

            String[] start = schedule.getFrom().split(":");
            Calendar calFrom = Calendar.getInstance();
            calFrom.set(Calendar.DAY_OF_WEEK, currentDay);
            calFrom.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
            calFrom.set(Calendar.MINUTE, Integer.parseInt(start[1]));
            calFrom.set(Calendar.SECOND,0);
            String[] end = schedule.getTo().split(":");
            Calendar calEnd = Calendar.getInstance();
            calEnd.set(Calendar.DAY_OF_WEEK, currentDay);
            calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end[0]));
            calEnd.set(Calendar.MINUTE, Integer.parseInt(end[1]));
            calEnd.set(Calendar.SECOND,0);

            int daysTillNext = daysTillNext(schedule);
            boolean cross_night = false;

            cancelOldAlarm(context,schedule);

            if (calEnd.compareTo(calFrom) <= 0) {
                cross_night = true;
                if (daysEnabled(schedule).contains(currentDay == Calendar.MONDAY ? daysOfWeek[6] : currentDay - 1)) {
                    if (calNow.compareTo(calEnd) < 0) {
                        setSilent(context,false,schedule);
                    }
                } else {
                    calEnd.add(Calendar.DATE,1);
                }
            }

            if (!daysEnabled(schedule).contains(currentDay) || calFrom.compareTo(calNow) <= 0 && calEnd.compareTo(calNow) <= 0) {
                calFrom.add(Calendar.DATE,daysTillNext);
                calEnd.add(Calendar.DATE,daysTillNext);
            }

            if (daysEnabled(schedule).contains(currentDay) && calFrom.compareTo(calNow) <= 0) {
                setSilent(context,false,schedule);
                calFrom.add(Calendar.DATE,daysTillNext);
            }

            Intent startIntent = new Intent(context, TimeReceiver.class).setAction(App.AUTO_TIME_SCHEDULE);
            startIntent.putExtra("id",schedule.getId());
            startIntent.putExtra("sound_enable",false);
            startIntent.putExtra("cross_night",cross_night);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calFrom.getTimeInMillis(),getPendingIntent(context,schedule.getId(),startIntent));

            Intent endIntent = new Intent(context, TimeReceiver.class).setAction(App.AUTO_TIME_SCHEDULE);
            endIntent.putExtra("id",schedule.getId() + 1);
            endIntent.putExtra("sound_enable",true);
            endIntent.putExtra("cross_night",cross_night);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calEnd.getTimeInMillis(),getPendingIntent(context,schedule.getId() + 1,endIntent));
        }
    }

    public static int daysTillNext (Schedule schedule) {
        int daysTillNext = 7;
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (today == Calendar.SUNDAY) {
            today = SUNDAY;
        }

        if (schedule != null) {
            List<Integer> daysEnabled = daysEnabled(schedule);
            if (daysEnabled.size() > 0) {
                for (int dayEnabled : daysEnabled) {
                    if (dayEnabled < today) {
                        if (daysTillNext == 7) {
                            daysTillNext = 7 - (today - dayEnabled);
                        }
                    } else if (dayEnabled > today) {
                        daysTillNext = dayEnabled - today;
                        break;
                    }
                }
            }
        }

        return daysTillNext;
    }

    public static int daysTillNext (Schedule schedule,boolean cross_night) {
        if (cross_night) {
            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (today == Calendar.SUNDAY) {
                today = SUNDAY;
            }
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
        List<Integer> daysEnabled = new ArrayList<Integer>();
        for (int i = 0 ; i < schedule.getChecked().size() ; i++) {
            if (schedule.getChecked().get(i) == 1) {
                daysEnabled.add(daysOfWeek[i]);
            }
        }

        return daysEnabled;
    }

    public static void cancelOldAlarm(Context context,Schedule schedule) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent temp = new Intent(context,TimeReceiver.class).setAction(App.AUTO_TIME_SCHEDULE);
        PendingIntent cancel = getPendingIntent(context,schedule.getId(),temp);
        cancel.cancel();
        alarmManager.cancel(cancel);
        PendingIntent newCancel = getPendingIntent(context,schedule.getId() + 1,temp);
        newCancel.cancel();
        alarmManager.cancel(newCancel);
        if (spUtil.getRunningId() != -1 && spUtil.getRunningId() == schedule.getId() && spUtil.isUsable()) {
            spUtil.setRunningId(-1);
            context.stopService(new Intent(context,ListenerService.class));
        }
    }

    public static void pauseAlarm (Context context,Schedule schedule) {
        cancelOldAlarm(context, schedule);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calNow = Calendar.getInstance();
        int currentDay = calNow.get(Calendar.DAY_OF_WEEK);
        if (currentDay == Calendar.SUNDAY) {
            currentDay = SUNDAY;
        }

        String[] start = schedule.getFrom().split(":");
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(Calendar.DAY_OF_WEEK, currentDay);
        calFrom.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
        calFrom.set(Calendar.MINUTE, Integer.parseInt(start[1]));
        calFrom.set(Calendar.SECOND, 0);
        String[] end = schedule.getTo().split(":");
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.DAY_OF_WEEK, currentDay);
        calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end[0]));
        calEnd.set(Calendar.MINUTE, Integer.parseInt(end[1]));
        calEnd.set(Calendar.SECOND, 0);

        if (calEnd.compareTo(calFrom) <= 0) {
            calEnd.add(Calendar.DATE, 1);
        }

        calEnd.add(Calendar.HOUR_OF_DAY, 1);
        Intent intent = new Intent(context, TimeReceiver.class).setAction(App.AUTO_TIME_SCHEDULE);
        intent.putExtra("notify", true);
        PendingIntent pi = PendingIntent.getBroadcast(context, -2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calEnd.getTimeInMillis(),pi);
        context.startService(new Intent(context,ListenerService.class).putExtra("note",context.getString(R.string.auto_task) + calEnd.get(Calendar.HOUR_OF_DAY) + ":" + calEnd.get(Calendar.MINUTE) +context.getString(R.string.auto_task_over)));
    }
}
