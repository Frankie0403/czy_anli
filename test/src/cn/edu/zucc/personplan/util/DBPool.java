package cn.edu.zucc.personplan.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBPool {
	private static DBPool dbPool;
	private ComboPooledDataSource dataSource;

	static {
		dbPool = new DBPool();
	}

	public DBPool() {
		try {
			dataSource = new ComboPooledDataSource();
			dataSource.setUser("root");
			dataSource.setPassword("123");
			dataSource
					.setJdbcUrl("jdbc:mysql://localhost:3306/plan");
			dataSource.setDriverClass("com.mysql.jdbc.Driver");
			dataSource.setInitialPoolSize(2);
			dataSource.setMinPoolSize(1);
			dataSource.setMaxPoolSize(10);
			dataSource.setMaxStatements(50);
			dataSource.setMaxIdleTime(60);
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
	}
	

	public final static DBPool getInstance() {
		return dbPool;
	}

	public final Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("无法从数据源获取连接 ", e);
		}
	}

//	public static void main(String[] args) throws SQLException {
//		Connection con = null;
//		try {
//			con = DBPool.getInstance().getConnection();
//			java.sql.ResultSet rs=con.createStatement().executeQuery("select id from tbl_logger_visit");
//			while(rs.next())
//				System.out.println(rs.getString(1));
//		} catch (Exception e) {
//		} finally {
//			if (con != null)
//				con.close();
//		}
//	}
}