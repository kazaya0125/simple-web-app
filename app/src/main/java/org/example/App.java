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

        response.setContentType("text/html; charset=UTF-8");

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

                response.getWriter().println("<html>");
                response.getWriter().println("<head><meta charset='UTF-8'><title>ユーザー一覧</title></head>");
                response.getWriter().println("<body>");

                response.getWriter().println("<h1>ユーザー一覧</h1>");

                response.getWriter().println("<table border='1'>");
                response.getWriter().println("<tr>");
                response.getWriter().println("<th>ID</th>");
                response.getWriter().println("<th>組織コード</th>");
                response.getWriter().println("<th>ユーザー名</th>");
                response.getWriter().println("<th>種別</th>");
                response.getWriter().println("<th>有効</th>");
                response.getWriter().println("</tr>");

                while (resultSet.next()) {
                    response.getWriter().println(
                        "<tr>" +
                        "<td>" + resultSet.getLong("id") + "</td>" +
                        "<td>" + resultSet.getString("organization_code") + "</td>" +
                        "<td>" + resultSet.getString("username") + "</td>" +
                        "<td>" + resultSet.getString("account_type") + "</td>" +
                        "<td>" + resultSet.getBoolean("enabled") + "</td>" +
                        "</tr>"
                    );
                }

                response.getWriter().println("</table>");
                response.getWriter().println("</body>");
                response.getWriter().println("</html>");

            }

        } catch (SQLException e) {
            response.getWriter().println("<h1>DB Connection failed!</h1>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}