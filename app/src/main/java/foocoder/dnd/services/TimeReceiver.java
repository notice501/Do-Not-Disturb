package foocoder.dnd.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import java.util.Calendar;

import foocoder.dnd.presentation.App;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;

public class TimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        App settings = (App) App.getContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        SharedPreferenceUtil spUtil = settings.getSharedPreferenceUtil();
        Bundle extras = intent.getExtras();

        if (App.AUTO_TIME_SCHEDULE.equals(intent.getAction())) {
            if (extras.getBoolean("notify", false)) {
                Schedule schedule = settings.getScheduleById(spUtil.getRunningId());
                AlarmUtil.setSilent(context,true,schedule);
                AlarmUtil.startSchedule(context,schedule);
            } else {
                int _id = extras.getInt("id",0);
                boolean sound_enable = extras.getBoolean("sound_enable",false);
                boolean cross_night = extras.getBoolean("cross_night",false);

                Intent newIntent = new Intent(context,TimeReceiver.class).setAction(App.AUTO_TIME_SCHEDULE);
                newIntent.putExtras(extras);
                PendingIntent pendingIntent = AlarmUtil.getPendingIntent(context,_id,newIntent);

                Calendar calendar = Calendar.getInstance();
                int daysTillNext;
                Schedule schedule;
                if (_id % 2 != 0) {
                    schedule = settings.getScheduleById(_id - 1);
                    daysTillNext = AlarmUtil.daysTillNext(schedule,cross_night);
                } else {
                    schedule = settings.getScheduleById(_id);
                    daysTillNext = AlarmUtil.daysTillNext(schedule);
                }
                calendar.add(Calendar.DATE, daysTillNext);

                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

                AlarmUtil.setSilent(context,sound_enable,schedule);
            }
        } else {
            if (audioManager.getRingerMode() > 1) {
                if (spUtil.isUsable()) {
                    spUtil.setRunningId(-1);
                    spUtil.enable(false);
                    context.stopService(new Intent(context,ListenerService.class));
                }
            }
        }
    }
}
