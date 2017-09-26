package edu.phystech.stethoscope.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.phystech.stethoscope.data.DataBaseHelper;
import edu.phystech.stethoscope.data.DataManager;
import edu.phystech.stethoscope.player.DeviceConnectionManager;
import edu.phystech.stethoscope.player.PlayerController;
import edu.phystech.stethoscope.usecase.AudioUseCase;
import edu.phystech.stethoscope.usecase.PersonUseCase;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    DeviceConnectionManager provideDeviceConnectionManager() {
        return new DeviceConnectionManager(application);
    }

    @Provides
    @Singleton
    DataBaseHelper provideDataBaseHelper(Context context) {
        return new DataBaseHelper(context);
    }

    @Provides
    @Singleton
    DataManager provideDataManager(DataBaseHelper dataBaseHelper) {
        return new DataManager(dataBaseHelper);
    }

    @Provides
    @Singleton
    PersonUseCase providePersonUseCase(DataManager dataManager) {
        return new PersonUseCase(dataManager);
    }

    @Provides
    @Singleton
    AudioUseCase provideAudioUseCase(DataManager dataManager) {
        return new AudioUseCase(dataManager);
    }

    @Provides
    @Singleton
    PlayerController providePlayerController(AudioUseCase audioUseCase, Context context) {
        return new PlayerController(audioUseCase, context);
    }
}
