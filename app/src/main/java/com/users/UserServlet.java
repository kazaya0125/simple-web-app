package com.users;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

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

                String id = request.getParameter("id");

                // idが指定されている場合：編集画面
                if (id != null) {

                    String sql = """
                        SELECT id, organization_code, username, account_type, enabled
                        FROM auth_db.users
                        WHERE id = ?
                        """;

                    try (PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setLong(1, Long.parseLong(id));

                        try (ResultSet resultSet = statement.executeQuery()) {

                            if (resultSet.next()) {

                                response.getWriter().println("<html>");
                                response.getWriter().println("<head><meta charset='UTF-8'><title>ユーザー編集</title></head>");
                                response.getWriter().println("<body>");

                                response.getWriter().println("<h1>ユーザー編集</h1>");

                                response.getWriter().println("<form method='post' action='/app/users'>");

                                response.getWriter().println(
                                    "<input type='hidden' name='id' value='" +
                                    resultSet.getLong("id") +
                                    "'>"
                                );

                                response.getWriter().println("<p>");
                                response.getWriter().println("ID: ");
                                response.getWriter().println(resultSet.getLong("id"));
                                response.getWriter().println("</p>");

                                response.getWriter().println("<p>");
                                response.getWriter().println("組織コード: ");
                                response.getWriter().println(resultSet.getString("organization_code"));
                                response.getWriter().println("</p>");

                                response.getWriter().println("<p>");
                                response.getWriter().println("ユーザー名: ");

                                response.getWriter().println(
                                    "<input type='text' name='username' value='" +
                                    resultSet.getString("username") +
                                    "'>"
                                );

                                response.getWriter().println("</p>");

                                response.getWriter().println("<p>");
                                response.getWriter().println("種別: ");
                                response.getWriter().println(resultSet.getString("account_type"));
                                response.getWriter().println("</p>");

                                response.getWriter().println("<p>");
                                response.getWriter().println("有効: ");
                                response.getWriter().println(resultSet.getBoolean("enabled"));
                                response.getWriter().println("</p>");

                                response.getWriter().println("<button type='submit'>更新</button>");

                                response.getWriter().println("</form>");

                                response.getWriter().println("</body>");
                                response.getWriter().println("</html>");
                            }
                        }
                    }

                // idが指定されていない場合：一覧画面
                } else {

                    String sql = """
                        SELECT id, organization_code, username, account_type, enabled
                        FROM auth_db.users
                        """;

                    try (
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql)
                    ) {

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
                        response.getWriter().println("<th>操作</th>");
                        response.getWriter().println("</tr>");

                        while (resultSet.next()) {

                            response.getWriter().println(
                                "<tr>" +
                                "<td>" + resultSet.getLong("id") + "</td>" +
                                "<td>" + resultSet.getString("organization_code") + "</td>" +
                                "<td>" + resultSet.getString("username") + "</td>" +
                                "<td>" + resultSet.getString("account_type") + "</td>" +
                                "<td>" + resultSet.getBoolean("enabled") + "</td>" +
                                "<td><a href='/app/users?id=" +
                                resultSet.getLong("id") +
                                "'>編集</a></td>" +
                                "</tr>"
                            );
                        }

                        response.getWriter().println("</table>");
                        response.getWriter().println("</body>");
                        response.getWriter().println("</html>");
                    }
                }

            }

        } catch (SQLException e) {

            response.getWriter().println("<h1>DB Connection failed!</h1>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");

            e.printStackTrace();

        } catch (NumberFormatException e) {

            response.getWriter().println("<h1>Invalid ID</h1>");
            response.getWriter().println("<p>指定されたIDが不正です。</p>");

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String url = "jdbc:mariadb://simple-web-app-db.cdg0iegkyfrh.ap-northeast-1.rds.amazonaws.com:3306/auth_db";
        String user = "admin";
        String password = "Password";

        request.setCharacterEncoding("UTF-8");

        try {
            Driver driver = new org.mariadb.jdbc.Driver();

            DriverManager.registerDriver(driver);

            try (Connection connection = DriverManager.getConnection(url, user, password)) {

                String id = request.getParameter("id");
                String username = request.getParameter("username");

                String sql = """
                    UPDATE auth_db.users
                    SET username = ?
                    WHERE id = ?
                    """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setString(1, username);
                    statement.setLong(2, Long.parseLong(id));

                    statement.executeUpdate();
                }

                // 更新後、ユーザー一覧へ戻る
                response.sendRedirect("/app/users");
            }

        } catch (SQLException e) {

            response.setContentType("text/html; charset=UTF-8");

            response.getWriter().println("<h1>DB Update failed!</h1>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");

            e.printStackTrace();

        } catch (NumberFormatException e) {

            response.setContentType("text/html; charset=UTF-8");

            response.getWriter().println("<h1>Invalid ID</h1>");
            response.getWriter().println("<p>指定されたIDが不正です。</p>");
        }
    }
}