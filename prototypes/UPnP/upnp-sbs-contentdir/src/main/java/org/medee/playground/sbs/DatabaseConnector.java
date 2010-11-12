package org.medee.playground.sbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;


public class DatabaseConnector {

	public Connection slimDB;
	public Statement s;
	private Vector<Contributor> cv = null; 
	
	
	public DatabaseConnector() {
		super();
		try {
			Properties prop = new Properties();
			prop.setProperty("characterEncoding", "utf8");
			prop.setProperty("useBlobToStoreUTF8OutsideBMP", "true");
			prop.setProperty("user", "aeazezaeaze");
			prop.setProperty("password", "testpass");
			prop.setProperty("useUnicode", "yes");
			slimDB =  DriverManager.getConnection("jdbc:mysql://192.168.1.3:9092/slimserver", prop);
			s = slimDB.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printAlbums(String artistName) {
		String query = "select distinct a.title, a.titlesort "
		+ "from " 
		+	"contributors as c, " 
		+	"contributor_album as ca," 
		+	"albums as a " 
		+"where c.namesort = '"+artistName+"' and c.id = ca.contributor and ca.album = a.id;";

		ResultSet rs;
		try {
			rs = s.executeQuery(query);
			while (rs.next ()) {
// workaround needed if useBlobToStoreUTF8OutsideBMP isn't set to "true"
//				System.out.println("title: "+new String(rs.getBytes("title")) + "\n" +
				System.out.println("title: "+rs.getString("title") + "\n" +
									"titlesort: "+rs.getString("titlesort"));
				//Clob b =rs.getClob("title");
// also works but more verbose				
//				Blob b =rs.getBlob("title");
//				System.err.println(new String(b.getBytes(1,(int)b.length()) ));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Vector<Contributor> getContributorsVector() {

		if( cv != null ) {
			return cv;
		}

		String query = "select  distinct c.id, c.name, c.namesort "
			+ "from " 
			+	"contributors as c;";
		
			
	 	cv = new Vector<Contributor>();
		ResultSet rs;
		try {
			rs = s.executeQuery(query);
			while (rs.next ()) {
				Contributor c = new Contributor();
				cv.add(c);
				c.setId(rs.getInt("id"));
				c.setName(rs.getString("name"));
				c.setNamesort(rs.getString("namesort"));
				System.out.println("title: "+rs.getString("name") + "\n" +
									"titlesort: "+rs.getString("namesort"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cv;
	}
	
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		DatabaseConnector dc = new DatabaseConnector();
		System.out.println(dc.slimDB.getCatalog());
		System.out.println("Encoding: "+System.getProperty("file.encoding"));
		System.out.println("Locale: "+Locale.getDefault().getDisplayLanguage());
		dc.printAlbums("a dominique");
	}

}
