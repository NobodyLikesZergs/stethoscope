package edu.phystech.stethoscope.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import edu.phystech.stethoscope.domain.Audio;
import edu.phystech.stethoscope.usecase.AudioUseCase;
import edu.phystech.stethoscope.utils.AudioUtils;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class PlayerController {

    public static final int RATE = 11025;
    private final static String SAVE_DIR = "/stet";

    protected volatile boolean shouldBeInProgress = false;
    protected volatile boolean inProgress = false;
    protected Disposable audioDisposable = Disposables.disposed();
    protected AudioUseCase audioUseCase;
    private AudioManager am;
    private PlaytimeCallback playtimeCallback;
    private Context context;

    @Inject
    public PlayerController(AudioUseCase audioUseCase, Context context) {
        this.audioUseCase = audioUseCase;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.context = context;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setPlayTimeCallBack(PlaytimeCallback playTimeCallBack) {
        this.playtimeCallback = playTimeCallBack;
    }

    public void detachCallback() {
        this.playtimeCallback = null;
    }

    public void notifyCallback(long time) {
        if (playtimeCallback != null) {
            playtimeCallback.playtimeUpdated(time);
        }
    }

    public boolean startRecord(long personId, int point, int number, boolean isHeart) {
        if (inProgress) {
            return false;
        }
        shouldBeInProgress = true;
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+SAVE_DIR);
        dir.mkdir();
        String place = isHeart ? "_heart_" : "_lungs_";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + SAVE_DIR + "/personId_" + personId + place+ "_point_" + point + "_recnum_" + number;
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
        audioUseCase.saveAudio(personId, isHeart, point, number, filePath).subscribe(
                new SingleObserver<Audio>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        audioDisposable = d;
                    }

                    @Override
                    public void onSuccess(@NonNull final Audio audio) {
                        registerReceiverForRecord(audio);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }
        );
        return true;
    }

    protected void registerReceiverForRecord (final Audio audio) {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.d("OLOLOG", "Audio SCO state: " + state);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    if (!inProgress) {
                        inProgress = true;
                        new Thread(new RecordRunnable(audio.getFilePath())).start();
                    }
                    context.unregisterReceiver(this);
                    }
                }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
//        if (am.isBluetoothScoOn()) {
            am.stopBluetoothSco();
//        }
        am.startBluetoothSco();
    }

    public void stopRecord() {
        shouldBeInProgress = false;
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
    }

    public boolean startPlaying(String file) {
        if (inProgress) {
            return false;
        }
        shouldBeInProgress = true;
        inProgress = true;
        new Thread(new PlayRunnable(file)).start();
        return true;
    }

    public void stopPlaying() {
        shouldBeInProgress = false;
    }

    public interface PlaytimeCallback {
        void playtimeUpdated(long playTime);
    }

    private class RecordRunnable implements Runnable {

        private String filePath;

        RecordRunnable(String filePath) {
            this.filePath = filePath;
        }

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int buffersize = AudioRecord.getMinBufferSize(RATE, AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord arec = new AudioRecord(MediaRecorder.AudioSource.MIC, RATE,
                    AudioFormat.CHANNEL_IN_MONO, MediaRecorder.AudioEncoder.AMR_NB, buffersize);
            AudioTrack atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, RATE,
                    AudioFormat.CHANNEL_OUT_MONO, MediaRecorder.AudioEncoder.AMR_NB, buffersize,
                    AudioTrack.MODE_STREAM);
            atrack.setPlaybackRate(RATE);
            byte[] buffer = new byte[buffersize];
            arec.startRecording();
            atrack.play();
            long time = System.currentTimeMillis();
            notifyCallback(0);
            try {
                FileOutputStream os = new FileOutputStream(filePath+".pcm");
                while(shouldBeInProgress) {
                    if (System.currentTimeMillis() - time > 1000) {
                        notifyCallback(System.currentTimeMillis() - time);
                    }
                    arec.read(buffer, 0, buffersize);
                    atrack.write(buffer, 0, buffer.length);
                    os.write(buffer, 0, buffer.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                atrack.release();
                arec.release();
                inProgress = false;
            }
            am.stopBluetoothSco();
            atrack.release();
            arec.release();
            inProgress = false;
            try {
                AudioUtils.rawToWave(new File(filePath+".pcm"), new File(filePath+".wav"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PlayRunnable implements Runnable {

        private String filePath;

        PlayRunnable(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int buffersize = AudioRecord.getMinBufferSize(RATE, AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, RATE,
                    AudioFormat.CHANNEL_OUT_MONO, MediaRecorder.AudioEncoder.AMR_NB, buffersize,
                    AudioTrack.MODE_STREAM);
            atrack.setPlaybackRate(RATE);
            byte[] buffer = new byte[buffersize];
            atrack.play();
            long time = System.currentTimeMillis();
            notifyCallback(0);
            try {
                FileInputStream is = new FileInputStream(filePath+".pcm");
                while(shouldBeInProgress && is.read(buffer, 0, buffersize) > 1) {
                    atrack.write(buffer, 0, buffer.length);
                    if (System.currentTimeMillis() - time > 1000) {
                        notifyCallback(System.currentTimeMillis() - time);
                    }
                }
                notifyCallback(-1);
            } catch (IOException e) {
                atrack.release();
                e.printStackTrace();
                inProgress = false;
            }
            atrack.release();
            inProgress = false;
        }
    }

}
