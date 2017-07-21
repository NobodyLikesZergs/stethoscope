package edu.phystech.stethoscope;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_person_activity);
        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
