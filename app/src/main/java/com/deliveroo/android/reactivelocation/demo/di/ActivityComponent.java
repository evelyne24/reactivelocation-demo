package com.deliveroo.android.reactivelocation.demo.di;

import com.deliveroo.android.reactivelocation.demo.MainActivity;

import dagger.Subcomponent;

/**
 * Created by evelina on 20/05/2016.
 */
@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);
}
