package edu.phystech.stethoscope.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.R;

public class SelectRecordTypeActivity extends AppCompatActivity {

    public static final String PERSON_ID_EXTRA = "PERSON_ID_EXTRA";
    private long personId;

    @BindView(R.id.heart)
    ImageView heart;
    @BindView(R.id.lungs)
    ImageView lungs;

    public static Intent getCallingIntent(Context context, long personId) {
        Intent intent = new Intent(context, SelectRecordTypeActivity.class);
        intent.putExtra(PERSON_ID_EXTRA, personId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_type);
        personId = getIntent().getExtras().getLong(PERSON_ID_EXTRA);
        ButterKnife.bind(this);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(HeartRecordActivity.getCallingIntent(SelectRecordTypeActivity.this, personId));
            }
        });
        lungs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LungRecordActivity.getCallingIntent(SelectRecordTypeActivity.this, personId));
            }
        });
    }
}
