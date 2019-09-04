package com.example.rxwithmap.rest;

import com.example.rxwithmap.model.ListUsers;
import com.example.rxwithmap.model.User;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("bins/157ykb")
    Single<ListUsers> getUsers();
}
