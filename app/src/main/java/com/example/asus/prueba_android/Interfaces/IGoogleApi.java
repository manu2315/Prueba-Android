package com.example.asus.prueba_android.Interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApi {
    @GET
    Call<String> getDataFromGoogleApi(@Url String url);
}
