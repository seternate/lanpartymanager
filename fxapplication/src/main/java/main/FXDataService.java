package main;

import entities.Game;
import entities.Status;
import entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface FXDataService {
    @GET("status")
    Call<Status> getStatus();

    @GET("login")
    Call<User> getLogin();

    @POST("login")
    Call<Boolean> login(@Body User user);

    @POST("games")
    Call<List<Game>> getGames(@Body List<Game> games);

    @POST("games/uptodate")
    Call<Integer> isGameUptodate(@Body Game game);

    @GET("users")
    Call<List<User>> getUsers();

    @POST("games/download")
    Call<Integer> download(@Body Game game);
}
