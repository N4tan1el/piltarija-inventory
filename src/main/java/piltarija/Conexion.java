package piltarija;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Opens the application database connection from environment variables. */
public final class Conexion {

    private static final String URL = requiredSetting("PILTARIJA_DB_URL");
    private static final String USUARIO = requiredSetting("PILTARIJA_DB_USER");
    private static final String CONTRASENA = requiredSetting("PILTARIJA_DB_PASSWORD");

    private Conexion() {
    }

    private static String requiredSetting(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Define the environment variable " + name);
        }
        return value;
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
