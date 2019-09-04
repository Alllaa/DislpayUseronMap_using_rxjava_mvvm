package com.example.rxwithmap.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rxwithmap.model.ListUsers;
import com.example.rxwithmap.model.UserServer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    private UserServer userServer;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<ListUsers> responseLiveData = new MutableLiveData<>();

    public MainViewModel() {
        userServer = UserServer.getInstance();
    }

    public MutableLiveData<ListUsers> getResponseLiveData() {
        return responseLiveData;
    }


    public void makeObserve() {
        disposables.add(userServer.getData().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeWith(new DisposableSingleObserver<ListUsers>() {
                            @Override
                            public void onSuccess(ListUsers listUsers) {
                                responseLiveData.setValue(listUsers);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("Error",e.getMessage()+"");

                            }
                        }));
    }

}
