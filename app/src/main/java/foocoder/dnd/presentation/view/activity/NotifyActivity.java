package foocoder.dnd.presentation.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

import foocoder.dnd.R;
import foocoder.dnd.presentation.App;
import foocoder.dnd.services.ListenerService;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;

public class NotifyActivity extends Activity {

    SharedPreferenceUtil spUtil;

    AudioManager audioManager;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify);

        spUtil = App.getContext().getSharedPreferenceUtil();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        app = App.getContext();

        findViewById(R.id.btn1).setOnClickListener(v -> finish());
        findViewById(R.id.add_1).setVisibility(spUtil.isStarted() ? View.GONE : View.VISIBLE);
        findViewById(R.id.add_1).setOnClickListener(v -> {
            AlarmUtil.pauseAlarm(NotifyActivity.this, null);
            finish();
        });
        findViewById(R.id.stop).setOnClickListener(v -> {
            if (spUtil.isStarted()) {
                sendBroadcast(new Intent().setAction(App.START_STOP_ACTION));
            } else if (spUtil.isUsable()) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                spUtil.enable(false);
                spUtil.setRunningId(-1);
            }
            stopService(new Intent(NotifyActivity.this, ListenerService.class));
            finish();
        });
    }
}
