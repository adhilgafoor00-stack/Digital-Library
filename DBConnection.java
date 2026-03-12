import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:library.db";
    private static boolean initialized = false;

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection(URL);
            if (!initialized) {
                initializeDatabase(con);
                initialized = true;
            }
            return con;
        } catch (Throwable t) {
            System.err.println("[DB Error] " + t.getMessage());
            return null;
        }
    }

    private static void initializeDatabase(Connection con) {
        System.out.println("[DB] Initializing database schema...");
        try (Statement stmt = con.createStatement()) {
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT)");

            // Create books table
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "author TEXT," +
                    "quantity INTEGER)");

            // Create members table
            stmt.execute("CREATE TABLE IF NOT EXISTS members (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "phone TEXT)");

            // Create issue_books table
            stmt.execute("CREATE TABLE IF NOT EXISTS issue_books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "book_id INTEGER," +
                    "member_id INTEGER," +
                    "issue_date TEXT," +
                    "FOREIGN KEY(book_id) REFERENCES books(id)," +
                    "FOREIGN KEY(member_id) REFERENCES members(id))");

            // Insert default user if not exists
            ResultSet rsAdmin = stmt.executeQuery("SELECT count(*) FROM users WHERE username='admin'");
            if (rsAdmin.next() && rsAdmin.getInt(1) == 0) {
                System.out.println("[DB] Creating default admin user.");
                stmt.execute("INSERT INTO users (username, password) VALUES ('admin', 'admin123')");
            }

            // Insert default books if table is empty
            ResultSet rsBooks = stmt.executeQuery("SELECT count(*) FROM books");
            if (rsBooks.next() && rsBooks.getInt(1) == 0) {
                System.out.println("[DB] Adding default books inventory.");
                stmt.execute("INSERT INTO books (title, author, quantity) VALUES ('Java Programming', 'James Gosling', 5)");
                stmt.execute("INSERT INTO books (title, author, quantity) VALUES ('Clean Code', 'Robert Martin', 3)");
                stmt.execute("INSERT INTO books (title, author, quantity) VALUES ('The Pragmatic Programmer', 'Andrew Hunt', 4)");
            }

            // Insert default members if table is empty
            ResultSet rsMembers = stmt.executeQuery("SELECT count(*) FROM members");
            if (rsMembers.next() && rsMembers.getInt(1) == 0) {
                System.out.println("[DB] Adding default library members.");
                stmt.execute("INSERT INTO members (name, phone) VALUES ('Alice Smith', '555-0101')");
                stmt.execute("INSERT INTO members (name, phone) VALUES ('Bob Jones', '555-0102')");
            }

            System.out.println("[DB] Initialization complete.");
        } catch (SQLException e) {
            System.err.println("[DB Error] Schema initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}