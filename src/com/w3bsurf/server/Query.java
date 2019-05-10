package com.w3bsurf.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Query {
	
	// Queries MySQL database for last "count" records from "field" and stores result into arraylist
	public static ArrayList<String> queryCsv(String field, int count) {
		ArrayList<String> result = new ArrayList<>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Connect to MySQL database
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/csvdb?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT", "root", "password");
			String query = "SELECT `" + field + "` FROM `csv_table` ORDER BY `id` DESC LIMIT " + count + ";";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			// Goes through resultset and adds each field to arraylist
			while (rs.next()) {
				result.add(rs.getString(field));
			}
			connection.close();
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}