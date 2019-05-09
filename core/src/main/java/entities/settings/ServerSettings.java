package entities.settings;

import java.io.IOException;

/**
 * {@code ServerSettings} manages the settings for the {@code LANServer}.
 * <br><br>
 * <p>
 *     Example for {@code ServerSettings} propertyfile:
 *     <br>
 *     servertcp = 54555
 *     <br>
 *     serverudp = 54777
 * </p>
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public class ServerSettings extends Settings {

    /**
     * Creates the {@code ServerSettings} and loads it.
     *
     * @throws IOException if any error while creating {@code ServerSettings} occur
     * @since 1.0
     */
    public ServerSettings() throws IOException {
        super(true);
    }

}
