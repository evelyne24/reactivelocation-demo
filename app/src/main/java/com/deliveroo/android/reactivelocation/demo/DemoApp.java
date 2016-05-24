package com.deliveroo.android.reactivelocation.demo;

import android.app.Application;
import android.os.StrictMode;

import com.deliveroo.android.reactivelocation.ReactiveModule;
import com.deliveroo.android.reactivelocation.demo.di.AppComponent;
import com.deliveroo.android.reactivelocation.demo.di.AppModule;
import com.deliveroo.android.reactivelocation.demo.di.DaggerAppComponent;
import com.deliveroo.android.reactivelocation.wallet.environments.ProductionWalletEnvironment;
import com.deliveroo.android.reactivelocation.wallet.environments.TestWalletEnvironment;
import com.deliveroo.android.reactivelocation.wallet.environments.WalletEnvironment;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by evelina on 20/05/2016.
 */

public class DemoApp extends Application {


    private static AppComponent appComponent;

    @Override public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .reactiveModule(new ReactiveModule(this, walletEnvironment()))
                .build();

        appComponent.inject(this);

        //LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
            StrictMode.enableDefaults();
        }
    }

    public static AppComponent appComponent() {
        return appComponent;
    }

    private WalletEnvironment walletEnvironment() {
        return BuildConfig.DEBUG ? new TestWalletEnvironment() : new ProductionWalletEnvironment();
    }
}
