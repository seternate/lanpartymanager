package entities;

import java.io.IOException;

/**
 * Serves the server settings.
 */
public final class ServerSettings extends Settings {
    /**
     * Loads the server settings.
     *
     * @throws IOException if any error occurs while creating server settings.
     */
    public ServerSettings() throws IOException {
        super(true);
    }
}
