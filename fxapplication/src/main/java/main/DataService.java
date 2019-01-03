package main;

import entities.Game;
import entities.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public interface DataService {
    @GET("/status")
    Call<ResponseBody> getStatus();

    @GET("/login")
    Call<Void> login();

    @GET("/games")
    Call<List<Game>> getGamelist();

    @GET("/users")
    Call<List<User>> getUserlist();

    @GET("/games/{name}/isuptodate")
    Call<Boolean> isUptodate(@Path("name") String gamename);



    @GET("/download")
    Call<Integer> download(@Query("game") String gameName);
}
