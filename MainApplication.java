import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MainApplication {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Routes
        server.createContext("/", new IndexHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/api/books", new BookHandler());
        server.createContext("/api/members", new MemberHandler());
        server.createContext("/api/issue", new IssueHandler());
        server.createContext("/api/return", new ReturnHandler());
        server.createContext("/api/stats", new StatsHandler());
        server.createContext("/api/issued-list", new IssuedListHandler());
        server.createContext("/health", (exchange) -> sendResponse(exchange, "OK", 200, "text/plain"));

        server.setExecutor(null);
        System.out.println("==============================================");
        System.out.println(" Professional Digital Library Server Started");
        System.out.println(" Access URL: http://localhost:" + port);
        System.out.println(" Log Out: Ctrl+C to stop");
        System.out.println("==============================================");
        server.start();
    }

    // --- Common Utilities ---

    static void sendResponse(HttpExchange exchange, String response, int code, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    static String getBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // --- Handlers ---

    static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, UIQueries.getLoginPage(), 200, "text/html");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            System.out.println("[Request] " + method + " /login");
            if ("POST".equals(method)) {
                try {
                    String body = getBody(exchange);
                    System.out.println("[Debug] Body: " + body);
                    Map<String, String> data = parseFormData(body);
                    String user = data.get("username");
                    String pass = data.get("password");
                    
                    System.out.println("[Debug] Attempting login for: " + user);
                    
                    try (Connection con = DBConnection.getConnection()) {
                        if (con == null) {
                             System.err.println("[Error] Could not get database connection");
                             sendResponse(exchange, "{\"error\":\"Database connection error\"}", 500, "application/json");
                             return;
                        }
                        try (PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
                            pst.setString(1, user);
                            pst.setString(2, pass);
                            try (ResultSet rs = pst.executeQuery()) {
                                if (rs.next()) {
                                    System.out.println("[Success] Login successful for: " + user);
                                    exchange.getResponseHeaders().set("Location", "/dashboard");
                                    exchange.sendResponseHeaders(302, -1);
                                    exchange.getResponseBody().close();
                                } else {
                                    System.out.println("[Fail] Invalid credentials for: " + user);
                                    sendResponse(exchange, "{\"error\":\"Invalid credentials\"}", 401, "application/json");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[Error] Exception in LoginHandler: ");
                    e.printStackTrace();
                    sendResponse(exchange, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}", 500, "application/json");
                }
            } else {
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.getResponseBody().close();
            }
        }
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendResponse(exchange, UIQueries.getDashboardPage(), 200, "text/html");
        }
    }

    static class BookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                StringBuilder json = new StringBuilder("[");
                try (Connection con = DBConnection.getConnection();
                     Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT * FROM books")) {
                    while (rs.next()) {
                        if (json.length() > 1) json.append(",");
                        json.append(String.format("{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"quantity\":%d}",
                                rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity")));
                    }
                } catch (Exception e) {
                    sendResponse(exchange, "Error: " + e.getMessage(), 500, "text/plain");
                    return;
                }
                json.append("]");
                sendResponse(exchange, json.toString(), 200, "application/json");
            } else if ("POST".equals(method)) {
                Map<String, String> data = parseFormData(getBody(exchange));
                String id = data.get("id");
                String title = data.get("title");
                String author = data.get("author");
                String qtyStr = data.get("quantity");

                if (title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty() || qtyStr == null) {
                    sendResponse(exchange, "{\"error\":\"Missing required fields\"}", 400, "application/json");
                    return;
                }

                try {
                    int quantity = Integer.parseInt(qtyStr);
                    if (quantity < 0) throw new NumberFormatException();

                    try (Connection con = DBConnection.getConnection()) {
                        if (id != null && !id.isEmpty()) {
                            // UPDATE
                            try (PreparedStatement pst = con.prepareStatement("UPDATE books SET title=?, author=?, quantity=? WHERE id=?")) {
                                pst.setString(1, title);
                                pst.setString(2, author);
                                pst.setInt(3, quantity);
                                pst.setInt(4, Integer.parseInt(id));
                                pst.executeUpdate();
                            }
                        } else {
                            // INSERT
                            try (PreparedStatement pst = con.prepareStatement("INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)")) {
                                pst.setString(1, title);
                                pst.setString(2, author);
                                pst.setInt(3, quantity);
                                pst.executeUpdate();
                            }
                        }
                        sendResponse(exchange, "{\"message\":\"Success\"}", 200, "application/json");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "{\"error\":\"Quantity must be a valid positive integer\"}", 400, "application/json");
                } catch (Exception e) {
                    sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500, "application/json");
                }
            }
        }
    }

    static class MemberHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
             String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                StringBuilder json = new StringBuilder("[");
                try (Connection con = DBConnection.getConnection();
                     Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT * FROM members")) {
                    while (rs.next()) {
                        if (json.length() > 1) json.append(",");
                        json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"phone\":\"%s\"}",
                                rs.getInt("id"), rs.getString("name"), rs.getString("phone")));
                    }
                } catch (Exception e) {
                    sendResponse(exchange, "Error: " + e.getMessage(), 500, "text/plain");
                    return;
                }
                json.append("]");
                sendResponse(exchange, json.toString(), 200, "application/json");
            } else if ("POST".equals(method)) {
                Map<String, String> data = parseFormData(getBody(exchange));
                String id = data.get("id");
                String name = data.get("name");
                String phone = data.get("phone");

                if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
                    sendResponse(exchange, "{\"error\":\"Missing required fields\"}", 400, "application/json");
                    return;
                }

                try (Connection con = DBConnection.getConnection()) {
                    if (id != null && !id.isEmpty()) {
                        // UPDATE
                        try (PreparedStatement pst = con.prepareStatement("UPDATE members SET name=?, phone=? WHERE id=?")) {
                            pst.setString(1, name);
                            pst.setString(2, phone);
                            pst.setInt(3, Integer.parseInt(id));
                            pst.executeUpdate();
                        }
                    } else {
                        // INSERT
                        try (PreparedStatement pst = con.prepareStatement("INSERT INTO members (name, phone) VALUES (?, ?)")) {
                            pst.setString(1, name);
                            pst.setString(2, phone);
                            pst.executeUpdate();
                        }
                    }
                    sendResponse(exchange, "{\"message\":\"Success\"}", 200, "application/json");
                } catch (Exception e) {
                    sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500, "application/json");
                }
            }
        }
    }

    static class IssueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> data = parseFormData(getBody(exchange));
                    int bid = Integer.parseInt(data.get("bookId"));
                    int mid = Integer.parseInt(data.get("memberId"));

                    try (Connection con = DBConnection.getConnection()) {
                        if (con == null) throw new SQLException("DB Connection failed");
                        con.setAutoCommit(false);
                        try {
                            // Check quantity
                            try (PreparedStatement check = con.prepareStatement("SELECT quantity FROM books WHERE id=?")) {
                                check.setInt(1, bid);
                                try (ResultSet rs = check.executeQuery()) {
                                    if (rs.next() && rs.getInt("quantity") > 0) {
                                        // Issue
                                        try (PreparedStatement issue = con.prepareStatement("INSERT INTO issue_books (book_id, member_id, issue_date) VALUES (?, ?, ?)")) {
                                            issue.setInt(1, bid);
                                            issue.setInt(2, mid);
                                            issue.setString(3, java.time.LocalDate.now().toString());
                                            issue.executeUpdate();
                                        }

                                        // Decrease
                                        try (PreparedStatement dec = con.prepareStatement("UPDATE books SET quantity = quantity - 1 WHERE id=?")) {
                                            dec.setInt(1, bid);
                                            dec.executeUpdate();
                                        }

                                        con.commit();
                                        sendResponse(exchange, "{\"message\":\"Book issued successfully\"}", 200, "application/json");
                                    } else {
                                        sendResponse(exchange, "{\"error\":\"Book not available or invalid ID\"}", 400, "application/json");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            con.rollback();
                            throw e;
                        }
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "{\"error\":\"IDs must be valid integers\"}", 400, "application/json");
                } catch (Exception e) {
                    sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500, "application/json");
                }
            }
        }
    }

    static class ReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> data = parseFormData(getBody(exchange));
                    int bid = Integer.parseInt(data.get("bookId"));
                    int mid = Integer.parseInt(data.get("memberId"));

                    try (Connection con = DBConnection.getConnection()) {
                        if (con == null) throw new SQLException("DB Connection failed");
                        con.setAutoCommit(false);
                        try {
                            // Check issue
                            try (PreparedStatement check = con.prepareStatement("SELECT id FROM issue_books WHERE book_id=? AND member_id=? LIMIT 1")) {
                                check.setInt(1, bid);
                                check.setInt(2, mid);
                                try (ResultSet rs = check.executeQuery()) {
                                    if (rs.next()) {
                                        int issueId = rs.getInt("id");
                                        // Delete
                                        try (PreparedStatement del = con.prepareStatement("DELETE FROM issue_books WHERE id=?")) {
                                            del.setInt(1, issueId);
                                            del.executeUpdate();
                                        }

                                        // Increase
                                        try (PreparedStatement inc = con.prepareStatement("UPDATE books SET quantity = quantity + 1 WHERE id=?")) {
                                            inc.setInt(1, bid);
                                            inc.executeUpdate();
                                        }

                                        con.commit();
                                        sendResponse(exchange, "{\"message\":\"Book returned successfully\"}", 200, "application/json");
                                    } else {
                                        sendResponse(exchange, "{\"error\":\"No active issue record found for this book and member\"}", 400, "application/json");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            con.rollback();
                            throw e;
                        }
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "{\"error\":\"IDs must be valid integers\"}", 400, "application/json");
                } catch (Exception e) {
                    sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500, "application/json");
                }
            }
        }
    }

    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT count(*) FROM books")) {
                int books = 0, members = 0, issued = 0;
                if(rs.next()) books = rs.getInt(1);
                try (ResultSet rs2 = st.executeQuery("SELECT count(*) FROM members")) { if(rs2.next()) members = rs2.getInt(1); }
                try (ResultSet rs3 = st.executeQuery("SELECT count(*) FROM issue_books")) { if(rs3.next()) issued = rs3.getInt(1); }
                
                String json = String.format("{\"books\":%d,\"members\":%d,\"issued\":%d}", books, members, issued);
                sendResponse(exchange, json, 200, "application/json");
            } catch (Exception e) {
                sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500, "application/json");
            }
        }
    }

    static class IssuedListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            String query = "SELECT i.issue_date, b.title, m.name, m.phone " +
                          "FROM issue_books i " +
                          "LEFT JOIN books b ON i.book_id = b.id " +
                          "LEFT JOIN members m ON i.member_id = m.id " +
                          "ORDER BY i.id DESC";
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    if (json.length() > 1) json.append(",");
                    String title = rs.getString("title");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    
                    if (title == null) title = "Unknown Book";
                    if (name == null) name = "Unknown Member";
                    if (phone == null) phone = "N/A";

                    json.append(String.format("{\"date\":\"%s\",\"book\":\"%s\",\"member\":\"%s\",\"phone\":\"%s\"}",
                            rs.getString("issue_date"), title, name, phone));
                }
            } catch (Exception e) {
                sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500, "application/json");
                return;
            }
            json.append("]");
            sendResponse(exchange, json.toString(), 200, "application/json");
        }
    }
}
