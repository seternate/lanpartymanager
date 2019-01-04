package message;

public class ErrorMessage {
    public static final String userNotLoggedIn = "User not logged in.",
                               userAllreadyLoggedIn = "User allready logged in.",
                               gameNotOnServer = "Server don't have the game: ";

    public String error;


    public ErrorMessage(){ }

    public ErrorMessage(String error){
        this.error = error;
    }
}
