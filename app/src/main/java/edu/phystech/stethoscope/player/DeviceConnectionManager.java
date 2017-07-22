package edu.phystech.stethoscope.player;

import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import javax.inject.Inject;

import io.reactivex.subjects.BehaviorSubject;

public class DeviceConnectionManager {

    public static int STATE_CONNECTED = 1520;
    public static int STATE_DISCONNECTED = 1292;
    BehaviorSubject<Integer> stateSubject = BehaviorSubject.createDefault(STATE_DISCONNECTED);

    private BluetoothManager bm;
    Context context;
    BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnection();
        }
    };
    private BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (proxy.getConnectedDevices().isEmpty()) {
                stateSubject.onNext(STATE_DISCONNECTED);
            } else {
                stateSubject.onNext(STATE_CONNECTED);
            }
            bm.getAdapter().closeProfileProxy(profile, proxy);
        }

        @Override
        public void onServiceDisconnected(int profile) {
        }
    };

    @Inject
    public DeviceConnectionManager(Context context) {
        this.context = context;
        this.bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public void startListening() {
        checkConnection();
        context.registerReceiver(stateReceiver,
                new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED));
    }

    public void checkConnection() {
        if (bm.getAdapter() == null || !bm.getAdapter().isEnabled()) {
            stateSubject.onNext(STATE_DISCONNECTED);
        }
        bm.getAdapter().getProfileProxy(context, listener, BluetoothProfile.HEADSET);
    }

    public BehaviorSubject<Integer> getStateSubject() {
        return stateSubject;
    }
}