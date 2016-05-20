package com.deliveroo.android.reactivelocation.demo.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by evelina on 20/05/2016.
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides Activity activity() {
        return activity;
    }
}
