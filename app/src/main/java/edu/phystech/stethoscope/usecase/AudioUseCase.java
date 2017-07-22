package edu.phystech.stethoscope.usecase;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import edu.phystech.stethoscope.data.DataManager;
import edu.phystech.stethoscope.domain.Audio;
import edu.phystech.stethoscope.domain.Person;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AudioUseCase {

    private DataManager dataManager;

    @Inject
    public AudioUseCase(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Single<List<Audio>> getAudioByPersonId(final long personId) {
        return Single.fromCallable(new Callable<Person>() {
            @Override
            public Person call() throws Exception {
                return dataManager.getPersonById(personId);
            }
        }).map(new Function<Person, List<Audio>>() {
            @Override
            public List<Audio> apply(@NonNull Person person) throws Exception {
                return dataManager.getAudiosByPerson(person);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Audio> saveAudio(final long personId, final boolean isHeart, final int point,
                                   final int number, final String filePath) {
        return Single.fromCallable(new Callable<Audio>() {
            @Override
            public Audio call() throws Exception {
                return dataManager.saveAudio(personId, isHeart, point, number, filePath);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}