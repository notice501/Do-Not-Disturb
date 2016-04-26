package foocoder.dnd.presentation.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.components.MainComponent;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.presenter.MainPresenter;
import foocoder.dnd.presentation.view.MainSettingView;
import foocoder.dnd.presentation.view.adapter.ScheduleAdapter;
import foocoder.dnd.services.ListenerService;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;
import foocoder.dnd.views.TimeDialog;
import rx.Subscription;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static foocoder.dnd.utils.SharedPreferenceUtil.START;

public final class MainActivity extends BaseActivity<MainComponent> implements MainSettingView {

    @Inject
    App global;

    @Inject
    SharedPreferenceUtil sp;

    @BindView(Window.ID_ANDROID_CONTENT)
    ViewGroup container;

    @BindView(R.id.big_fab)
    ImageButton fab;

    @BindView(R.id.recover_swh)
    Switch recover_swh;

    @BindView(R.id.launch)
    Switch launch;

    @BindView(R.id.timer_swh)
    Switch timer_swh;

    @BindView(R.id.add)
    ImageButton add;

    @BindView(R.id.schs)
    ListView schs;

    @BindView(R.id.vibr_switch)
    Switch vibr_switch;

    @BindView(R.id.white_btn)
    ImageView white_btn;

    @BindView(R.id.repeat_switch)
    Switch repeat_switch;

    @BindView(R.id.up_fab)
    ImageButton up_fab;

    @BindView(R.id.timepicker_rl)
    RelativeLayout timepicker;

    @BindView(R.id.more_setting)
    ViewGroup more_setting;

    @Inject
    MainPresenter mainPresenter;

    private AudioManager audio;

    private List<Schedule> schList;

    private ScheduleAdapter adapter;

    private TimeDialog addDialog;

    private TimeDialog itemDialog;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private boolean open_setting;

    private int originBottom = 0;

    private int originEnd = 0;

    private int originalHeight = 0;

    private int heightToScale;

    private int heightToMove;
    private float translationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        getComponent().inject(this);
        mainPresenter.bindView(this);

        listener = new MyListener();
        open_setting = false;

        sp.registerListener(listener);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        schList = global.getScheduleList();

        timepicker.setVisibility(sp.isQuiet() ? View.VISIBLE : View.GONE);

        adapter = new ScheduleAdapter(this, schList);
        schs.setAdapter(adapter);
        schs.setDivider(null);

        getWindow().getDecorView().post(() -> {
            originalHeight = more_setting.getHeight();
            int[] points = new int[2];
            fab.getLocationOnScreen(points);
            translationY = container.getHeight() - TypedValue.applyDimension(COMPLEX_UNIT_DIP, 118, getResources().getDisplayMetrics());
            heightToMove = (int) (translationY - container.getHeight() * 0.7);
            heightToScale = (int) (container.getHeight() * 0.3f * 0.37f - (points[1] + fab.getHeight() / 2));
            more_setting.setTranslationY(translationY);

            initAnim();
        });
    }

    @Override
    protected MainComponent getComponent() {
        return getApplicationComponent().add(new ActivityModule(this));
    }

    @Override
    protected void onDestroy() {
        sp.unregisterListener(listener);

        super.onDestroy();
    }

    @OnCheckedChanged(R.id.recover_swh)
    void onRecoverSwitchCheckedChange(CompoundButton buttonView, boolean isChecked) {
        sp.setRecover(isChecked);

        if (sp.isStarted()) {
            if (isChecked) {
                startService(new Intent(MainActivity.this, ListenerService.class).putExtra("note", getString(R.string.manual_auto_cancel)));
            }

        }
    }

    @OnCheckedChanged(R.id.launch)
    void onLaunchSwitchCheckedChange(CompoundButton buttonView, boolean isChecked) {
        sp.setLaunch(isChecked);

        if (isChecked) {
            if (sp.isStarted()) {
                if (sp.isVib()) {
                    audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            } else {
                if (sp.isQuiet()) {
//                            startService(new Intent(MainActivity.this,TimeService.class));
                }
            }
        } else {
            if (sp.isStarted()) {
                if (sp.isVib()) {
                    switch (audio.getRingerMode()) {
                        case AudioManager.RINGER_MODE_NORMAL:
                            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        case AudioManager.RINGER_MODE_VIBRATE:
                            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            } else {
                if (sp.isQuiet()) {
//                            stopService(new Intent(MainActivity.this,TimeService.class));
                }
            }
        }
    }

    @OnCheckedChanged(R.id.timer_swh)
    void onTimerSwitchCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sp.setQuiet(isChecked);
        timepicker.setVisibility(isChecked ? View.VISIBLE : View.GONE);

        if (isChecked) {
            if (!sp.isStarted()) {
                if (global.getScheduleList().size() > 0) {
                    for (Schedule schedule : global.getScheduleList()) {
                        AlarmUtil.startSchedule(MainActivity.this, schedule);
                    }
                }
            }
        } else {
            if (!sp.isStarted()) {
                if (global.getScheduleList().size() > 0) {
                    for (Schedule schedule : global.getScheduleList()) {
                        AlarmUtil.cancelOldAlarm(MainActivity.this, schedule);
//                                    stopService(new Intent(MainActivity.this,ListenerService.class));
                    }
                    sp.setRunningId(-1);
                    sp.enable(false);
                }
            }
        }
    }

    @OnCheckedChanged(R.id.vibr_switch)
    void onVibrateSwitchCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sp.setVib(isChecked);

        if (sp.isLaunched()) {
            if (isChecked) {
                if (sp.isStarted()) {
                    audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            } else {
                if (sp.isStarted()) {
                    if (audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            }
        }
    }

    @OnCheckedChanged(R.id.repeat_switch)
    void onRepeatSwitchCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sp.setRepeat(isChecked);
    }

    @OnItemClick(R.id.schs)
    void onScheduleListItemClick(AdapterView<?> parent, View view, int position, long id) {
        itemDialog = new TimeDialog(schList.get(position), MainActivity.this, new TimeDialog.OnCustomDialogListener() {
            @Override
            public void back(Schedule sch) {
                if (sch.isDel()) {
                    AlarmUtil.cancelOldAlarm(MainActivity.this, sch);
                    schList.remove(sch);
                    global.delSchedule(sch);
                    if (schList.size() == 0) {
                        sp.setRunningId(-1);
                    }
                } else {
                    schList.set(position, sch);
                    global.updateSchedule(sch);
                    if (!sp.isStarted()) {
                        AlarmUtil.startSchedule(MainActivity.this, sch);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        itemDialog.show();
    }

    @OnClick(R.id.add)
    void onAddClick() {
        addDialog = new TimeDialog(MainActivity.this, new TimeDialog.OnCustomDialogListener() {

            @Override
            public void back(Schedule sch) {
                if (sch.isDel()) {
                    schList.remove(sch);
                    global.delSchedule(sch);
                } else {
                    if (schList.size() >= 5) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle(getString(R.string.hint))
                                .setMessage(R.string.hint_desc)
                                .setPositiveButton(R.string.complete, (dialog, which) -> {
                                    dialog.dismiss();
                                }).create().show();
                        return;
                    }
                    int _id = sp.getId() + 2;
                    sch.setId(_id);
                    sp.setId(_id);
                    if (!sp.isStarted()) {
                        AlarmUtil.startSchedule(MainActivity.this, sch);
                    }
                    schList.add(sch);
                    global.saveSchedule(sch);
                }
                adapter.notifyDataSetChanged();
            }
        });
        addDialog.show();
    }

    @OnClick(R.id.big_fab)
    void onFabClick(View view) {
        sendBroadcast(new Intent().setAction(App.START_STOP_ACTION));
    }

    @OnClick(R.id.white_btn)
    void onWhiteListClick(View view) {
        ContactListActivity.start(this);
    }

    @Override
    public void changeState(boolean enabled) {
        fab.setImageResource(enabled ? R.drawable.shut12 : R.drawable.shut1);
    }

    @Override
    public void changeAutoRecoverState(boolean enabled) {
        recover_swh.setChecked(sp.isRecoverable());
    }

    @Override
    public void changeLauncherState(boolean enabled) {
        launch.setChecked(sp.isLaunched());
    }

    @Override
    public void changeTimerState(boolean enabled) {
        timer_swh.setChecked(sp.isQuiet());
    }

    @Override
    public void changeVibrationState(boolean enabled) {
        vibr_switch.setChecked(sp.isVib());
    }

    @Override
    public void changeRepetitionState(boolean enabled) {
        repeat_switch.setChecked(sp.isRepeated());
    }

    private void initAnim() {
        final ObjectAnimator scaleOpen = ObjectAnimator.ofPropertyValuesHolder(fab, PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.3f)
                , PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.3f)
                , PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1f, heightToScale));
        scaleOpen.setDuration(220);

        final RotateAnimation rotateOpen = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateOpen.setDuration(335);
        rotateOpen.setFillAfter(true);

        ObjectAnimator openSetting = ObjectAnimator.ofFloat(more_setting, "translationY", translationY, heightToMove);
        openSetting.setDuration(300);
        openSetting.setInterpolator(new LinearInterpolator());
        openSetting.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                up_fab.setImageResource(R.drawable.up_arrow);
                scaleOpen.start();
                up_fab.startAnimation(rotateOpen);
            }
        });

        final RotateAnimation rotateClose = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateClose.setDuration(335);
        rotateClose.setFillAfter(true);

        ObjectAnimator closeSetting = ObjectAnimator.ofFloat(more_setting, "translationY", heightToMove, translationY);
        closeSetting.setDuration(300);
        closeSetting.setInterpolator(new LinearInterpolator());
        closeSetting.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                up_fab.setImageResource(R.drawable.down_arrow);
                scaleOpen.reverse();
                up_fab.startAnimation(rotateClose);
            }
        });

        Subscription subscription = RxView.clicks(up_fab).subscribe(aVoid -> {
            originEnd = ((ViewGroup.MarginLayoutParams) up_fab.getLayoutParams()).rightMargin;
            originBottom = ((ViewGroup.MarginLayoutParams) up_fab.getLayoutParams()).bottomMargin;
            if (!open_setting) {
                open_setting = true;
                scaleOpen.setStartDelay(100);
                openSetting.start();
            } else {
                open_setting = false;
                scaleOpen.setStartDelay(0);
                closeSetting.start();
            }
        });
        addSubscriptionsForUnbinding(subscription);

        LayoutTransition transition = new LayoutTransition();
        ObjectAnimator appear = ObjectAnimator.ofPropertyValuesHolder(schs, PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1));
        appear.setDuration(100);
        appear.setInterpolator(new OvershootInterpolator());
        transition.setAnimator(LayoutTransition.APPEARING, appear);
        more_setting.setLayoutTransition(transition);
    }

    private class MyListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(START)) {
                AnimationDrawable animationDrawable;
                if (sharedPreferences.getBoolean(START, false)) {
                    fab.setImageResource(R.drawable.shutup);
                    animationDrawable = (AnimationDrawable) fab.getDrawable();
                    animationDrawable.start();
                } else {
                    fab.setImageResource(R.drawable.openup);
                    animationDrawable = (AnimationDrawable) fab.getDrawable();
                    animationDrawable.start();
                }
            }
        }
    }
}
