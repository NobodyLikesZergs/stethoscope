package edu.phystech.stethoscope.ui.audios;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.phystech.stethoscope.MyApplication;
import edu.phystech.stethoscope.R;
import edu.phystech.stethoscope.domain.Audio;
import edu.phystech.stethoscope.usecase.AudioUseCase;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class AudiosActivity extends AppCompatActivity {

    public static final String PERSON_EXTRA = "PERSON_ID_EXTRA";
    private long personId;

    @BindView(R.id.audio_recycler_view)
    RecyclerView audioRecyclerView;
    @BindView(R.id.share_button)
    ImageView shareButton;

    private AudiosRecyclerViewAdapter adapter;

    @Inject
    AudioUseCase audioUseCase;

    private Disposable audioDisposable = Disposables.disposed();
    private SingleObserver<List<Audio>> audioObserver = new SingleObserver<List<Audio>>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            audioDisposable = d;
        }

        @Override
        public void onSuccess(@NonNull List<Audio> audios) {
            adapter.setAudios(audios);
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }
    };

    public static Intent getCallingIntent(long personId, Context context) {
        Intent intent = new Intent(context, AudiosActivity.class);
        intent.putExtra(PERSON_EXTRA, personId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audios_layout);
        this.personId = getIntent().getExtras().getLong(PERSON_EXTRA);
        initViews();
        inject();
        subscribe();
    }

    private void inject() {
        MyApplication.getAppComponent().inject(this);
    }

    private void initViews() {
        ButterKnife.bind(this);
        audioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudiosRecyclerViewAdapter();
        audioRecyclerView.setAdapter(adapter);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email();
            }
        });
    }



    private void unsubscribe() {
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
    }

    private void subscribe() {
        unsubscribe();
        audioUseCase.getAudioByPersonId(this.personId).subscribe(audioObserver);
    }

    @Override
    protected void onDestroy() {
        unsubscribe();
        super.onDestroy();
    }

    public void email() {
        if (adapter.getSelectedAudios().isEmpty()) {
            Toast.makeText(this, "Выберите записи", Toast.LENGTH_SHORT).show();
            return;
        }
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{""});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "audio files");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "audios");
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        for (Audio audio: adapter.getSelectedAudios())
        {
            File fileIn = new File(audio.getFilePath()+".wav");
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
