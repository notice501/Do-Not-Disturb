package foocoder.dnd.presentation.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import foocoder.dnd.R;
import foocoder.dnd.domain.Contact;
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

import static foocoder.dnd.presentation.view.activity.ContactListActivity.RESULT_FINISH;
import static foocoder.dnd.utils.SharedPreferenceUtil.START;

public final class MainActivity extends BaseActivity<MainComponent> implements MainSettingView {

    @Inject
    App global;

    @Inject
    SharedPreferenceUtil sp;

    @BindView(R.id.container)
    FrameLayout container;

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
    RelativeLayout more_setting;

    @Inject
    MainPresenter mainPresenter;

    private AudioManager audio;

    private List<Schedule> schList;

    private ArrayList<Contact> contacts;

    private ScheduleAdapter adapter;

    private TimeDialog addDialog;

    private TimeDialog itemDialog;

    private static final int WHITE_LIST = 100;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private boolean open_setting;

    private int originBottom = 0;

    private int originEnd = 0;

    private int originalHeight = 0;

    private int heightToScale;

    private int heightToMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listener = new MyListener();
        open_setting = false;

        ButterKnife.bind(this);
        getComponent().inject(this);
        mainPresenter.bindView(this);
        sp.registerListener(listener);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        schList = global.getScheduleList();
        contacts = new ArrayList<>();



        timepicker.setVisibility(sp.isQuiet() ? View.VISIBLE : View.GONE);

        adapter = new ScheduleAdapter(this, schList);
        schs.setAdapter(adapter);
        schs.setDivider(null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_FINISH:
                contacts = data.getParcelableArrayListExtra("contacts");
            case RESULT_OK:
                contacts = data.getParcelableArrayListExtra("contacts");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (global.getOriginalHeight() == 0) {
            originalHeight = more_setting.getMeasuredHeight();
            global.setOriginalHeight(originalHeight);
        } else {
            originalHeight = global.getOriginalHeight();
        }

        if (sp.getHeightToMove() == 0) {
            heightToMove = (int) (container.getMeasuredHeight() * 0.7);
            sp.setHeightToMove(heightToMove);
        } else {
            heightToMove = sp.getHeightToMove();
        }

        if (sp.getHeightToScale() == 0) {
            int[] points = new int[2];
            fab.getLocationOnScreen(points);
            heightToScale = (int) (container.getMeasuredHeight() * 0.3f * 0.37f - (points[1] + fab.getMeasuredHeight() / 2));
            sp.setHeightToScale(heightToScale);
        } else {
            heightToScale = sp.getHeightToScale();
        }

        initAnim();
    }

    private void initAnim() {
        final ObjectAnimator scaleOpen = ObjectAnimator.ofPropertyValuesHolder(fab, PropertyValuesHolder.ofFloat("scaleX", 1f, 0.3f)
                , PropertyValuesHolder.ofFloat("scaleY", 1f, 0.3f)
                , PropertyValuesHolder.ofFloat("translationY", 1f, heightToScale));
        scaleOpen.setDuration(220);

        final RotateAnimation rotateOpen = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateOpen.setDuration(335);
        rotateOpen.setFillAfter(true);

        final ValueAnimator openSetting = ValueAnimator.ofInt(0, heightToMove);
        openSetting.setDuration(300);
        openSetting.setInterpolator(new LinearInterpolator());
        openSetting.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                up_fab.setImageResource(R.drawable.up_arrow);
                up_fab.requestLayout();
                scaleOpen.start();
                up_fab.startAnimation(rotateOpen);
            }
        });
        openSetting.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = more_setting.getLayoutParams();
                params.height = originalHeight + (Integer) animation.getAnimatedValue();

                ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) up_fab.getLayoutParams();
                params1.setMargins(0, 0, originEnd, originBottom + (Integer) animation.getAnimatedValue());

                more_setting.requestLayout();
            }
        });

        final RotateAnimation rotateClose = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateClose.setDuration(335);
        rotateClose.setFillAfter(true);

        final ValueAnimator closeSetting = ValueAnimator.ofInt(0, heightToMove);
        closeSetting.setDuration(300);
        closeSetting.setInterpolator(new LinearInterpolator());
        closeSetting.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                up_fab.setImageResource(R.drawable.down_arrow);
                up_fab.requestLayout();
                scaleOpen.reverse();
                up_fab.startAnimation(rotateClose);
            }
        });
        closeSetting.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = more_setting.getLayoutParams();
                params.height = originalHeight + heightToMove - (Integer) animation.getAnimatedValue();

                ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) up_fab.getLayoutParams();
                params1.setMargins(0, 0, originEnd, originBottom - (Integer) animation.getAnimatedValue());

                more_setting.requestLayout();
            }
        });

        up_fab.setOnClickListener(view -> {
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

        LayoutTransition transition = new LayoutTransition();
        ObjectAnimator appear = ObjectAnimator.ofPropertyValuesHolder(schs, PropertyValuesHolder.ofFloat("scaleY", 0, 1));
        appear.setDuration(100);
        appear.setInterpolator(new OvershootInterpolator());
        transition.setAnimator(LayoutTransition.APPEARING, appear);
        more_setting.setLayoutTransition(transition);
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
        Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
        intent.putParcelableArrayListExtra("contacts", contacts);
        intent.putExtra("firstLoad", contacts.size() == 0);
        startActivityForResult(intent, WHITE_LIST);
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
