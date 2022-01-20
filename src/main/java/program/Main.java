//1
package program;

import com.github.javafaker.Faker;

import java.awt.print.Paper;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.sql.*;
import java.util.*;

public class Main {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        String strConn = "jdbc:mariadb://localhost:3306/local.spu911.java";

        Seeder(strConn);

        int menu = 0;
        while (menu != -1) {
            System.out.println("---------------------------------------------------\n" +
                    "1 - Select from DB\n" +
                    "2 - Insert into DB\n" +
                    "3 - Update DB\n" +
                    "4 - Delete from DB\n" +
                    "5 - Set filter\n" +
                    "\n" +
                    "-1 - Exit\n" +
                    "---------------------------------------------------");
            menu = in.nextInt();

            switch (menu) {
                case 1:
                    SelectFromDB(strConn);
                    break;
                case 2:
                    InsertIntoDB(strConn);
                    break;
                case 3:
                    UpdateForDB(strConn);
                    break;
                case 4:
                    DeleteFromDB(strConn);
                    break;
                case 5:
                    SelectFilterFromDB(strConn);
                    break;
                case -1:
                    return;
                default:
                    System.out.println("Invalid item");
                    break;
            }
        }
    }

    private static void PrintProductList(List<Product> prods) {
        for (Product pr : prods) {
            System.out.println("Id: " + pr.getId() +
                    " | Name: " + pr.getName() +
                    " | Price: " + pr.getPrice() +
                    " | Description: " + pr.getDescription());
        }
    }

    private static void SelectFromDB(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            System.out.println("Successful connection");

            String selectSql = "SELECT * FROM products";
            try {
                PreparedStatement ps = con.prepareStatement(selectSql);

                ResultSet resultSet = ps.executeQuery();
                List<Product> products = new ArrayList<>();
                while (resultSet.next()) {
                    Product pr = new Product(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getString("description"));
                    products.add(pr);
                }
                PrintProductList(products);
            } catch (SQLException ex) {
                System.out.println("Error executeQuery: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
    }

    private static  void SelectFilterFromDB(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            System.out.println("Successful connection");

            String selectSql = "SELECT * FROM products WHERE UPPER(name) LIKE ?";
            try {
                System.out.print("Enter filter for name: ");
                in.nextLine();
                String filterName = in.nextLine().toUpperCase(Locale.ROOT);

                PreparedStatement ps = con.prepareStatement(selectSql);
                ps.setString(1, "%" + filterName + "%");

                ResultSet resultSet = ps.executeQuery();
                List<Product> products = new ArrayList<>();
                while (resultSet.next()) {
                    Product pr = new Product(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getString("description"));
                    products.add(pr);
                }
                PrintProductList(products);
            } catch (Exception ex) {
                System.out.println("Error executeQuery: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
    }

    private static  void AutoInsertIntoDB(String strConn, List<Params> params) {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {

            String query = "INSERT INTO `products` (`name`, `price`, `description`) " +
                    "VALUES (?, ?, ?);";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                for (Params p: params) {
                    stmt.setString(1, p.getName());
                    stmt.setBigDecimal(2, p.getPrice());
                    stmt.setString(3, p.getDescription());

                    stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                System.out.println("Error statements: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                System.out.println("Error types: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
    }

    private static  void InsertIntoDB(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            System.out.println("Successful connection");

            String query = "INSERT INTO `products` (`name`, `price`, `description`) " +
                    "VALUES (?, ?, ?);";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                String name, description;
                double price;
                System.out.print("Enter name: ");
                in.nextLine();
                name = in.nextLine();
                System.out.print("Enter price: ");
                price = Double.parseDouble(in.nextLine());
                System.out.print("Enter description: ");
                description = in.nextLine();


                stmt.setString(1, name);
                stmt.setBigDecimal(2, new BigDecimal(price));
                stmt.setString(3, description);

                stmt.executeUpdate();

                System.out.println("Successful insert");
            } catch (SQLException ex) {
                System.out.println("Error statements: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                System.out.println("Error types: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
    }

    private static void UpdateForDB(String strConn) {
        SelectFromDB(strConn);
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {

            String query = "UPDATE products SET name = ? WHERE id = ?";
            try(PreparedStatement stmt = con.prepareStatement(query)) {
                System.out.print("\nEnter id: ");
                int id = in.nextInt();
                System.out.print("Enter new name: ");
                in.nextLine();
                String name = in.nextLine();

                List<Integer> ids = SelectIdFromDB(strConn);
                for (int i: ids) {
                    if(id == i) {
                        stmt.setInt(2, id);
                        stmt.setString(1, name);

                        stmt.executeQuery();

                        System.out.println("Successful update");
                        return;
                    }
                }
                throw new ArrayIndexOutOfBoundsException("Invalid index");

            } catch (SQLException ex) {
                System.out.println("Error update:" + ex.getMessage());
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("Error : " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
    }

    private static void DeleteFromDB(String strConn) {
        SelectFromDB(strConn);
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            String query = "DELETE FROM products WHERE id = ?";
            try(PreparedStatement stmt = con.prepareStatement(query)) {
                System.out.print("Enter Id: ");
                int id = in.nextInt();

                List<Integer> ids = SelectIdFromDB(strConn);
                for (int i: ids) {
                    if(id == i) {
                        stmt.setInt(1, id);
                        stmt.executeQuery();
                        System.out.println("Successful delete");
                        return;
                    }
                }
                throw new ArrayIndexOutOfBoundsException("Invalid id");

            } catch (SQLException ex) {
                System.out.println("Error delete: " + ex.getMessage());
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("Error: " + ex.getMessage());
            }

        } catch (Exception ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }

    }

    private static List<Integer> SelectIdFromDB(String strConn) {
        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
            String selectSql = "SELECT * FROM products";
            try {
                PreparedStatement ps = con.prepareStatement(selectSql);
                ResultSet resultSet = ps.executeQuery();
                List<Integer> products = new ArrayList<>();
                while (resultSet.next()) {
                    products.add(resultSet.getInt("id"));
                }
                return products;
            } catch (SQLException ex) {
                System.out.println("Error executeQuery: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Error connection: " + ex.getMessage());
        }
        return null;
    }

    private static void Seeder(String strConn) {
        if(SelectIdFromDB(strConn).size() == 0) {
            System.out.println("Choose locale:\n" +
                    "1) - en-GB\n" +
                    "2) - ru");
            int menu = in.nextInt();

            Faker fake = null;

            switch (menu) {
                case 1:
                    fake = new Faker(new Locale("en-GB"), new Random());
                    break;
                case 2:
                    fake = new Faker(new Locale("ru"), new Random());
                    break;
                default:
                    System.out.println("Invalid index");
                    break;
            }

            if(fake != null) {
                int count = 0;
                List<Params> params = new ArrayList<>();
                while (count != 1000) {
                    params.add(new Params(fake.food().ingredient(), new BigDecimal(fake.number().numberBetween(0,new Random().nextInt(1000))), fake.food().spice()));
                    ++count;
                }
                AutoInsertIntoDB(strConn, params);
            }
        }
    }
}

//2

//package program;
//
//import com.github.javafaker.Faker;
//import java.net.ConnectException;
//import java.sql.*;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.Date;
//
//public class Main {
//    static Scanner in = new Scanner(System.in);
//    public static void main(String[] args) {
//        String strConn = "jdbc:mariadb://localhost:3306/local.spu911.java";
//
//        try {
//            Seeder(strConn);
//        } catch (Exception ex) {
//
//        }
//
//        int menu = 0;
//        while (menu != -1) {
//            System.out.println("---------------------------------------------------\n" +
//                    "1 - Select from DB\n" +
//                    "2 - Insert into DB\n" +
//                    "3 - Update DB\n" +
//                    "4 - Delete from DB\n" +
//                    "5 - Set filter\n" +
//                    "\n" +
//                    "-1 - Exit\n" +
//                    "---------------------------------------------------");
//            menu = in.nextInt();
//
//            switch (menu) {
//                case 1:
//                    SelectFromDB(strConn);
//                    break;
//                case 2:
//                    InsertIntoDB(strConn);
//                    break;
//                case 3:
//                    UpdateForDB(strConn);
//                    break;
//                case 4:
//                    DeleteFromDB(strConn);
//                    break;
//                case 5:
//                    SelectFilterFromDB(strConn);
//                    break;
//                case -1:
//                    return;
//                default:
//                    System.out.println("Invalid item");
//                    break;
//            }
//        }
//    }
//
//    private static void PrintNewsList(List<News> prods) {
//        DateFormat dateFormatter;
//        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("fr_FR"));
//        for (News n : prods) {
//            System.out.println("Id: " + n.getId() +
//                    "\n | Name: " + n.getName() + "\n" +
//                    " | Content: " + n.getContent() + "\n" +
//                    " | Date: " + dateFormatter.format(n.getDate()));
//        }
//    }
//
//    private static void SelectFromDB(String strConn) {
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//            System.out.println("Successful connection");
//
//            String selectSql = "SELECT * FROM news";
//            try {
//                PreparedStatement ps = con.prepareStatement(selectSql);
//
//                ResultSet resultSet = ps.executeQuery();
//                List<News> news = new ArrayList<>();
//                while (resultSet.next()) {
//                    News n = new News(resultSet.getInt("id"),
//                            resultSet.getString("name"),
//                            resultSet.getString("content"),
//                            resultSet.getDate("date"));
//                    news.add(n);
//                }
//                PrintNewsList(news);
//            } catch (SQLException ex) {
//                System.out.println("Error executeQuery: " + ex.getMessage());
//            }
//        } catch (SQLException ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//    }
//
//    private static  void SelectFilterFromDB(String strConn) {
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//            System.out.println("Successful connection");
//
//            String selectSql = "SELECT * FROM news WHERE UPPER(name) LIKE ?";
//            try {
//                System.out.print("Enter filter for name: ");
//                in.nextLine();
//                String filterName = in.nextLine().toUpperCase(Locale.ROOT);
//
//                PreparedStatement ps = con.prepareStatement(selectSql);
//                ps.setString(1, "%" + filterName + "%");
//
//                ResultSet resultSet = ps.executeQuery();
//                List<News> news = new ArrayList<>();
//                while (resultSet.next()) {
//                    News n = new News(resultSet.getInt("id"),
//                            resultSet.getString("name"),
//                            resultSet.getString("content"),
//                            resultSet.getDate("date"));
//                    news.add(n);
//                }
//                PrintNewsList(news);
//            } catch (Exception ex) {
//                System.out.println("Error executeQuery: " + ex.getMessage());
//            }
//        } catch (SQLException ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//    }
//
//    private static  void AutoInsertIntoDB(String strConn, List<NewsParams> params) {
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//
//            String query = "INSERT INTO `news` (`name`, `content`, `date`) " +
//                    "VALUES (?, ?, ?);";
//            try (PreparedStatement stmt = con.prepareStatement(query)) {
//                for (NewsParams p: params) {
//                    stmt.setString(1, p.getName());
//                    stmt.setString(2, p.getContent());
//                    stmt.setDate(3, new java.sql.Date(p.getDate().getTime()));
//
//                    stmt.executeUpdate();
//                }
//            } catch (SQLException ex) {
//                System.out.println("Error statements: " + ex.getMessage());
//            } catch (NumberFormatException ex) {
//                System.out.println("Error types: " + ex.getMessage());
//            }
//        } catch (Exception ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//    }
//
//    private static  void InsertIntoDB(String strConn) {
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//            System.out.println("Successful connection");
//
//            String query = "INSERT INTO `news` (`name`, `content`, `date`) " +
//                    "VALUES (?, ?, ?);";
//            try (PreparedStatement stmt = con.prepareStatement(query)) {
//                String name, content;
//                Date date;
//                System.out.print("Enter name: ");
//                in.nextLine();
//                name = in.nextLine();
//                System.out.print("Enter date(dd/mm/yyyy): ");
//                date = new SimpleDateFormat("dd/mm/yyyy").parse(in.nextLine());
//                System.out.print("Enter description: ");
//                content = in.nextLine();
//
//
//                stmt.setString(1, name);
//                stmt.setDate(3, new java.sql.Date(date.getTime()));
//                stmt.setString(2, content);
//
//                stmt.executeUpdate();
//
//                System.out.println("Successful insert");
//            } catch (SQLException ex) {
//                System.out.println("Error statements: " + ex.getMessage());
//            } catch (NumberFormatException ex) {
//                System.out.println("Error types: " + ex.getMessage());
//            } catch (ParseException ex) {
//                System.out.println("Error parse: " + ex.getMessage());
//            }
//        } catch (Exception ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//    }
//
//    private static void UpdateForDB(String strConn) {
//        SelectFromDB(strConn);
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//
//            String query = "UPDATE news SET name = ? WHERE id = ?";
//            try(PreparedStatement stmt = con.prepareStatement(query)) {
//                System.out.print("\nEnter id: ");
//                int id = in.nextInt();
//                System.out.print("Enter new name: ");
//                in.nextLine();
//                String name = in.nextLine();
//
//                List<Integer> ids = SelectIdFromDB(strConn);
//                for (int i: ids) {
//                    if(id == i) {
//                        stmt.setInt(2, id);
//                        stmt.setString(1, name);
//
//                        stmt.executeQuery();
//
//                        System.out.println("Successful update");
//                        return;
//                    }
//                }
//                throw new ArrayIndexOutOfBoundsException("Invalid index");
//
//            } catch (SQLException ex) {
//                System.out.println("Error update:" + ex.getMessage());
//            } catch (ArrayIndexOutOfBoundsException ex) {
//                System.out.println("Error : " + ex.getMessage());
//            }
//        } catch (SQLException ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//    }
//
//    private static void DeleteFromDB(String strConn) {
//        SelectFromDB(strConn);
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//            String query = "DELETE FROM news WHERE id = ?";
//            try(PreparedStatement stmt = con.prepareStatement(query)) {
//                System.out.print("Enter Id: ");
//                int id = in.nextInt();
//
//                List<Integer> ids = SelectIdFromDB(strConn);
//                for (int i: ids) {
//                    if(id == i) {
//                        stmt.setInt(1, id);
//                        stmt.executeQuery();
//                        System.out.println("Successful delete");
//                        return;
//                    }
//                }
//                throw new ArrayIndexOutOfBoundsException("Invalid id");
//
//            } catch (SQLException ex) {
//                System.out.println("Error delete: " + ex.getMessage());
//            } catch (ArrayIndexOutOfBoundsException ex) {
//                System.out.println("Error: " + ex.getMessage());
//            }
//
//        } catch (Exception ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//
//    }
//
//    private static List<Integer> SelectIdFromDB(String strConn) {
//        try(Connection con = DriverManager.getConnection(strConn, "root", "")) {
//            String selectSql = "SELECT * FROM news";
//            try {
//                PreparedStatement ps = con.prepareStatement(selectSql);
//                ResultSet resultSet = ps.executeQuery();
//                List<Integer> products = new ArrayList<>();
//                while (resultSet.next()) {
//                    products.add(resultSet.getInt("id"));
//                }
//                return products;
//            } catch (SQLException ex) {
//                System.out.println("Error executeQuery: " + ex.getMessage());
//            }
//        } catch (SQLException ex) {
//            System.out.println("Error connection: " + ex.getMessage());
//        }
//        return null;
//    }
//
//    private static void Seeder(String strConn) throws ParseException {
//        if(SelectIdFromDB(strConn).size() == 0) {
//            System.out.println("Choose locale:\n" +
//                    "1) - en-GB\n" +
//                    "2) - ru");
//            int menu = in.nextInt();
//
//            Faker fake = null;
//
//            switch (menu) {
//                case 1:
//                    fake = new Faker(new Locale("en-GB"), new Random());
//                    break;
//                case 2:
//                    fake = new Faker(new Locale("ru"), new Random());
//                    break;
//                default:
//                    System.out.println("Invalid index");
//                    break;
//            }
//
//            if(fake != null) {
//                int count = 0;
//                List<NewsParams> params = new ArrayList<>();
//                while (count != 1000) {
//                    params.add(new NewsParams(fake.esports().event(),
//                            ("" + fake.address().cityName() + " " + fake.commerce().productName()),
//                            fake.date().between(new SimpleDateFormat("dd.mm.yyyy").parse("01.01.1800"), new SimpleDateFormat("dd.mm.yyyy").parse("01.01.2023"))));
//                    ++count;
//                }
//                AutoInsertIntoDB(strConn, params);
//            }
//        }
//    }
//}
