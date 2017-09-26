package edu.phystech.stethoscope.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.MyApplication;
import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Audio;
import edu.phystech.stethoscope.player.DeviceConnectionManager;
import edu.phystech.stethoscope.player.PlayerController;
import edu.phystech.stethoscope.ui.audios.AudiosActivity;
import edu.phystech.stethoscope.usecase.AudioUseCase;
import edu.phystech.stethoscope.utils.Utils;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class HeartRecordActivity extends AppCompatActivity {

    public static final String PERSON_ID_EXTRA = "PERSON_ID_EXTRA";
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 846;
    public static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 523;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToStoreAccepted = false;

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
    @BindView(R.id.recordButton)
    FloatingActionButton recordButton;
    @BindView(R.id.play_button)
    FloatingActionButton playButton;
    @BindView(R.id.floatingActionButton2)
    FloatingActionButton listButton;
    @BindView(R.id.timer)
    TextView timer;

    @Inject
    PlayerController playerController;
    @Inject
    AudioUseCase audioUseCase;
    @Inject
    DeviceConnectionManager deviceConnectionManager;

    List<Audio> audioList = new ArrayList<>();
    private Disposable audioDisposable = Disposables.disposed();
    private SingleObserver<List<Audio>> audioObserver = new SingleObserver<List<Audio>>() {
        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
            audioDisposable = d;
        }

        @Override
        public void onSuccess(@io.reactivex.annotations.NonNull List<Audio> audios) {
            audioList = audios;
            applyWaitState();
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
            e.printStackTrace();
        }
    };

    private int state = DeviceConnectionManager.STATE_DISCONNECTED;

    private Disposable stateDisposable = Disposables.disposed();
    private Observer<Integer> stateObserver = new Observer<Integer>() {
        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
            stateDisposable = d;
        }

        @Override
        public void onNext(@io.reactivex.annotations.NonNull Integer integer) {
            state = integer;
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {

        }
    };

    ImageView[] redPoints;
    ImageView[] whitePoints;

    private long personId;
    private int currentPoint = 0;

    public static Intent getCallingIntent(Context context, long personId) {
        Intent intent = new Intent(context, HeartRecordActivity.class);
        intent.putExtra(PERSON_ID_EXTRA, personId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heart_record_layout);
        personId = getIntent().getExtras().getLong(PERSON_ID_EXTRA);
        inject();
        deviceConnectionManager.startListening();
        subscribeOnState();
        initViews();
        subscribeOnAudioList();
        playerController.setPlayTimeCallBack(new PlayerController.PlaytimeCallback() {
            @Override
            public void playtimeUpdated(final long playTime) {
                timer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (playTime < 0) {
                            applyWaitState();
                            playerController.stopRecord();
                        } else {
                            String text = "";
                            if (playTime/1000/60 < 10)
                                text+="0";
                            text += playTime/1000/60+":";
                            if (playTime/1000 < 10)
                                text+="0";
                            text += playTime/1000;
                            timer.setText(text);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerController.stopPlaying();
        playerController.detachCallback();
        unsubscribeFromAudioList();
        unsubscribeFromState();
    }

    private void inject() {
        MyApplication.getAppComponent().inject(this);
    }

    private void initViews() {
        ButterKnife.bind(this);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerController.stopPlaying();
                unsubscribeFromAudioList();
                unsubscribeFromState();
                startActivity(AudiosActivity.getCallingIntent(HeartRecordActivity.this, personId));
            }
        });
        redPoints = new ImageView[]{point1red, point2red, point3red, point4red, point5red};
        whitePoints = new ImageView[]{point1white, point2white, point3white, point4white, point5white};
        setPointsSelectable(false);
    }

    private void setPointsSelectable(final boolean selectable) {
        for (int i = 0; i < 5; i++) {
            final int finalI = i;
            whitePoints[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectable) {
                        setSelectedPoint(finalI);
                    }
                }
            });
        }
    }

    private void applyWaitState() {
        setPointsSelectable(true);
        recordButton.setAlpha(1f);
        recordButton.setImageResource(R.drawable.record_circle);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playerController.isInProgress()) {
                    deviceConnectionManager.checkConnection();
                    if (state == DeviceConnectionManager.STATE_CONNECTED) {
                        applyRecordState();
                        if (!playerController.startRecord(personId, currentPoint, Utils.getLastAudioNumWithPoint(audioList, currentPoint) + 1, true)) {
                            Toast.makeText(HeartRecordActivity.this, R.string.wait, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HeartRecordActivity.this, R.string.connect_device, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        playButton.setImageResource(R.drawable.back);
        if (Utils.hasAudiosWithPoint(audioList, currentPoint)) {
            playButton.setAlpha(1f);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!playerController.startPlaying(Utils.getLastAudioWithPoint(audioList, currentPoint).getFilePath())) {
                        Toast.makeText(HeartRecordActivity.this, R.string.wait, Toast.LENGTH_SHORT).show();
                    }
                    applyPlayState();
                }
            });
        } else {
            playButton.setAlpha(0.25f);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    private void applyRecordState() {
        setPointsSelectable(false);
        recordButton.setAlpha(1f);
        recordButton.setImageResource(R.drawable.pause_icon);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerController.stopRecord();
                subscribeOnAudioList();
            }
        });
        playButton.setAlpha(0.25f);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void applyPlayState() {
        setPointsSelectable(false);
        recordButton.setAlpha(0.25f);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerController.stopPlaying();
                applyWaitState();
            }
        });
        playButton.setImageResource(R.drawable.pause_icon);
    }

    private void setSelectedPoint(int newSelectedPoint) {
        redPoints[currentPoint].setVisibility(View.GONE);
        whitePoints[currentPoint].setVisibility(View.VISIBLE);
        whitePoints[newSelectedPoint].setVisibility(View.GONE);
        redPoints[newSelectedPoint].setVisibility(View.VISIBLE);
        currentPoint = newSelectedPoint;
        applyWaitState();
    }

    private void checkPermissions() {
        if (!permissionToRecordAccepted) {
            String[] permission = {Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, permission, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void subscribeOnAudioList() {
        unsubscribeFromAudioList();
        audioUseCase.getAudioByPersonId(personId, 1).subscribe(audioObserver);
    }

    private void unsubscribeFromAudioList() {
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!permissionToRecordAccepted) {
                    finish();
                } else {
                    String[] permission2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!permissionToStoreAccepted) {
                        ActivityCompat.requestPermissions(this, permission2, REQUEST_EXTERNAL_STORAGE_PERMISSION);
                    }
                }
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                permissionToStoreAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!permissionToStoreAccepted) finish();
                break;
        }
    }

    private void subscribeOnState() {
        unsubscribeFromState();
        deviceConnectionManager.getStateSubject().subscribe(stateObserver);
    }

    private void unsubscribeFromState() {
        if (!stateDisposable.isDisposed()) {
            stateDisposable.dispose();
        }
    }
}
