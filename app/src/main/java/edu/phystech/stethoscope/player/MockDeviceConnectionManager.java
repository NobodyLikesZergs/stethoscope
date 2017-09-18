package edu.phystech.stethoscope.player;

import android.content.Context;

public class MockDeviceConnectionManager extends DeviceConnectionManager {
    public MockDeviceConnectionManager(Context context) {
        super(context);
    }

    @Override
    public void startListening() {
        getStateSubject().onNext(STATE_CONNECTED);
    }

    @Override
    public void checkConnection() {
        getStateSubject().onNext(STATE_CONNECTED);
    }
}
