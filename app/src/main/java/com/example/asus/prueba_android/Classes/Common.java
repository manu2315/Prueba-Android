package com.example.asus.prueba_android.Classes;

import com.example.asus.prueba_android.Interfaces.IGoogleApi;

public class Common {

        public static final String baseURL = "https://googleapis.com";
        public static IGoogleApi getGoogleAPi(){
            return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);

        }
}
