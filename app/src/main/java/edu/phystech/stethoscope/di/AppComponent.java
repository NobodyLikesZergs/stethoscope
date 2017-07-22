package edu.phystech.stethoscope.di;

import javax.inject.Singleton;

import dagger.Component;
import edu.phystech.stethoscope.ui.CreatePersonActivity;
import edu.phystech.stethoscope.ui.MainActivity;
import edu.phystech.stethoscope.ui.RecordActivity;
import edu.phystech.stethoscope.ui.audios.AudiosActivity;
import edu.phystech.stethoscope.ui.history.HistoryActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(CreatePersonActivity createPersonActivity);
    void inject(HistoryActivity historyActivity);
    void inject(RecordActivity recordActivity);
    void inject(AudiosActivity audiosActivity);
}
