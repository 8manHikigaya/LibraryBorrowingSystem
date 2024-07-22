package library.management.system;
import java.sql.*;
import java.util.Scanner;


class App {
    public static void main(String args[]) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db", "root", "supersaltyspider");
            // here db is database name, root is username and password

            Statement stmt = con.createStatement();

            //creating a books table
            String createTableQuery = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "author VARCHAR(100) NOT NULL," +
                    "price DECIMAL(10, 2) NOT NULL," +
                    "tag VARCHAR(100))";
            stmt.executeUpdate(createTableQuery);

            // Create sold products table if it doesn't exist
            String createSoldTableQuery = "CREATE TABLE IF NOT EXISTS borrowed_books (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "book_id INT NOT NULL," +
                    "borrow_date VARCHAR(100) NOT NULL," +
                    "student VARCHAR(100) NOT NULL)";
            stmt.executeUpdate(createSoldTableQuery);

            

            //menu loop
            Scanner sc = new Scanner(System.in);
            int choice = 0;
            while (choice != 9) {
                System.out.println("1. Upload books");
                System.out.println("2. Browse books");
                System.out.println("3. Borrow books");
                System.out.println("4. View borrowed books");
                System.out.println("5. Return books");
                System.out.println("6. Book History");
                System.out.println("7. View UserBase");
                System.out.println("8. Lost Books");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
                
                switch(choice) {
                    case 1:
                        System.out.print("Enter book name: ");
                        String bookName = sc.nextLine();
                        System.out.print("Enter book author: ");
                        String bookAuth = sc.nextLine();
                        System.out.print("Enter product price: ");
                        double bookPrice = sc.nextDouble();
                        sc.nextLine(); // Consume newline
                        System.out.print("Enter product tag: ");
                        String bookTag = sc.nextLine();
                        uploadBooks(con, bookName, bookAuth, bookPrice, bookTag);
                        break;
                    case 2:
                        browseBooks(stmt, sc);
                        break;
                    case 3:
                        System.out.print("Enter book ID to borrow: ");
                        int bookId = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        System.out.print("Enter the date of borrowing: ");
                        String borrow_date = sc.nextLine();
                        System.out.print("Enter your name: ");
                        String student = sc.nextLine();
                        borrowBooks(con, bookId, borrow_date, student);
                        break;
                    case 4:
                        viewBorrowedBooks(stmt);
                        break;
                    case 5:
                        returnBooks(con, sc);
                        break;
                    case 6:
                        checkHistory(con, sc);
                        break;
                    case 7:
                        checkuserBase(stmt);
                        break;
                    case 8:
                        lostBooks(stmt, sc, con);
                        break;
                    case 9:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice, Please try again.");
                }
                System.out.println();

            }




            sc.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void uploadBooks(Connection conn, String name, String author, double price, String tag) throws SQLException {
        String insertQuery = "INSERT INTO books (name, author, price, tag) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertQuery);
        pstmt.setString(1, name);
        pstmt.setString(2, author);
        pstmt.setDouble(3, price);
        pstmt.setString(4, tag);
        pstmt.executeUpdate();
        System.out.println("Product uploaded successfully.");
        pstmt.close();
    }

    private static void browseBooks(Statement stmt, Scanner sc) throws SQLException {
        System.out.print("Enter a tag filter or else just press ENTER: ");
        String filter = sc.nextLine();
        String selectQuery;
        if (filter == "") {
            selectQuery = "SELECT * FROM books";
        } else {
            selectQuery = "SELECT * FROM books where tag = '" + filter + "'";
        }

        ResultSet rs = stmt.executeQuery(selectQuery);

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String author = rs.getString("author");
            double price = rs.getDouble("price");
            String description = rs.getString("tag");

            System.out.println("\nID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Author: " + author);
            System.out.println("Price: " + price);
            System.out.println("Description: " + description);
            System.out.println("----------------------");
        }

        rs.close();
    }

    private static void borrowBooks(Connection conn, int bookId, String borrow_date, String student) throws SQLException {
        String checkProductQuery = "SELECT * FROM books WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(checkProductQuery);
        pstmt.setInt(1, bookId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String insertQuery = "INSERT INTO borrowed_books (book_id, borrow_date, student) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, bookId);
            insertStmt.setString(2, borrow_date);
            insertStmt.setString(3, student);
            insertStmt.executeUpdate();
            System.out.println("Book borrowed successfully.");
            insertStmt.close();
        } else {
            System.out.println("Book not found.");
        }

        rs.close();
        pstmt.close();
    }

    private static void viewBorrowedBooks(Statement stmt) throws SQLException {
        String selectQuery = "SELECT p.id, sp.borrow_date, p.name, p.author, p.price, p.tag, sp.student " +
                "FROM borrowed_books sp " +
                "JOIN books p ON sp.book_id = p.id";
        ResultSet rs = stmt.executeQuery(selectQuery);

        while (rs.next()) {
            int id = rs.getInt("id");
            String purchaseDate = rs.getString("borrow_date");
            String name = rs.getString("name");
            String author = rs.getString("author");
            double price = rs.getDouble("price");
            String tag = rs.getString("tag");
            String student = rs.getString("student");
            
            System.out.println("\nId: " + id);
            System.out.println("Purchase Date: " + purchaseDate);
            System.out.println("Name: " + name);
            System.out.println("Author: " + author);
            System.out.println("Price: " + price);
            System.out.println("Tag: " + tag);
            System.out.println("Student: " + student);
            System.out.println("----------------------");
        }

        rs.close();
    }

    private static void returnBooks(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter your name: ");
        String student = sc.nextLine();
        String checkBorrowQuery = "SELECT p.id, sp.borrow_date, p.name, p.author, p.price, p.tag, sp.student " +
                "FROM borrowed_books sp " +
                "JOIN books p ON sp.book_id = p.id and sp.student = ?";
        PreparedStatement pstmt = con.prepareStatement(checkBorrowQuery);
        pstmt.setString(1, student);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String purchaseDate = rs.getString("borrow_date");
            String name = rs.getString("name");
            String author = rs.getString("author");
            double price = rs.getDouble("price");
            String tag = rs.getString("tag");
            

            System.out.println("\nId: " + id);
            System.out.println("Purchase Date: " + purchaseDate);
            System.out.println("Name: " + name);
            System.out.println("Author: " + author);
            System.out.println("Price: " + price);
            System.out.println("Tag: " + tag);
            System.out.println("----------------------");
        }

        System.out.print("Enter the ID of the book you wish to return: ");
        int returnID = sc.nextInt();
        System.out.print("Enter the number of days you have kept the book: ");
        int returnDate = sc.nextInt();

        // String checkDateQuery = "SELECT sp.borrow_date FROM borrowed_books sp WHERE book_id = ? ";
        // PreparedStatement getDateStmt = con.prepareStatement(checkDateQuery);
        // pstmt.setInt(1, returnID);
        // ResultSet rs2 = getDateStmt.executeQuery();

        
        // while(rs2.next()) {
        //     String borrow_date = rs2.getString("borrow_date");

        //     int difference = Integer.parseInt(returnDate)/10000 - Integer.parseInt(borrow_date)/10000;

        //     if (difference >= 14) {
        //         int fine = (difference - 14)*10;
        //         System.out.println("The fine you must pay is : " + fine);
                
        //     }
        // }

        if (returnDate > 14) {
            int fine = (returnDate - 14) * 10;
            System.out.println("The fine you must pay for this book is : " + fine);
        }

        String dropBorrowQuery = "DELETE from borrowed_books WHERE book_id = ?";
        PreparedStatement deleteStmt = con.prepareStatement(dropBorrowQuery);
        deleteStmt.setInt(1, returnID);
        deleteStmt.executeUpdate();

        System.out.println("Returned the book!");
        deleteStmt.close();



        rs.close();
        pstmt.close();
    }
    
    private static void checkHistory(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter the ID of the book: ");
        int bookID = sc.nextInt();

        String checkHistoryQuery = "SELECT p.id, sp.borrow_date, p.name, p.author, p.price, p.tag, sp.student " +
                "FROM books p " +
                "JOIN borrowed_books sp ON sp.book_id = p.id and sp.book_id = ?";

        PreparedStatement pstmt = con.prepareStatement(checkHistoryQuery);
        pstmt.setInt(1, bookID);
        ResultSet rs = pstmt.executeQuery();

        if(rs.next()) {
            while(rs.next()) {
                int id = rs.getInt("id");
                String purchaseDate = rs.getString("borrow_date");
                String name = rs.getString("name");
                String author = rs.getString("author");
                double price = rs.getDouble("price");
                String tag = rs.getString("tag");
                String student = rs.getString("student");
                

                System.out.println("\nId: " + id);
                System.out.println("Purchase Date: " + purchaseDate);
                System.out.println("Name: " + name);
                System.out.println("Author: " + author);
                System.out.println("Price: " + price);
                System.out.println("Tag: " + tag);
                System.out.println("Student: " + student);
                System.out.println("----------------------");

            }

        } else {
            checkHistoryQuery = "SELECT * from books WHERE id = ?";
            PreparedStatement bpstmt = con.prepareStatement(checkHistoryQuery);
            bpstmt.setInt(1, bookID);
            ResultSet rs2 = bpstmt.executeQuery();
            while(rs2.next()) {
                int id = rs2.getInt("id");
                String name = rs2.getString("name");
                String author = rs2.getString("author");
                double price = rs2.getDouble("price");
                String tag = rs2.getString("tag");
                

                System.out.println("\nId: " + id);
                System.out.println("Name: " + name);
                System.out.println("Author: " + author);
                System.out.println("Price: " + price);
                System.out.println("Tag: " + tag);
                System.out.println("Status: Unborrowed!");
                System.out.println("----------------------");

            }
            rs2.close();
            bpstmt.close();


        }

        pstmt.close();
        rs.close();
    }

    private static void checkuserBase(Statement stmt) throws SQLException {

        String checkUserQuery = "SELECT student from borrowed_books";
        ResultSet rs = stmt.executeQuery(checkUserQuery);
        
        if(rs.next()){
            System.out.println("\nStudents: ");
            while(rs.next()) {
                String student = rs.getString("student");

                System.out.println(student);
            }
        } else {
            System.out.println("No one has borrowed yet!");
        }

    }

    private static void lostBooks(Statement stmt, Scanner sc, Connection con) throws SQLException {

        System.out.print("Enter your name: ");
        String s_name = sc.nextLine();
        
        String checkUserQuery = "SELECT p.id, p.name, p.price " +
                                "FROM books p " +
                                "JOIN borrowed_books sp on sp.book_id = p.id and sp.student = ?";
        PreparedStatement pstmt = con.prepareStatement(checkUserQuery);
        pstmt.setString(1, s_name);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Type y/n if you have lost the book");
        double cost = 0;

        while(rs.next()) {
            String book = rs.getString("name");
            System.out.print("Have you lost " + book + " : ");
            String ch = sc.nextLine();
            if ("y".equals(ch)) {
                double price = rs.getDouble("price");
                cost = cost + price;
            } 
        }

        System.out.println("Your final price is " + cost);

        
    }
}
