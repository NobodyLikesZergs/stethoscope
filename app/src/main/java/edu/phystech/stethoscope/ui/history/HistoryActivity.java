package edu.phystech.stethoscope.ui.history;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.MyApplication;
import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Person;
import edu.phystech.stethoscope.usecase.PersonUseCase;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class HistoryActivity extends AppCompatActivity {

    @BindView(R.id.person_recycler_view)
    RecyclerView personRecyclerView;

    private PersonRecyclerViewAdapter adapter;

    @Inject
    PersonUseCase personUseCase;

    private Disposable personListDisposable = Disposables.disposed();
    private SingleObserver<List<Person>> personListObserver = new SingleObserver<List<Person>>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            personListDisposable = d;
        }

        @Override
        public void onSuccess(@NonNull List<Person> persons) {
            adapter.setPersons(persons);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        initViews();
        inject();
        subscribe();
    }

    private void inject() {
        MyApplication.getAppComponent().inject(this);
    }

    private void initViews() {
        ButterKnife.bind(this);
        personRecyclerView.setHasFixedSize(true);
        personRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PersonRecyclerViewAdapter();
        personRecyclerView.setAdapter(adapter);
    }

    private void unsubscribe() {
        if (!personListDisposable.isDisposed()) {
            personListDisposable.dispose();
        }
    }

    private void subscribe() {
        unsubscribe();
        personUseCase.getAllPersons().subscribe(personListObserver);
    }

    @Override
    protected void onDestroy() {
        unsubscribe();
        super.onDestroy();
    }
}
