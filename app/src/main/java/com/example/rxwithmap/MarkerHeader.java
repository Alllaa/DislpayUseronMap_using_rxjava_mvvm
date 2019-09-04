package com.example.rxwithmap;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MarkerHeader implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater = null;
    private View view;
    private TextView textView;
    private ImageView img;
    private Marker mMarker;
    private String url;
    private Disposable disposables = new CompositeDisposable();


    public MarkerHeader(LayoutInflater layoutInflater) {
        this.inflater = layoutInflater;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (view == null) {
            view = inflater.inflate(R.layout.marker, null);
        }


        textView = view.findViewById(R.id.name);
        img = view.findViewById(R.id.header_image);

      url = (String) marker.getTag();


        textView.setText(marker.getTitle());

//        getBitmapObservable()
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getBitmap());
//​
        return view;

    }
//    private Single<Bitmap> getBitmapObservable() {
//​
//        return Single.create(new SingleOnSubscribe<Bitmap>() {
//            @Override
//            public void subscribe(final SingleEmitter<Bitmap> emitter) throws Exception {
//                if (!emitter.isDisposed()) {
//                    Glide.with(img.getContext()).load(url)
//                            .asBitmap()
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    //Log.d("ready", "ddd");
//                                    if (resource != null) {
//                                        //Log.d("not null", "----");
//                                        emitter.onSuccess(resource);
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//    }
//​
//    private SingleObserver<Bitmap> getBitmap() {
//        return new SingleObserver<Bitmap>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                disposables = d;
//            }
//​
//            @Override
//            public void onSuccess(Bitmap s) {
//                img.setImageBitmap(s);
//                mMarker.showInfoWindow();
//                disposables.dispose();
//            }
//​
//            @Override
//            public void onError(Throwable e) {
//                Log.e("TAG", "onError: " + e.getMessage());
//            }
//        };
//    }
}

