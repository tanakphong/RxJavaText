package com.deverdie.rxjavatext;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;

import java.util.concurrent.Callable;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends LocalizationActivity implements View.OnClickListener {

    private static final String TAG = "dlg";
    private BluetoothSPP bt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setDefaultLanguage("th");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         bt = new BluetoothSPP(getApplicationContext());

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }


        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Connection lost"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect"
                        , Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
//                    bt.disconnected();
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        findViewById(R.id.btn_th).setOnClickListener(this);
        findViewById(R.id.btn_en).setOnClickListener(this);

        doSomething();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_ANDROID);
            setup();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    public void setup() {

        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });

//        Intent intent = new Intent(MainActivity.this, DeviceList.class);
//        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public String getUserId() throws InterruptedException {
        Thread.sleep(5000);
        return "1234";
    }

    public void doSomething() {

        final TextView tvUserId = findViewById(R.id.text);

        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getUserId();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("Rx", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Rx", "onError: "+e.getMessage());
                        tvUserId.setText("-");
                    }

                    @Override
                    public void onNext(String id) {
                        Log.d("Rx", "onNext");
                        tvUserId.setText(id);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_en) {
            setLanguage("en");
        } else if (id == R.id.btn_th) {
            setLanguage("th");
        }
    }
}
