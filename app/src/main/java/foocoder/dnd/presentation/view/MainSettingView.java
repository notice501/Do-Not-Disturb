package foocoder.dnd.presentation.view;

/**
 * Created by xuechi.
 * Time: 2016 四月 27 02:59
 * Project: dnd
 */
public interface MainSettingView {

    void changeState(boolean enabled);

    void changeAutoRecoverState(boolean enabled);

    void changeLauncherState(boolean enabled);

    void changeTimerState(boolean enabled);

    void changeVibrationState(boolean enabled);

    void changeRepetitionState(boolean enabled);

    void showSchedules();
}
