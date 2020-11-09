package arsh;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBWorker {
    public static final String PATH_TO_DB_FILE = "database.db";
    public static final String URL = "jdbc:sqlite:" + PATH_TO_DB_FILE;
    public static Connection conn;

    public static void initDB() {
        try {
            conn = DriverManager.getConnection(URL);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Драйвер: " + meta.getDriverName());
                //createDB();
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка подключения к БД: " + ex);
        }
    }

    public static void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public static void createDB() throws SQLException {
        Statement statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'groups' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'title' text);");
        System.out.println("Таблица создана или уже существует.");
        statmt.execute("CREATE TABLE if not exists 'students' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'lastname' text,'name' text,'email' text, 'group_id' INTEGER NOT NULL, FOREIGN KEY (group_id) REFERENCES groups (id));");
        System.out.println("Таблица создана или уже существует.");
    }

    public static void addGroup(String data) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO groups(`title`) " +
                        "VALUES(?)");
        statement.setObject(1, data);
        statement.execute();
        statement.close();
    }

    public static void addStudent(Student student) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO students(`lastname`,`name`,`group_id`) " +
                        "VALUES(?,?,?)");
            statement.setObject(1, student.getLastname());
            statement.setObject(2, student.getName());
            statement.setObject(3, student.getGroup());
            statement.execute();
        statement.close();
    }
    public static int getGroupId(String grName) throws SQLException {

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id FROM groups WHERE groups.title ='"+grName+"'");
       int grId = -1;
       grId = resultSet.getInt(1);
       resultSet.close();
       statement.close();
       return grId;
    }
    public static String getGroupName(int grId) throws SQLException {

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT title FROM groups WHERE groups.id ="+grId);
        String grName = "";
        grName = resultSet.getString(1);
        resultSet.close();
        statement.close();
        return grName;
    }
    public static List<String> getAllGroups() throws SQLException {

        Statement statement = conn.createStatement();
            List<String> list = new ArrayList<String>();
            ResultSet resultSet = statement.executeQuery("SELECT id, title FROM groups");
            while (resultSet.next()) {
                list.add(resultSet.getInt("id") + " " + resultSet.getString("title"));
            }
            resultSet.close();
            statement.close();
            return list;
    }

    public static List<Student> getAllStudents() throws SQLException {
        Statement statement = conn.createStatement();
            List<Student> list = new ArrayList<Student>();
            ResultSet resultSet = statement.executeQuery("SELECT students.id, students.lastname, students.name, students.group_id, groups.title FROM students JOIN groups ON groups.id = students.group_id");
            while (resultSet.next()) {
                  list.add(new Student(resultSet.getInt("id"),resultSet.getString("lastname"), resultSet.getString("name"), getGroupName(resultSet.getInt("group_id"))));
            }
            resultSet.close();
            statement.close();
            return list;
    }

    public static void deleteStudent(Student student) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("DELETE FROM students WHERE students.id ="+student.getId());
        System.out.println("deleted!");
        statement.close();
    }
}
