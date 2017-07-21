package edu.phystech.stethoscope.usecase;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import edu.phystech.stethoscope.data.DataManager;
import edu.phystech.stethoscope.domain.Person;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PersonUseCase {

    DataManager dataManager;

    @Inject
    public PersonUseCase(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Single<Person> savePerson(final String firstName, final String lastName,
                                         final int age, final String date, final String comment) {
        return Single.fromCallable(new Callable<Person>() {
            @Override
            public Person call() throws Exception {
                return dataManager.savePerson(firstName, lastName, age, date, comment);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Person>> getAllPersons() {
        return Single.fromCallable(new Callable<List<Person>>() {
            @Override
            public List<Person> call() throws Exception {
                return dataManager.getAllPersons();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Person> getPersonById(final long id) {
        return Single.fromCallable(new Callable<Person>() {
            @Override
            public Person call() throws Exception {
                return dataManager.getPersonById(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
