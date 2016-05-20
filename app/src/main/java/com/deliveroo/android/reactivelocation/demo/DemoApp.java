package com.deliveroo.android.reactivelocation.demo;

import android.app.Application;

import com.deliveroo.android.reactivelocation.ReactiveModule;
import com.deliveroo.android.reactivelocation.demo.di.AppComponent;
import com.deliveroo.android.reactivelocation.demo.di.AppModule;
import com.deliveroo.android.reactivelocation.demo.di.DaggerAppComponent;
import com.deliveroo.android.reactivelocation.permissions.PermissionsModule;
import com.deliveroo.android.reactivelocation.wallet.ProductionWalletEnvironment;
import com.deliveroo.android.reactivelocation.wallet.TestWalletEnvironment;
import com.deliveroo.android.reactivelocation.wallet.WalletEnvironment;

/**
 * Created by evelina on 20/05/2016.
 */

public class DemoApp extends Application {


    private static AppComponent appComponent;

    @Override public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .permissionsModule(new PermissionsModule(this))
                .reactiveModule(new ReactiveModule(walletEnvironment()))
                .build();

        appComponent.inject(this);
    }

    public static AppComponent appComponent() {
        return appComponent;
    }

    private WalletEnvironment walletEnvironment() {
        return BuildConfig.DEBUG ? new TestWalletEnvironment() : new ProductionWalletEnvironment();
    }
}
