package com.deverdie.rxjavatext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "dlg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        doSomething();
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
}
