package edu.phystech.stethoscope.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.MyApplication;
import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Person;
import edu.phystech.stethoscope.usecase.PersonUseCase;
import edu.phystech.stethoscope.utils.Utils;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class CreatePersonActivity extends AppCompatActivity {

    @BindView(R.id.comment_field)
    EditText comment;
    @BindView(R.id.name_field)
    EditText name;
    @BindView(R.id.last_name_field)
    EditText lastName;
    @BindView(R.id.age_field)
    EditText age;
    @BindView(R.id.save_button)
    Button save;
    @BindView(R.id.cancel_button)
    Button cancel;

    @Inject
    PersonUseCase personUseCase;

    private Disposable saveDisposable = Disposables.disposed();
    private SingleObserver<Person> saveObserver = new SingleObserver<Person>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            saveDisposable = d;
        }

        @Override
        public void onSuccess(@NonNull Person p) {
            startActivity(SelectRecordTypeActivity.getCallingIntent(CreatePersonActivity.this,
                    p.getId()));
            finish();
        }


        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        setContentView(R.layout.new_person_layout);
        initViews();
    }

    private void inject() {
        MyApplication.getAppComponent().inject(this);
    }

    private void initViews() {
        ButterKnife.bind(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validData()) {
                    unsubscribe();
                    personUseCase.savePerson(name.getText().toString(), lastName.getText().toString(),
                            Integer.parseInt(age.getText().toString()),
                            Utils.dateToString(new DateTime()), comment.getText().toString())
                            .subscribe(saveObserver);
                } else {
                    Toast.makeText(CreatePersonActivity.this, R.string.fill_fields, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void unsubscribe() {
        if (!saveDisposable.isDisposed()) {
            saveDisposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        unsubscribe();
        super.onDestroy();
    }

    private boolean validData() {
        boolean nameIsEmpty = name.getText().toString().equals("");
        boolean lastNameIsEmpty = lastName.getText().toString().equals("");
        boolean ageIsEmpty = age.getText().toString().equals("");
        return !(nameIsEmpty || lastNameIsEmpty || ageIsEmpty);
    }
}
