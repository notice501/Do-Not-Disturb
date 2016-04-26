package foocoder.dnd.presentation.presenter;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.view.MainSettingView;
import foocoder.dnd.utils.SharedPreferenceUtil;

@PerActivity
public class MainPresenter extends Presenter<MainSettingView> {

    @Inject
    SharedPreferenceUtil spUtil;

    @Inject
    public MainPresenter() {}

    @Override
    public void start() {

    }

    @Override
    public void bindView(@NonNull MainSettingView view) {
        super.bindView(view);

        view.changeState(false);
        view.changeAutoRecoverState(spUtil.isRecoverable());
        view.changeLauncherState(spUtil.isLaunched());
        view.changeTimerState(spUtil.isStarted());
        view.changeVibrationState(spUtil.isVib());
        view.changeRepetitionState(spUtil.isRepeated());
    }
}
