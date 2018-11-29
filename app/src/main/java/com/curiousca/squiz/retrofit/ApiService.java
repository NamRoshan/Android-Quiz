package com.curiousca.squiz.retrofit;

import com.curiousca.squiz.model.ApiObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public class ApiService {

    private static final String BASE_URL = "https://wowquiz25.000webhostapp.com/";

    public static RetrofitInterface getServiceClass() {
        return RetrofitAPI.getRetrofit(BASE_URL)
                .create(RetrofitInterface.class);
    }

    public interface RetrofitInterface {
        @GET("androidjsonquiz.php")
        public Call<List<ApiObject>> getAllPost();
        }
}
