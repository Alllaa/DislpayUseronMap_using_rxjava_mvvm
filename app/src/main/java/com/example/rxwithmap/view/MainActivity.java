package com.example.rxwithmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.rxwithmap.R;
import com.example.rxwithmap.model.ListUsers;
import com.example.rxwithmap.model.User;
import com.example.rxwithmap.viewmodel.MainViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private Marker marker;
    private MarkerOptions markerOptions;
    private GoogleMap map;
    private MapView mapView;
    List<User> users = new ArrayList<>();
    MainViewModel mainViewModel;
    private Disposable disposables = new CompositeDisposable();
    TextView textView;
    ImageView imageView;
    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = getLayoutInflater().inflate(R.layout.marker,null);
        textView = view.findViewById(R.id.name);
        imageView = view.findViewById(R.id.header_image);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mainViewModel = new MainViewModel();
        mainViewModel.makeObserve();
        mainViewModel.getResponseLiveData().observe(this, new Observer<ListUsers>() {
            @Override
            public void onChanged(ListUsers listUsers) {
                users.addAll(listUsers.getUsers());
                Log.d("LOL", users.get(0).getName());
                mapView.getMapAsync(MainActivity.this);

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for (User user : users) {

            LatLng latLng = new LatLng(user.getLat(), user.getLng());
            markerOptions = new MarkerOptions();

            getBitmapObservable(user.getName(),user.getImage()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getBitmap(latLng));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 11));

                return false;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle("MapViewBundleKey");
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle("MapViewBundleKey", mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }


    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    public Bitmap createCustomMarker(Context context, Bitmap image, String name) {
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
        TextView t = v.findViewById(R.id.name);
        t.setText(name);
        ImageView markerImage = (ImageView) v.findViewById(R.id.header_image);
        markerImage.setImageBitmap(image);
        v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }



    private SingleObserver<Bitmap> getBitmap(final LatLng latLng) {
        return new SingleObserver<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables = d;
            }

            @Override
            public void onSuccess(Bitmap bitmap) {

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                markerOptions.position(latLng).flat(false);
                marker = map.addMarker(markerOptions);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "onError: " + e.getMessage());

            }
        };
    }

    private Single<Bitmap> getBitmapObservable(final String name,final String url) {

        return Single.create(new SingleOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(final SingleEmitter<Bitmap> emitter) throws Exception {
                if (!emitter.isDisposed())
                {
                    Bitmap myBitmap = Glide.with(getApplicationContext())
                            .load(url)
                            .asBitmap()
                            .into(40, 40)
                            .get();

                    emitter.onSuccess(myBitmap);
                }
            }
        }).map(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) throws Exception {

//                imageView.setImageBitmap(bitmap);
                Bitmap bitmap1 = createCustomMarker(getApplicationContext(),bitmap,name);

                return bitmap1;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();

    }
}
