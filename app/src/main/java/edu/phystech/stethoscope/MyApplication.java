package edu.phystech.stethoscope;

import android.app.Application;

import edu.phystech.stethoscope.di.AppComponent;
import edu.phystech.stethoscope.di.AppModule;
import edu.phystech.stethoscope.di.DaggerAppComponent;

public class MyApplication extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        createComponent();
    }

    public void createComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

}
