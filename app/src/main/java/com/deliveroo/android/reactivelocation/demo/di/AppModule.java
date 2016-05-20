package com.deliveroo.android.reactivelocation.demo.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by evelina on 20/05/2016.
 */
@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides @Singleton Application application() {
        return application;
    }
}
