package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/hello")
public class App extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String url = "jdbc:mariadb://simple-web-app-db.cdg0iegkyfrh.ap-northeast-1.rds.amazonaws.com:3306/auth_db";
        String user = "admin";
        String password = "Password";

        response.setContentType("text/plain; charset=UTF-8");

        try {
            Driver driver = new org.mariadb.jdbc.Driver();
            DriverManager.registerDriver(driver);

            try (Connection connection = DriverManager.getConnection(url, user, password)) {

                String sql = """
                    SELECT id, organization_code, username, account_type, enabled
                    FROM auth_db.users
                    """;

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                while (resultSet.next()) {
                    response.getWriter().println(
                        resultSet.getLong("id") + " " +
                        resultSet.getString("organization_code") + " " +
                        resultSet.getString("username") + " " +
                        resultSet.getString("account_type") + " " +
                        resultSet.getBoolean("enabled")
                    );
                }
            }

        } catch (SQLException e) {
            response.getWriter().println("DB Connection failed!");
            e.printStackTrace();
        }
    }
}