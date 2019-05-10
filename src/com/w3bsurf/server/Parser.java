package com.w3bsurf.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Parser {
	
	// Reads and parses csv file and then inserts it into database
	public static Exception parseCsv(String fileLocation) {
		Exception i = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileLocation)));
			Class.forName("com.mysql.jdbc.Driver");
			// Connect to MySQL database
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/csvdb?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT", "root", "password");
			// Set MySQL prepared statement
			String stmt = "INSERT INTO `csv_table`"
					+ "(`col_1`, `col_2`, `col_3`) VALUES "
					+ "(?, ?, ?)";
			PreparedStatement pstmt = connection.prepareStatement(stmt);
			String line = null;
			String[] st = null;
			
			// Parses csv file line by line
			// Sets prepared statement values and adds to batch
			while ((line = reader.readLine()) != null) {
				st = line.replace("\"","").split(",");
				pstmt.setString(1,  st[0]);
				pstmt.setString(2,  st[1]);
				pstmt.setString(3,  st[2]);
				pstmt.addBatch();
			}
			
			// Executes prepared statement and closes connection
			pstmt.executeBatch();
			connection.close();
			reader.close();
			
		} catch (IOException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return e;
		} 
		return i;
	}
}
