package com.sqlite;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqLiteJDBC implements Closeable {

    private Connection conn;
    private String tableName;
    /** 基线版本 */
    private String baseTabel;

    // dbUrl jdbc:sqlite:D:\\SqLite\\version-record-xi7.db
    public SqLiteJDBC(String version, String baseVersion, String dbUrl) throws SQLException {
        this.tableName = "item" + version.replaceAll("\\.", "_");
        this.baseTabel = "item" + baseVersion.replaceAll("\\.", "_");
        this.conn = DriverManager.getConnection(dbUrl);
    }

    public static void main(String[] args) {

        try (SqLiteJDBC db = new SqLiteJDBC("v1.7.3", "v1.7.2", "jdbc:sqlite:D:\\SqLite\\version-record-xi7.db")) {
            // 连接SQLite数据库，数据库文件是test.db，如果文件不存在，会自动创建

            // db.dropTable();
            // db.createTable();

            // Item item = new Item();
            // // 给item所有字段赋值
            // item.setId("a101");
            // item.setName("a101name");
            // item.setType("1");
            // item.setProp("2");
            // item.setDropplace("3");
            // item.setShop("4");
            // item.setRemark("5");
            // db.insertData(item);

            // db.queryAddDiffSql();
            // db.queryDelDiffSql();
            // db.queryPropDiffSql();
            // db.queryDropPlaceDiffSql();
            db.queryShopDiffSql();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + this.tableName;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + this.tableName + " (" +
                "id VARCHAR 'id' PRIMARY KEY ," +
                "name VARCHAR '名称'," +
                "type VARCHAR '类型'," +
                "prop VARCHAR '属性'," +
                "dropplace VARCHAR '获取途径'," +
                "shop VARCHAR '神秘商店是否出售'," +
                "remark VARCHAR '备注'" +
                ")";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }

    public void insertData(Item item) throws SQLException {
        String sql = "INSERT INTO " + this.tableName
                + " (id,name,type,prop,dropplace,shop,remark) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 0;
            pstmt.setString(++i, item.getId());
            pstmt.setString(++i, item.getName());
            pstmt.setString(++i, item.getType());
            pstmt.setString(++i, item.getProp());
            pstmt.setString(++i, item.getDropplace());
            pstmt.setString(++i, item.getShop());
            pstmt.setString(++i, item.getRemark());
            pstmt.executeUpdate();
        }
    }

    public void queryAddDiffSql() throws SQLException {
        // --新增物品查询
        String sql = "select i.id,i.name,i.type,i.prop,i.dropplace,i.shop,i.remark " +
                "from " + tableName + " i " +
                "left join " + baseTabel + " i2 on i2.id = i.id " +
                "where i2.id is null";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.printf("ID: %s, Name: %s%n", id, name);
            }
        }
    }

    public void queryDelDiffSql() throws SQLException {
        // --删除物品查询
        String sql = "select i.id,i.name,i.type,i.prop,i.dropplace,i.shop,i.remark " +
                "from " + baseTabel + " i " +
                "left join " + tableName + " i2 on i2.id = i.id " +
                "where i2.id is null";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.printf("ID: %s, Name: %s%n", id, name);
            }
        }
    }
    
    public void queryPropDiffSql() throws SQLException {
        // --删除物品查询
        String sql = "select i.id,i.name,i.type,i.prop,i.dropplace,i.shop,i.remark " +
                "from " + tableName + " i " +
                "left join " + baseTabel + " i2 on i2.id = i.id " +
                "where i.prop != i2.prop";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.printf("ID: %s, Name: %s%n", id, name);
            }
        }
    }
    public void queryDropPlaceDiffSql() throws SQLException {
        // --删除物品查询
        String sql = "select i.id,i.name,i.type,i.prop,i.dropplace,i.shop,i.remark " +
                "from " + tableName + " i " +
                "left join " + baseTabel + " i2 on i2.id = i.id " +
                "where i.dropplace != i2.dropplace";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.printf("ID: %s, Name: %s%n", id, name);
            }
        }
    }

    public void queryShopDiffSql() throws SQLException {
        // --删除物品查询
        String sql = "select i.id,i.name,i.type,i.prop,i.dropplace,i.shop,i.remark " +
                "from " + tableName + " i " +
                "left join " + baseTabel + " i2 on i2.id = i.id " +
                "where i.shop != i2.shop";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.printf("ID: %s, Name: %s%n", id, name);
            }
        }
    }

    public void queryData() throws SQLException {
        String sql = "SELECT * FROM " + this.tableName + "";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.printf("ID: %d, Name: %s, Age: %d%n", id, name, age);
            }
        }
    }

    public void updateData() throws SQLException {
        String sql = "UPDATE users SET age = ? WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 31);
            pstmt.setString(2, "Alice");
            pstmt.executeUpdate();
        }
    }

    public void deleteData() throws SQLException {
        String sql = "DELETE FROM users WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Bob");
            pstmt.executeUpdate();
        }
    }

    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                // 关闭连接
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}