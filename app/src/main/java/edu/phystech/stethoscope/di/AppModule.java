package edu.phystech.stethoscope.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.phystech.stethoscope.player.DeviceConnectionManager;

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
}
