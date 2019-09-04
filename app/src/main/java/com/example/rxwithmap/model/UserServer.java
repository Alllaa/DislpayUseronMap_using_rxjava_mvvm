package com.example.rxwithmap.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.rxwithmap.rest.ApiService;
import com.example.rxwithmap.rest.RestClient;
import com.google.gson.JsonElement;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.observers.ResourceSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UserServer {

    private static UserServer userServer;

    public static UserServer getInstance() {
        if (userServer == null) {
            userServer = new UserServer();
        }
        return userServer;
    }

    public Single<ListUsers> getData()
    {
        ApiService apiService = RestClient.getClient().create(ApiService.class);
        return apiService.getUsers();
    }
}
