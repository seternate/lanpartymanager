package clientInterface;

import entities.User;
import entities.GameList;
import entities.ServerStatus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FXDataClient {

    @GET("status")
    Call<ServerStatus> getStatus();

    @GET("user")
    Call<User> getUser();

    @POST("user")
    Call<Boolean> sendUser(@Body User user);

    @GET("games")
    Call<GameList> getGames();

    /*
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

    @POST("users")
    Call<List<User>> getUsers(@Body List<User> users);

    @POST("games/download")
    Call<Integer> download(@Body Game game);

    @POST("games/openexplorer")
    Call<Boolean> openExplorer(@Body Game game);

    @POST("games/startgame")
    Call<Boolean> startGame(@Body Game game);

    @POST("games/connect/{ip}")
    Call<Boolean> connect(@Body Game game, @Path("ip") String ip);

    @POST("games/startserver")
    Call<Boolean> startServer(@Body Game game, @Query("param") String param);
    */
}
