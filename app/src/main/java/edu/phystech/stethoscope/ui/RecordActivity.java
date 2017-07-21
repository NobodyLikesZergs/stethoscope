package edu.phystech.stethoscope.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.R;

public class RecordActivity extends AppCompatActivity {

    public static String PERSON_ID_EXTRA = "PERSON_ID_EXTRA";

    @BindView(R.id.point1red)
    ImageView point1red;
    @BindView(R.id.point2red)
    ImageView point2red;
    @BindView(R.id.point3red)
    ImageView point3red;
    @BindView(R.id.point4red)
    ImageView point4red;
    @BindView(R.id.point5red)
    ImageView point5red;
    @BindView(R.id.point1white)
    ImageView point1white;
    @BindView(R.id.point2white)
    ImageView point2white;
    @BindView(R.id.point3white)
    ImageView point3white;
    @BindView(R.id.point4white)
    ImageView point4white;
    @BindView(R.id.point5white)
    ImageView point5white;

    ImageView[] redPoints;
    ImageView[] whitePoints;

    private int currentPoint = 0;
    private boolean isRecording = false;

    public static Intent getCallingIntent(Context context, long personId) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra(PERSON_ID_EXTRA, personId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);
        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this);
        redPoints = new ImageView[]{point1red, point2red, point3red, point4red, point5red};
        whitePoints = new ImageView[]{point1white, point2white, point3white, point4white, point5white};
        for (int i = 0; i < 5; i++) {
            final int finalI = i;
            whitePoints[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isRecording) {
                        setSelectedPoint(finalI);
                    }
                }
            });
        }
    }

    private void setSelectedPoint(int newSelectedPoint) {
        redPoints[currentPoint].setVisibility(View.GONE);
        whitePoints[currentPoint].setVisibility(View.VISIBLE);
        whitePoints[newSelectedPoint].setVisibility(View.GONE);
        redPoints[newSelectedPoint].setVisibility(View.VISIBLE);
        currentPoint = newSelectedPoint;
    }
}
