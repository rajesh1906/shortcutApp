package com.shortshop.shortcutapp;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RestAPI {
    @GET
    Call<ResponseBody> getUrlData(@Url String rest_url);

}
