package com.deliveroo.android.reactivelocation.demo.di;

import com.deliveroo.android.reactivelocation.ReactiveModule;
import com.deliveroo.android.reactivelocation.demo.DemoApp;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by evelina on 20/05/2016.
 */
@Singleton
@Component(modules = {AppModule.class, ReactiveModule.class})
public interface AppComponent {

    ActivityComponent plus(ActivityModule module);

    void inject(DemoApp app);
}
