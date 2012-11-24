package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Fizz extends Thread
{
	private static final String url = "jdbc:postgresql://localhost:5432/pilot-web?charset=utf-8";
	private static final String username = "pilot";
	private static final String password = "pilot";

	public static void main(String... obj)
	{
		Connection conn = null;

		try
		{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(url, username, password);
			
			PreparedStatement stat = conn.prepareStatement("select * from bgd_measurement_kind where id >= ?");
			stat.setInt(1, 25179);
			
			ResultSet rs = stat.executeQuery();
			
			while(rs.next()) {
				System.out.println("deleted: " + rs.getBoolean("deleted"));
				System.out.println("description: " + rs.getString("description"));
				System.out.println("name: " + rs.getString("name"));
				System.out.println("shortname: " + rs.getString("shortname"));
				System.out.println("id: " + rs.getInt("id"));
				System.out.println("---------------");
			}
			
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn != null)
				try
				{
					conn.close();
				}
				catch(SQLException e)
				{}
		}

	}
}
