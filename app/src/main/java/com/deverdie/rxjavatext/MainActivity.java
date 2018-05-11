package com.deverdie.rxjavatext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "dlg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doSomething();
    }

    public String getNameList(int index) throws IndexOutOfBoundsException {
        List<String> nameList = Arrays.asList("Cupcake",
                "Donut",
                "Eclair",
                "Froyo",
                "Gingerbread",
                "Honeycomb",
                "Ice Cream Sandwich",
                "Jelly Bean",
                "Kitkat",
                "Lollipop",
                "Marshmallow",
                "Nugat");
        return nameList.get(index);
    }

    public void doSomething() {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getNameList(100);
            }
        })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("Rx", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Rx", "onError");
                    }

                    @Override
                    public void onNext(String name) {
                        Log.d("Rx", "Name: " + name);
                    }
                });
    }
}
