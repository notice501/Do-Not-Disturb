package foocoder.dnd.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import foocoder.dnd.presentation.App;

public class CallService extends Service{

    private App app;

    private Timer timer;

    private ScanTask task;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = (App) App.getContext();
        timer = new Timer(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(task != null){
            task.cancel();
        } else {
            task = new ScanTask();
        }
        timer.schedule(task,100,1000);

        return super.onStartCommand(intent, flags, startId);
    }

    private class ScanTask extends TimerTask{

        @Override
        public void run() {
            app.scanNumbers();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        task.cancel();
    }
}
