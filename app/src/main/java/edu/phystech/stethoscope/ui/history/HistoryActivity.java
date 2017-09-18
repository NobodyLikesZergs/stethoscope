package edu.phystech.stethoscope.ui.history;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.MyApplication;
import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Person;
import edu.phystech.stethoscope.ui.RecordActivity;
import edu.phystech.stethoscope.usecase.PersonUseCase;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class HistoryActivity extends AppCompatActivity {

    @BindView(R.id.person_recycler_view)
    RecyclerView personRecyclerView;
    @BindView(R.id.trash_button)
    ImageView trashButton;
    @BindView(R.id.unselect_all)
    ImageView unselectAllImageView;
    @BindView(R.id.selected_num)
    TextView selectedNumTextView;

    private PersonRecyclerViewAdapter adapter;

    @Inject
    PersonUseCase personUseCase;

    private Disposable personListDisposable = Disposables.disposed();
    private Disposable removePersonListDisposable = Disposables.disposed();

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
        adapter = new PersonRecyclerViewAdapter(new PersonRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClick(List<Integer> selectionList, long id) {
                startActivity(RecordActivity.getCallingIntent(HistoryActivity.this, id));
            }
        }, new PersonRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClick(List<Integer> selectionList, long id) {
                if (selectionList.isEmpty()) {
                    selectedNumTextView.setVisibility(View.GONE);
                    unselectAllImageView.setVisibility(View.GONE);
                    trashButton.setVisibility(View.GONE);
                } else {
                    selectedNumTextView.setText("Выбрано: " + selectionList.size());
                    selectedNumTextView.setVisibility(View.VISIBLE);
                    unselectAllImageView.setVisibility(View.VISIBLE);
                    trashButton.setVisibility(View.VISIBLE);
                }
            }
        });
        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePersons();
            }
        });
        personRecyclerView.setAdapter(adapter);
        unselectAllImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.unselectAll();
            }
        });
    }

    private void unsubscribe() {
        if (!personListDisposable.isDisposed()) {
            personListDisposable.dispose();
        }
        if (!removePersonListDisposable.isDisposed()) {
            removePersonListDisposable.dispose();
        }
    }

    private void removePersons() {
        List<Long> selectedId = adapter.getSelectedIdList();
        if (adapter.getSelectedPersons().isEmpty()) {
            Toast.makeText(this, "Выберите записи", Toast.LENGTH_SHORT).show();
            return;
        }
        if (removePersonListDisposable.isDisposed()) {
            removePersonListDisposable.dispose();
        }
        personUseCase.removePersonList(selectedId).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                removePersonListDisposable = d;
            }

            @Override
            public void onSuccess(@NonNull Integer aInteger) {
                adapter.removeSelected();
                Toast.makeText(HistoryActivity.this, "Данные удалены", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                Toast.makeText(HistoryActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            }
        });
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
