import java.sql.*;import java.util.*;
public class PassLoaderDemo {
 public static void main(String[] a)throws Exception{
  String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/buspass_db");
  String user=System.getenv().getOrDefault("DB_USER","root");
  String pw=System.getenv().getOrDefault("DB_PASSWORD","");
  int n=a.length>0?Integer.parseInt(a[0]):1000;
  try(Connection c=DriverManager.getConnection(url,user,pw)){
   try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}
   List<String[]> rows=new ArrayList<>();
   for(int i=1;i<=n;i++)rows.add(new String[]{"P"+i,"R"+i,"Student"+i,""+(i%20+1),""+(1000+i)});
   long t=System.nanoTime();System.out.println("Rows loaded="+PassLoader.loadAll(c,rows));
   System.out.printf("Elapsed ms=%.3f%n",(System.nanoTime()-t)/1e6);
  }
 }
}
