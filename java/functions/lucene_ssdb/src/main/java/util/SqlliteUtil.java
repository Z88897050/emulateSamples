package util;

import bean.Constants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SqlliteUtil {

    private static final Logger logger = Logger.getLogger(SqlliteUtil.class);

    private static final BasicDataSource dataSource = new BasicDataSource();
    private static final String dbPath = Paths.get(Constants.appDir, "index.db").toString();

    private SqlliteUtil() {

    }

    static {
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:" + dbPath);
        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(20);
        dataSource.setMaxIdle(20);
        dataSource.setMinIdle(1);
        dataSource.setMaxWaitMillis(1800000);
        checkSchema();
        checkSsdb();
    }

    /**
     * 创建索引的schema信息
     */
    private static void checkSchema() {
        String checkSql = "select count(*) from sqlite_master where type=? and name=?";
        try {
            List<Map<String, Object>> result = query(checkSql, "table", "schema");
            if ((int) result.get(0).get("count(*)") == 0) {
                String createSql = "CREATE TABLE schema(" +
                        "name TEXT PRIMARY KEY NOT NULL, " +
                        "value TEXT NOT NULL" +
                        ")";
                SqlliteUtil.update(createSql);
            }
        } catch (SQLException e) {
            logger.error("初始化schema错误,系统退出", e);
            System.exit(1);
        }
    }

    /**
     * ssdb数据源的增量信息
     * list:offset
     * hash:key_start
     */
    private static void checkSsdb() {
        String checkSql = "select count(*) from sqlite_master where type=? and name=?";
        try {
            List<Map<String, Object>> result = query(checkSql, "table", "ssdb");
            if ((int) result.get(0).get("count(*)") == 0) {
                String createSql = "CREATE TABLE ssdb(" +
                        "name TEXT PRIMARY KEY NOT NULL, " +
                        "point TEXT NOT NULL" +
                        ")";
                SqlliteUtil.update(createSql);
            }
        } catch (SQLException e) {
            logger.error("初始化ssdb错误,系统退出", e);
            System.exit(1);
        }
    }

    public static void close() throws SQLException {
        dataSource.close();
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<List<Map<String, Object>>> h = new MapListHandler();
        return runner.query(sql, h, params);
    }

    public static Object[] insert(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<Object[]> h = new ArrayHandler();
        return runner.insert(sql, h, params);
    }

    public static List<Object[]> updateBatch(String sql, Object[][] params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        return runner.insertBatch(sql, h, params);
    }

    public static Object[] insert(Connection conn, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<Object[]> h = new ArrayHandler();
        return runner.insert(conn, sql, h, params);
    }

    public static List<Object[]> updateBatch(Connection conn, String sql, Object[][] params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        return runner.insertBatch(conn, sql, h, params);
    }

    public static int update(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        return runner.update(sql, params);
    }

    public static int update(Connection conn, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.update(conn, sql, params);
    }

}
