/**
 * # Hiver Server execute
 * $ hiveserver2 &
 * 
 * # Then on the command-line; complie
 * $ javac HiveJdbcClient.java
 * 
 * # Then on the command-line; execute
 * $ HiveJdbcClient.sh
 * 
 * # Cron setting
 * $ crontab -e
 *   10 0 * * * /opt/HiveJdbcClient.sh
 */
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HiveJdbcClient {
  private static String driverName = "org.apache.hive.jdbc.HiveDriver";
   /**
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {
    Date from = new Date();
    SimpleDateFormat transFormat = new SimpleDateFormat("yyyymmdd");
    String toFileName = transFormat.format(from) + ".log";
    String toTblName = transFormat.format(from);
    // local file location define
    String filepath = "/tmp/" + toFileName;
    
    System.out.println(toFileName);
    URL url = null; 
    try { 
      //  You change domain-name
      url = new URL("http://www.kjhak.xyz/logs/" + toFileName); 
      URLConnection httpCon = url.openConnection();
      HttpURLConnection exitCode = (HttpURLConnection)httpCon;
      if (exitCode.getResponseCode() == 200) {
        System.out.println("url=["+url+"]");
        System.out.println("protocol=["+url.getProtocol()+"]");
        System.out.println("host=["+url.getHost()+"]");
        System.out.println("port=["+url.getPort()+"]");
        System.out.println("file=["+url.getFile()+"]");
        System.out.println("ref=["+url.getRef()+"]");
        try (InputStream in = url.openStream()) {
          Files.copy(in, Paths.get(filepath),StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } 
      System.out.println(exitCode.getResponseCode());
    } catch (Exception e) { 
      e.printStackTrace(); 
    } 
    try {
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
    // user-id change : permission error
    Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "hadoop", "akfkskxk");
    Statement stmt = con.createStatement();
    // table name define
    String tableName = "iot_sensor_" + toTblName;
    stmt.execute("drop table if exists " + tableName);
    // table field define
    stmt.execute("create table " + tableName + " ( dt string, locat string, temp float, humi float, pm10 int, pm2 int, ozone float, nitrogen float, carbon float, sulfurous float, sales int) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'");
    String sql = "show tables '" + tableName + "'";
    System.out.println("Running: " + sql);
    ResultSet res = stmt.executeQuery(sql);
    if (res.next()) {
      System.out.println(res.getString(1));
    }
    sql = "describe " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1) + "\t" + res.getString(2));
    }
    // local file location set
    sql = "load data local inpath '" + filepath + "' overwrite into table " + tableName;
    System.out.println("Running: " + sql);
    stmt.execute(sql);
    sql = "select * from " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(String.valueOf(res.getInt(1)));
    }
    sql = "select count(1) from " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1));
    }
  }
}
