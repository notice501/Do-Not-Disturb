package foocoder.dnd.presentation.view.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foocoder.dnd.R;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.presentation.internal.di.components.ContactComponent;
import foocoder.dnd.presentation.presenter.ContactListPresenter;
import foocoder.dnd.presentation.view.ContactListView;
import foocoder.dnd.presentation.view.adapter.ContactAdapter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class ContactListActivity extends BaseActivity implements ContactListView {

    public static final int RESULT_FINISH = 999;

    @BindView(R.id.contacts)
    ListView listView;

    @BindView(R.id.search)
    EditText search;

    @BindView(R.id.cancel)
    Button cancel;

    @BindView(R.id.save)
    Button save;

    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;

    @Inject
    ContactAdapter adapter;

    @Inject
    ContactListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list);

        ButterKnife.bind(this);
        getComponent().inject(this);
        presenter.bindView(this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            CheckBox check = (CheckBox) view.findViewById(R.id.check);
            check.toggle();
            presenter.onContactCheckedChange(check.isChecked(), position);
        });

        Subscription subscription = RxTextView.textChanges(search)
                .skip(1)
                .debounce(100, MILLISECONDS)
                .switchMap(charSequence -> Observable.create(subscriber -> {
                    final String name = charSequence.toString().toLowerCase();

                    try {
                        presenter.onTextChanged(name);
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }

                    subscriber.onNext(charSequence);
                }).subscribeOn(Schedulers.newThread()))
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> "An error occurred")
                .subscribe(charSequence -> {
                    Timber.d("name to be searched %s", charSequence);
                    adapter.notifyDataSetChanged();
                });

        presenter.start();

        addSubscriptionsForUnbinding(subscription);
    }

    @OnClick(R.id.save)
    @SuppressWarnings("unused")
    void onSaveClick() {
        presenter.saveContactList();

        finish();
    }

    @OnClick(R.id.cancel)
    @SuppressWarnings("unused")
    void onCancelClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();

        super.onDestroy();
    }

    @Override
    public void showContacts(List<Contact> contactList) {
        adapter.changeData(contactList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startLoading() {
        progressWheel.spin();
    }

    @Override
    public void stopLoading() {
        if (progressWheel.isSpinning()) {
            progressWheel.stopSpinning();
        }
    }

    private ContactComponent getComponent() {
        return getApplicationComponent().plus(getActivityModule());
    }
}
