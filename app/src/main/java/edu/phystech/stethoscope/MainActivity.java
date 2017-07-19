package edu.phystech.stethoscope;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 302;
//    boolean permissionToRecordAccepted = false;
//    boolean permissionToStoreAccepted = false;
//
//    boolean alreadyStarted = false;
//
//
////    BluetoothHeadset mBluetoothHeadset;
//
//    // Get the default adapter
////    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
////    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
////        public void onServiceConnected(int profile, BluetoothProfile proxy) {
////            if (profile == BluetoothProfile.HEADSET) {
////                Log.i("BT", "onServiceConnected");
////                mBluetoothHeadset = (BluetoothHeadset) proxy;
////                if (mBluetoothHeadset.startVoiceRecognition(mBluetoothHeadset
////                        .getConnectedDevices().get(0))) {
////                    Log.d("bt", "recognize");
////                } else
////                    Log.d("bt", "no recognize");
////                }
////
////            }
//
////        public void onServiceDisconnected(int profile) {
//
////            Log.i("BT", "onServiceDisConnected");
////            if (profile == BluetoothProfile.HEADSET) {
////
////                mBluetoothHeadset = null;
////            }
////        }
////    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
////        if (mBluetoothAdapter == null) {
////            return;
////        }
////        if (!mBluetoothAdapter.isEnabled()) {
////            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableBtIntent, 1);
////        }
////
////
////        mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);
//        String[] permission = {Manifest.permission.RECORD_AUDIO};
//        if (!permissionToRecordAccepted) {
//            ActivityCompat.requestPermissions(this, permission, REQUEST_RECORD_AUDIO_PERMISSION);
//        }
//
//
//    }
//
//    private void mainActions() {
////        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
////        registerReceiver(new BroadcastReceiver() {
////            @Override
////            public void onReceive(Context context, Intent intent) {
////                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
////                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
//                    if (permissionToRecordAccepted && !alreadyStarted) {
//                        alreadyStarted = true;
//                        final MediaRecorder mRecorder = new MediaRecorder();
//                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                        Log.e("OLOLOG", "save in: " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.3gp");
//                        mRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.3gp");
//                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//                        try {
//                            mRecorder.prepare();
//                        } catch (IOException e) {
//                            Log.e("OLOLOG", "prepare() failed");
//                            e.printStackTrace();
//                        }
//                        mRecorder.start();
//                        final MediaPlayer mPlayer = new MediaPlayer();
//                        try {
//                            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.3gp");
//                            mPlayer.setDataSource(fis.getFD());
//                        } catch (IOException e) {
//                            Log.e("OLOLOG", "mPlayer setDataSource() failed");
//                            e.printStackTrace();
//                        }
//                        mPlayer.prepareAsync();
//                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
//                                mPlayer.start();
//                            }
//                        });
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(7000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                mRecorder.stop();
//                                mRecorder.release();
//                                Log.e("OLOLOG", "done?");
////                                am.stopBluetoothSco();
//                            }
//                        }).start();
////                        unregisterReceiver(this);
//
////                    } else {
//////                        am.stopBluetoothSco();
////                    }
//                }
//
////            }
////        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
////        am.startBluetoothSco();
//
//        Log.d("OLOLOG", "starting bluetooth");
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                if (!permissionToRecordAccepted) finish();
//                else {
//                    String[] permission2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                    if (!permissionToStoreAccepted) {
//                        Log.e("OLOLOG", "requesting");
//                        ActivityCompat.requestPermissions(this, permission2, REQUEST_EXTERNAL_STORAGE_PERMISSION);
//                    }
//                }
//                break;
//            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
//                permissionToStoreAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                if (!permissionToStoreAccepted) finish();
//                else {
//                    someTest();
//                }
//                break;
//        }
//
//    }
//
//    private void someTest() {
//        new Thread(new Runnable() {
//            static final int bufferSize = 200000;
//            final short[] buffer = new short[bufferSize];
//            short[] readBuffer = new short[bufferSize];
//
//            public void run() {
////                isRecording = true;
//                android.os.Process.setThreadPriority
//                        (android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
//
//                int buffersize = AudioRecord.getMinBufferSize(11025,
//                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                        AudioFormat.ENCODING_PCM_16BIT);
//
//
//                AudioRecord arec = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                        11025,
//                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                        MediaRecorder.AudioEncoder.AMR_NB,
//                        buffersize);
//                AudioTrack atrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                        11025,
//                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                        MediaRecorder.AudioEncoder.AMR_NB,
//                        buffersize,
//                        AudioTrack.MODE_STREAM);
//
//                Log.d("AUDIO", "sample rate = : " + arec.getSampleRate());
//
//                atrack.setPlaybackRate(11025);
//
//                byte[] buffer = new byte[buffersize];
//                arec.startRecording();
//                atrack.play();
//                Equalizer eq = new Equalizer(0, atrack.getAudioSessionId());
//                short rangeLow = eq.getBandLevelRange()[0];
//                short rangeHigh = eq.getBandLevelRange()[1];
//                short bands = eq.getNumberOfBands();
//                Log.i("OLOLOG", "rh: " + rangeLow + " rl: " + rangeHigh + " num: " + bands);
//                for (short i = 0; i < bands; i++) {
////                    if (i==100) continue;
//                    eq.setBandLevel(i, rangeHigh);
//                }
//                eq.setEnabled(true);
//                while (true) {
//                    arec.read(buffer, 0, buffersize);
//                    atrack.write(buffer, 0, buffer.length);
//                }
//
//            }
//        }).start();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
//    }
}
