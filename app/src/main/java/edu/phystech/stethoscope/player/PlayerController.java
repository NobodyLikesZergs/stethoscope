package edu.phystech.stethoscope.player;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

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

    private volatile boolean active = false;
    private Disposable audioDisposable = Disposables.disposed();
    private AudioUseCase audioUseCase;
    private AudioManager am;
    private WeakReference<PlaytimeCallback> playtimeCallback;

    @Inject
    public PlayerController(AudioUseCase audioUseCase, Context context) {
        this.audioUseCase = audioUseCase;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean isActive() {
        return active;
    }

    public void setPlayTimeCallBack(PlaytimeCallback playTimeCallBack) {
        this.playtimeCallback = new WeakReference<PlaytimeCallback>(playTimeCallBack);
    }

    public void notifyCallback(long time) {
        if (playtimeCallback.get() != null) {
            playtimeCallback.get().playtimeUpdated(time);
        }
    }

    public void startRecord(long personId, int point, int number) {
        if (active) return;
        active = true;
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+SAVE_DIR);
        dir.mkdir();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + SAVE_DIR + "/personId_" + personId + "_point_" + point + "_recnum_" + number;
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
        audioUseCase.saveAudio(personId, true, point, number, filePath).subscribe(
                new SingleObserver<Audio>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        audioDisposable = d;
                    }

                    @Override
                    public void onSuccess(@NonNull final Audio audio) {
                                    new Thread(new RecordRunnable(audio.getFilePath())).start();
                        am.startBluetoothSco();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void stopRecord() {
        active = false;
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
    }

    public void startPlaying(String file) {
        if (active) return;
        new Thread(new PlayRunnable(file)).start();
        active = true;
    }

    public void stopPlaying() {
        active = false;
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
                    AudioFormat.CHANNEL_IN_STEREO, MediaRecorder.AudioEncoder.AMR_NB, buffersize);
            AudioTrack atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, RATE,
                    AudioFormat.CHANNEL_IN_STEREO, MediaRecorder.AudioEncoder.AMR_NB, buffersize,
                    AudioTrack.MODE_STREAM);
            atrack.setPlaybackRate(RATE);
            byte[] buffer = new byte[buffersize];
            arec.startRecording();
            atrack.play();
            long time = System.currentTimeMillis();
            notifyCallback(0);
            try {
                FileOutputStream os = new FileOutputStream(filePath+".pcm");
                while(active) {
                    if (System.currentTimeMillis() - time > 1000) {
                        notifyCallback(System.currentTimeMillis() - time);
                    }
                    arec.read(buffer, 0, buffersize);
                    atrack.write(buffer, 0, buffer.length);
                    os.write(buffer, 0, buffer.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            am.stopBluetoothSco();
            atrack.release();
            arec.release();
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
                    AudioFormat.CHANNEL_IN_STEREO, MediaRecorder.AudioEncoder.AMR_NB, buffersize,
                    AudioTrack.MODE_STREAM);
            atrack.setPlaybackRate(RATE);
            byte[] buffer = new byte[buffersize];
            atrack.play();
            long time = System.currentTimeMillis();
            notifyCallback(0);
            try {
                FileInputStream is = new FileInputStream(filePath+".pcm");
                while(active && is.read(buffer, 0, buffersize) > 1) {
                    atrack.write(buffer, 0, buffer.length);
                    if (System.currentTimeMillis() - time > 1000) {
                        notifyCallback(System.currentTimeMillis() - time);
                    }
                }
                notifyCallback(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            atrack.release();
        }
    }

}
