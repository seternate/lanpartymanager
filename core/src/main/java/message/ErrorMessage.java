package message;

/**
 * {@code ErrorMessage} is a class to send an errormessage.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class ErrorMessage {
    public static final String userNotLoggedIn = "User not logged in.",
                               userAlreadyLoggedIn = "User already logged in.",
                               gameNotOnServer = "Server don't have the game:",
                               noGameUpload = " is not uploading to ";
    public String error;

    public ErrorMessage(){ }

    public ErrorMessage(String error){
        this.error = error;
    }

}
