package database;

import config.Config;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                Config.URL,
                Config.USER,
                Config.PASSWORD
        );
    }
}
