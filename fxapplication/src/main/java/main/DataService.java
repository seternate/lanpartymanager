package main;

import entities.Game;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface DataService {
    @GET("/games")
    Call<List<Game>> listGames();

    @GET("/download")
    Call<Integer> download(@Query("game") String gameName);
}
