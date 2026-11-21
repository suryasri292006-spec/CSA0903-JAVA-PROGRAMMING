import java.sql.*;
public class DatabaseInspection {
 public static void main(String[] a)throws Exception{
  String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/buspass_db");
  try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),System.getenv().getOrDefault("DB_PASSWORD",""))){
   DatabaseMetaData m=c.getMetaData();
   System.out.println("Database="+m.getDatabaseProductName()+" "+m.getDatabaseProductVersion());
   System.out.println("Driver="+m.getDriverName()+" "+m.getDriverVersion());
   try(ResultSet r=m.getTables(c.getCatalog(),null,"%",new String[]{"TABLE"})){
    System.out.println("Tables:");while(r.next())System.out.println("- "+r.getString("TABLE_NAME"));
   }
  }
 }
}
