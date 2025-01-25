package com.abc.sqlite;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.abc.ItemDetail;

public class SqLiteJDBC implements Closeable {

    private static String DB_URL = "jdbc:sqlite:D:\\SqLite\\version-record-xi7.db";
    /** 当前版本表名 */
    private static String tableName;
    /** 基线版本表名 */
    private static String baseTabel;

    private static String baseVersion ;

    public static String getBaseVersion() {
        return baseVersion;
    }

    /** 数据库连接 */
    private Connection conn;

    public static void setVersion(String version, String baseVersion) throws SQLException {
        tableName = "item" + version.replaceAll("\\.", "_");
        baseTabel = "item" + baseVersion.replaceAll("\\.", "_");
        SqLiteJDBC.baseVersion = baseVersion;
        try (SqLiteJDBC db = new SqLiteJDBC();) {
            db.dropTable();
            db.createTable();
        }
    }

    public SqLiteJDBC() throws SQLException {
        this.conn = DriverManager.getConnection(DB_URL);
    }

    public static void main(String[] args) {

        try (SqLiteJDBC db = new SqLiteJDBC()) {
            SqLiteJDBC.setVersion("v1.7.3", "v1.7.2");
            // 连接SQLite数据库，数据库文件是test.db，如果文件不存在，会自动创建

            // db.dropTable();
            // db.createTable();

            // ItemDetail item = new ItemDetail("a101");
            // // 给item所有字段赋值
            // item.setId("a101");
            // item.setName("a101name");
            // item.setLevel("S+");
            // item.setType("1");
            // item.setDescription("2");
            // item.setDropPlace("3");
            // item.setShop("4");
            // item.setMark("5");
            // db.insertData(item);

            List<ItemDetail> list = db.queryAddDiffSql();
            System.out.println(list);
            // db.queryDelDiffSql();
            // db.queryPropDiffSql();
            // db.queryDropPlaceDiffSql();
            // db.queryShopDiffSql();
            // db.queryCountData(db.tableName);
            // db.queryCountData(db.baseTabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id VARCHAR 'id' PRIMARY KEY ," +
                "name VARCHAR '名称'  NOT NULL UNIQUE," +
                "level VARCHAR '等级' NOT NULL," +
                "type VARCHAR '类型' NOT NULL," +
                "description VARCHAR '属性' NOT NULL," +
                "dropPlace VARCHAR '获取途径' NOT NULL," +
                "shop VARCHAR '神秘商店是否出售' NOT NULL," +
                "hero VARCHAR '英雄专属' NOT NULL," +
                "mark VARCHAR '备注' NOT NULL" +
                ")";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }

    public void insertData(ItemDetail item) throws SQLException {
        String sql = "INSERT INTO " + tableName
                + " (id,name,level,type,description,dropPlace,shop,hero,mark) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 0;
            pstmt.setString(++i, defautlString(item.getId()));
            pstmt.setString(++i, defautlString(item.getName()));
            pstmt.setString(++i, defautlString(item.getLevel()));
            pstmt.setString(++i, defautlString(item.getType()));
            pstmt.setString(++i, defautlString(item.getDescription()));
            pstmt.setString(++i, defautlString(item.getDropPlace()));
            pstmt.setString(++i, defautlString(item.getShop()));
            pstmt.setString(++i, defautlString(item.getHero()));
            pstmt.setString(++i, defautlString(item.getMark()));
            pstmt.executeUpdate();
        }
    }

    public String defautlString(String str) {
        return str == null ? "" : str;
    }

    private static String SELECT_ALL_FIELD = "select i.id,i.name,i.level,i.type,i.description,i.dropPlace,i.shop,i.hero,i.mark ";

    private List<ItemDetail> handleResult(ResultSet rs) throws SQLException {
        // 获取 ResultSetMetaData
        ResultSetMetaData metaData = rs.getMetaData();
        // 获取列数
        int columnCount = metaData.getColumnCount();

        // 打印所有字段名称
        Set<String> fieldNameList = new HashSet<>();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            fieldNameList.add(columnName);
        }

        List<ItemDetail> resultList = new ArrayList<>();
        while (rs.next()) {
            ItemDetail item = new ItemDetail("");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                try {
                    Field f = ItemDetail.class.getDeclaredField(columnName);
                    f.setAccessible(true);
                    f.set(item, rs.getObject(i));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("属性为匹配到!!" + columnName + ":" + i);
                    throw new RuntimeException("map转对象失败");
                }
            }
            resultList.add(item);
        }
        return resultList;
    }

    public List<ItemDetail> queryAddDiffSql() throws SQLException {
        // --新增物品查询
        String sql = SELECT_ALL_FIELD +
                "from " + tableName + " i " +
                "left join " + baseTabel + " i2 on i2.id = i.id " +
                "where i2.id is null";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            return handleResult(rs);
        }
    }

    public List<ItemDetail> queryDelDiffSql() throws SQLException {
        // --删除物品查询
        String sql = SELECT_ALL_FIELD +
                "from " + baseTabel + " i " +
                "left join " + tableName + " i2 on i2.id = i.id " +
                "where i2.id is null";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            return handleResult(rs);
        }
    }

    public List<ItemDetail> queryPropDiffSql() throws SQLException {
        // --属性修改查询
        String sql = SELECT_ALL_FIELD +
                "from " + tableName + " i " +
                "inner join " + baseTabel + " i2 on i2.id = i.id " +
                "where i.description != i2.description";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            return handleResult(rs);
        }
    }

    public List<ItemDetail> queryDropPlaceDiffSql() throws SQLException {
        // --掉落查询
        String sql = SELECT_ALL_FIELD +
                "from " + tableName + " i " +
                "inner join " + baseTabel + " i2 on i2.id = i.id " +
                "where i.dropplace != i2.dropplace";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            return handleResult(rs);
        }
    }

    public List<ItemDetail> queryShopDiffSql() throws SQLException {
        // --商店查询
        String sql = SELECT_ALL_FIELD + ",i2.shop shop2 " +
                "from " + tableName + " i " +
                "inner join " + baseTabel + " i2 on i2.id = i.id " +
                "where i.shop != i2.shop";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            return handleResult(rs);
        }
    }

    public void queryCountData(String tableName) throws SQLException {
        String sql = "SELECT count(*) FROM " + tableName + "";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("count(*)");
                System.out.printf("count(*): %d%n", id);
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