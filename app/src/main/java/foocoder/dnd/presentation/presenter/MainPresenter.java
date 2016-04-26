package foocoder.dnd.presentation.presenter;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.view.SettingView;

@PerActivity
public class MainPresenter {

    private SettingView view;

    @Inject
    public MainPresenter() {

    }

    public void setView(@NonNull SettingView view) {
        this.view = view;
    }
}
