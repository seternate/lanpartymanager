package clientInterface;

import entities.game.Game;
import entities.game.GameList;
import entities.game.GameStatus;
import entities.server.ServerStatus;
import entities.user.User;
import entities.user.UserList;
import entities.user.UserRunGamesList;
import entities.user.UserRunServerList;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.File;
import java.util.List;

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

    @POST("sendfiles")
    Call<Boolean> sendFiles(@Body User user, @Query("files") List<File> files);

    @GET("filestatus")
    Call<Boolean> getFileStatus();

    @POST("stopdownloadunzip")
    Call<Boolean> stopDownloadUnzip(@Body Game game);

    @GET("getuserrungames")
    Call<UserRunGamesList> getUserRunGames();

    @GET("getuserrunservers")
    Call<UserRunServerList> getUserRunServer();

    @POST("stopgame")
    Call<Boolean> stopGame(@Body Game game);

    @POST("stopgamesandservers")
    Call<Boolean> stopGamesAndServers();

}
