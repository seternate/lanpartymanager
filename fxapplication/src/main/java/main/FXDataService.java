package main;

import entities.Game;
import entities.GameStatus;
import entities.ServerStatus;
import entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface FXDataService {
    @GET("status")
    Call<ServerStatus> getStatus();

    @GET("login")
    Call<User> getLogin();

    @POST("login")
    Call<Boolean> login(@Body User user);

    @POST("games")
    Call<List<Game>> getGames(@Body List<Game> games);

    @POST("games/status")
    Call<GameStatus> getGameStatus(@Body Game game);

    @GET("users")
    Call<List<User>> getUsers();

    @POST("games/download")
    Call<Integer> download(@Body Game game);

    @POST("games/openexplorer")
    Call<Boolean> openExplorer(@Body Game game);

    @POST("games/startgame")
    Call<Boolean> startGame(@Body Game game);

    @POST("games/connect/{ip}")
    Call<Boolean> connect(@Body Game game, @Path("ip") String ip);
}
