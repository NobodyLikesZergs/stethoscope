package edu.phystech.stethoscope.di;

import javax.inject.Singleton;

import dagger.Component;
import edu.phystech.stethoscope.MainActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
