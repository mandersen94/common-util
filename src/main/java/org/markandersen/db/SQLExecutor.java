package org.markandersen.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * 
 * @author MAndersen
 */
public class SQLExecutor {

	private File sqlFile;

	private String file;

	private Connection connection;

	private boolean autoCommit = true;

	private String classpathResource;

	private String userId;

	private String password;

	private String url;

	private String driver;

	/**
	 * execute the sql statement(s).
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public void executeSql() throws IOException, SQLException {

		initFile();
		initConnection();
		List<String> sqlCommands = createSQLCommands();
		if (autoCommit) {
			connection.setAutoCommit(true);
		} else {
			connection.setAutoCommit(false);
		}
		for (String singleStatement : sqlCommands) {
			executeSingleSQLStatement(singleStatement);
		}
		if (!autoCommit) {
			// if we haven't commited already, commit now.
			connection.commit();
		}
	}

	private List<String> createSQLCommands() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(sqlFile));
		List<String> commands = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("")) {
				// blank line
				continue;
			}

			if (line.startsWith("#")) {
				// commented out.
				continue;
			}

			commands.add(line);
			line = null;
		}

		return commands;
	}

	/**
	 * 
	 * 
	 */
	private void initFile() {
		if (classpathResource != null) {
			URL url = this.getClass().getClassLoader().getResource(
					classpathResource);
			sqlFile = new File(url.getFile());
		} else if (file != null) {
			sqlFile = new File(file);
		} else if (sqlFile != null) {
			// nop
		} else {
			throw new RuntimeException("file or classpathResource not set.");
		}

		if (!sqlFile.exists()) {
			throw new RuntimeException("file " + sqlFile + " not found.");
		}

	}

	private void initConnection() throws SQLException {
		if (connection == null) {
			connection = getNewConnection();
		}
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getNewConnection() throws SQLException {
		if (userId == null) {
			throw new RuntimeException("User Id attribute must be set!");
		}
		if (password == null) {
			throw new RuntimeException("Password attribute must be set!");
		}
		if (url == null) {
			throw new RuntimeException("Url attribute must be set!");
		}
		System.out.println("connecting to " + getUrl());
		Properties info = new Properties();
		info.put("user", getUserId());
		info.put("password", getPassword());
		Connection conn = getDriver().connect(getUrl(), info);

		if (conn == null) {
			// Driver doesn't understand the URL
			throw new SQLException("No suitable Driver for " + url);
		}

		return conn;

	}

	private Driver getDriver() {
		try {
			Class<?> dc = Class.forName(driver);

			Driver driverInstance = (Driver) dc.newInstance();
			return driverInstance;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Problesm loading driver " + driver
					+ ". " + e.toString());
		}
	}

	/**
	 * 
	 * @param singleStatement
	 */
	private void executeSingleSQLStatement(String singleStatement) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			boolean result = statement.execute(singleStatement);
			int updateCount = statement.getUpdateCount();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(statement);
		}
	}

	/**
	 * closes the statement.
	 * 
	 * @param statement
	 */
	private void closeStatement(Statement statement) {
		if (statement == null) {
			return;
		}
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public File getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(File sqlFile) {
		this.sqlFile = sqlFile;
	}

	public String getClasspathResource() {
		return classpathResource;
	}

	public void setClasspathResource(String classpathResource) {
		this.classpathResource = classpathResource;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}