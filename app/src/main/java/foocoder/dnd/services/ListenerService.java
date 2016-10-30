package foocoder.dnd.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import javax.inject.Inject;

import foocoder.dnd.R;
import foocoder.dnd.presentation.view.activity.MainActivity;

public class ListenerService extends Service {

    private static final int NOTIFICATION_ID = 1024;
    @Inject
    NotificationManager manager;
    private String note;
    private Notification.Builder builder;

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

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        note = intent.getStringExtra("note");
        if (note != null) {
            showNotification();
        } else {
            stopForeground(true);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {
        Intent i = new Intent(this, MainActivity.class);

        Notification notification = builder.setContentIntent(null)
                .setContentTitle(getString(R.string.running_hint))
                .setContentText(note)
                .setSmallIcon(R.drawable.volume_off)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }
}
