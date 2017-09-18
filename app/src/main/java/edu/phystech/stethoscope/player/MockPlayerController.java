package edu.phystech.stethoscope.player;

import android.content.Context;

import edu.phystech.stethoscope.domain.Audio;
import edu.phystech.stethoscope.usecase.AudioUseCase;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MockPlayerController extends PlayerController {
    public MockPlayerController(AudioUseCase audioUseCase, Context context) {
        super(audioUseCase, context);
    }

    @Override
    protected void registerReceiverForRecord(Audio audio) {
        inProgress = true;
    }

    @Override
    public boolean startRecord(long personId, int point, int number) {
        if (inProgress) {
            return false;
        }
        shouldBeInProgress = true;
        if (!audioDisposable.isDisposed()) {
            audioDisposable.dispose();
        }
        audioUseCase.saveAudio(personId, true, point, number, "").subscribe(
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

    @Override
    public boolean startPlaying(String file) {
        if (inProgress) {
            return false;
        }
        shouldBeInProgress = true;
        inProgress = true;
        return true;
    }

    @Override
    public void stopPlaying() {
        super.stopPlaying();
        inProgress = false;
    }

    @Override
    public void stopRecord() {
        super.stopRecord();
        inProgress = false;
    }
}
