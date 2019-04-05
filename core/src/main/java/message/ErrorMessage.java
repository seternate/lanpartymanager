package message;

public final class ErrorMessage {
    public static final String userNotLoggedIn = "User not logged in.",
                               userAlreadyLoggedIn = "User already logged in.",
                               gameNotOnServer = "Server don't have the game:",
                               noGameUpload = " is not uploading to ";
    public String error;


    @SuppressWarnings("unused")
    public ErrorMessage(){ }

    public ErrorMessage(String error){
        this.error = error;
    }
}
