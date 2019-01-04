package main;

import entities.Status;
import entities.User;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FXDataService {
    @GET("status")
    Call<Status> getStatus();

    @GET("login")
    Call<User> getLogin();
}
