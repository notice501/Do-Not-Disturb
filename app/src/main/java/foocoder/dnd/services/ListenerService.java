package foocoder.dnd.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import foocoder.dnd.R;
import foocoder.dnd.presentation.view.activity.NotifyActivity;

public class ListenerService extends Service {

    private String noteStr;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        noteStr = "";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        noteStr = intent.getStringExtra("note");
        startService();

        return START_STICKY;
    }

    private void startService() {
        Intent i = new Intent(this, NotifyActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification note = new Notification.Builder(this)
                .setContentIntent(pi)
                .setContentTitle(getString(R.string.running_hint))
                .setContentText(noteStr)
                .setSmallIcon(R.drawable.volume_off)
                .setWhen(java.lang.System.currentTimeMillis())
//                .build();
                .getNotification();
        startForeground(1337, note);
    }
}
