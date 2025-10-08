import java.sql.*;
import java.util.Scanner;

// Model Class
class Product {
    int productID;
    String productName;
    double price;
    int quantity;

    public Product(int productID, String productName, double price, int quantity) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}

// Database Utility Class
class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/your_database_name"; // Change your DB name
        String user = "root";  // Change if needed
        String pass = "your_password"; // Change if needed
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
}

// DAO Class (Data Access Object)
class ProductDAO {
    private Connection conn;

    public ProductDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public void insertProduct(Product p) throws SQLException {
        String query = "INSERT INTO Product (ProductName, Price, Quantity) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, p.productName);
        ps.setDouble(2, p.price);
        ps.setInt(3, p.quantity);
        ps.executeUpdate();
        System.out.println("✅ Product inserted successfully.");
    }

    // READ
    public void viewProducts() throws SQLException {
        String query = "SELECT * FROM Product";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        System.out.println("\n--- Product List ---");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("ProductID") + 
                               ", Name: " + rs.getString("ProductName") + 
                               ", Price: " + rs.getDouble("Price") + 
                               ", Quantity: " + rs.getInt("Quantity"));
        }
        System.out.println("--------------------");
    }

    // UPDATE
    public void updateProduct(int id, double newPrice, int newQuantity) throws SQLException {
        String query = "UPDATE Product SET Price = ?, Quantity = ? WHERE ProductID = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setDouble(1, newPrice);
        ps.setInt(2, newQuantity);
        ps.setInt(3, id);
        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("✅ Product updated successfully.");
        else
            System.out.println("⚠️ Product not found.");
    }

    // DELETE
    public void deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM Product WHERE ProductID = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("✅ Product deleted successfully.");
        else
            System.out.println("⚠️ Product not found.");
    }
}

// Controller + View (Main Menu)
public class ProductCRUDApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Enable transaction control
            ProductDAO dao = new ProductDAO(conn);

            while (true) {
                System.out.println("\n===== PRODUCT MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Product");
                System.out.println("2. View All Products");
                System.out.println("3. Update Product");
                System.out.println("4. Delete Product");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                try {
                    switch (choice) {
                        case 1:
                            sc.nextLine(); // consume newline
                            System.out.print("Enter Product Name: ");
                            String name = sc.nextLine();
                            System.out.print("Enter Price: ");
                            double price = sc.nextDouble();
                            System.out.print("Enter Quantity: ");
                            int qty = sc.nextInt();

                            dao.insertProduct(new Product(0, name, price, qty));
                            conn.commit();
                            break;

                        case 2:
                            dao.viewProducts();
                            break;

                        case 3:
                            System.out.print("Enter Product ID to Update: ");
                            int id = sc.nextInt();
                            System.out.print("Enter New Price: ");
                            double newPrice = sc.nextDouble();
                            System.out.print("Enter New Quantity: ");
                            int newQty = sc.nextInt();

                            dao.updateProduct(id, newPrice, newQty);
                            conn.commit();
                            break;

                        case 4:
                            System.out.print("Enter Product ID to Delete: ");
                            int delId = sc.nextInt();
                            dao.deleteProduct(delId);
                            conn.commit();
                            break;

                        case 5:
                            System.out.println("Exiting... Thank you!");
                            sc.close();
                            return;

                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error: " + e.getMessage());
                    conn.rollback(); // rollback on error
                    System.out.println("🔁 Transaction rolled back.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Connection Error: " + e.getMessage());
        }
    }
}
