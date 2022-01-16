package com.github.idimabr.raphazombie.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.utils.ConfigUtil;
import org.bukkit.Bukkit;

public class MySQL {

	private ConfigUtil config = RaphaZombie.getInstance().config;
	private Connection connection;
	private PreparedStatement smt;

	public MySQL() {
		try {
			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
			String host = config.getString("MySQL.Host"); 
			String database = config.getString("MySQL.Database");
			String url = "jdbc:mysql://" + host + "/" + database;
			String username = config.getString("MySQL.Username");
			String password = config.getString("MySQL.Password");
			connection = DriverManager.getConnection(url, username, password);
			RaphaZombie.getInstance().getLogger().info("§cConexao com banco de dados foi estabelecida.");
		} catch (Exception e) {
			RaphaZombie.getInstance().getLogger().info("§cOcorreu um erro no banco de dados");
			Bukkit.getPluginManager().disablePlugin(RaphaZombie.getInstance());
		}
	}
	
	public void createTable() {
		try {
			smt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players(`id` int(11) NOT NULL AUTO_INCREMENT, `UUID` varchar(36) NOT NULL, `infected` boolean NOT NULL, `blood` boolean NOT NULL, `sede` int(100) NOT NULL, `abates` int(255) NOT NULL, PRIMARY KEY (`id`))");
			smt.executeUpdate();
		} catch (SQLException e) {
			RaphaZombie.getInstance().getLogger().info("§cErro na criação da tabela de players no MYSQL");
		}
	}

	public Connection getConnectionMySQL() {
		return connection;
	}
	
	public boolean contains(String uuid) {
		try {
			smt = connection.prepareStatement("SELECT `UUID` FROM players WHERE `UUID` = ?");
			smt.setString(1, uuid);
			ResultSet result = smt.executeQuery();
			if(result.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ResultSet getString(String table, String coluna, String valor) {
		try {
			String sql = "SELECT * FROM " + table + " WHERE ? = ?";
			smt = connection.prepareStatement(sql);
			smt.setString(1, coluna);
			smt.setString(2, valor);
			ResultSet result = smt.executeQuery();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void executeUpdateMySQL(String query) {
		try {
			smt = connection.prepareStatement(query);
			smt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean close() {
		try {
			if(getConnectionMySQL() != null) {
				getConnectionMySQL().close();
			}else {
				return false;
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public Connection restart() {
		close();
		return getConnectionMySQL();
	}
}