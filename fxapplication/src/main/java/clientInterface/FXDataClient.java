package clientInterface;

import entities.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface FXDataClient {

    @GET("status")
    Call<ServerStatus> getStatus();

    @GET("user")
    Call<User> getUser();

    @POST("user")
    Call<Boolean> sendUser(@Body User user);

    @GET("games")
    Call<GameList> getGames();

    @POST("startgame")
    Call<Boolean> startGame(@Body Game game);

    @POST("download")
    Call<Integer> downloadGame(@Body Game game);

    @POST("games/status")
    Call<GameStatus> getGameStatus(@Body Game game);

    @POST("openexplorer")
    Call<Boolean> openExplorer(@Body Game game);

    @GET("users")
    Call<UserList> getUserlist();

    @POST("startserver")
    Call<Boolean> startServer(@Body Game game, @Query("param") String parameters);

    @POST("connect")
    Call<Boolean> connectServer(@Body Game game, @Query("ip") String ip);

}
