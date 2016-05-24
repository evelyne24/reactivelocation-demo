package com.deliveroo.android.reactivelocation.demo;

import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deliveroo.android.reactivelocation.ReactivePlayServices;
import com.deliveroo.android.reactivelocation.demo.di.ActivityModule;
import com.google.android.gms.location.LocationRequest;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static butterknife.ButterKnife.findById;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class MainActivity extends AppCompatActivity {

    @Inject ReactivePlayServices playServices;

    @Bind(R.id.location_status) TextView locationStatusView;
    @Bind(R.id.address_status) TextView addressStatusView;
    @Bind(R.id.wallet_status) TextView walletStatusView;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.try_again) View tryAgainView;
    @Bind(R.id.try_again_button) Button tryAgainButton;

    private final LocationRequest locationRequest = createLocationRequest();

    private Subscription walletSubscription = Subscriptions.empty();
    private Subscription locationSubscription = Subscriptions.empty();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DemoApp.appComponent()
                .plus(new ActivityModule(this))
                .inject(this);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findById(this, R.id.toolbar));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }

    @Override protected void onStart() {
        super.onStart();
        locationSubscription = startLocationUpdates();
        walletSubscription = checkWalletReady();
    }

    @Override protected void onStop() {
        locationSubscription.unsubscribe();
        walletSubscription.unsubscribe();
        super.onStop();
    }

    @Override protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private Subscription startLocationUpdates() {
        showProgress(true);

        // The normal way, where we wait indefinitely for a location fix...
        //return playServices.location().requestLocationUpdates(locationRequest)
        // or the better flavor, where we timeout after a given time to the last known location, if exists, or an error
        return playServices.location().requestLocationUpdates(locationRequest, 10, SECONDS, mainThread())
                .subscribeOn(mainThread())
                .observeOn(mainThread())
                .doOnNext(onLocationUpdated())
                .doOnError(onLocationError())
                .observeOn(io())
                .flatMap(reverseGeocodeLocation())
                .observeOn(mainThread())
                .subscribe(onAddressFound(), onAddressError());
    }

    private Action1<Throwable> onAddressError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                showError(addressStatusView, throwable);
            }
        };
    }

    private Action1<Address> onAddressFound() {
        return new Action1<Address>() {
            @Override public void call(Address address) {
                addressStatusView.setTextColor(Color.DKGRAY);
                addressStatusView.setText(address.toString());
            }
        };
    }

    private Func1<Location, Observable<Address>> reverseGeocodeLocation() {
        return new Func1<Location, Observable<Address>>() {
            @Override public Observable<Address> call(Location location) {
                return playServices.geocoder().reverseGeocodeLocation(location, 1);
            }
        };
    }

    private Action1<Throwable> onLocationError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                showError(locationStatusView, throwable);
                tryAgain();
            }
        };
    }

    private Action1<Location> onLocationUpdated() {
        return new Action1<Location>() {
            @Override public void call(Location location) {
                showProgress(false);
                locationStatusView.setTextColor(Color.BLUE);
                locationStatusView.setText(getString(R.string.location_found, location.getLatitude(), location.getLongitude()));
                addressStatusView.setTextColor(Color.DKGRAY);
                addressStatusView.setText(getString(R.string.start_geocoding));
            }
        };
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(50)
                .setNumUpdates(1);
    }

    private void tryAgain() {
        showProgress(false);
        tryAgainView.setVisibility(VISIBLE);
    }

    @OnClick(R.id.try_again_button)
    void onTryAgainClicked(View view) {
        tryAgainView.setVisibility(GONE);

        locationSubscription.unsubscribe();
        locationSubscription = startLocationUpdates();

        walletSubscription.unsubscribe();
        walletSubscription = checkWalletReady();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? VISIBLE : GONE);
    }

    private Subscription checkWalletReady() {
        return playServices.wallet().isReadyToPay()
                .subscribe(new Action1<Boolean>() {
                    @Override public void call(Boolean ready) {
                        if (ready) {
                            walletStatusView.setText(R.string.wallet_ready);
                            walletStatusView.setTextColor(Color.BLUE);
                        } else {
                            walletStatusView.setText(R.string.wallet_not_ready);
                            walletStatusView.setTextColor(Color.RED);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        showError(walletStatusView, throwable);
                    }
                });
    }

    private void showError(TextView textView, Throwable throwable) {
        textView.setText(throwable.getMessage());
        textView.setTextColor(Color.RED);
    }

}
