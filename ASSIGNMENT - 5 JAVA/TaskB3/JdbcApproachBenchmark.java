import java.sql.*;
public class JdbcApproachBenchmark {
 static String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/buspass_db");
 static String user=System.getenv().getOrDefault("DB_USER","root"),pw=System.getenv().getOrDefault("DB_PASSWORD","");
 static void clear(Connection c)throws SQLException{try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}}
 static long statement(Connection c,int n)throws Exception{
  clear(c);long t=System.nanoTime();
  try(Statement s=c.createStatement()){for(int i=1;i<=n;i++)s.executeUpdate(
   "INSERT INTO bus_pass VALUES('S"+i+"','R"+i+"','Student"+i+"',1,1000.0)");}
  return System.nanoTime()-t;
 }
 static long prepared(Connection c,int n)throws Exception{
  clear(c);long t=System.nanoTime();
  try(PreparedStatement p=c.prepareStatement("INSERT INTO bus_pass VALUES(?,?,?,?,?)")){
   for(int i=1;i<=n;i++){p.setString(1,"P"+i);p.setString(2,"R"+i);p.setString(3,"Student"+i);p.setInt(4,1);p.setDouble(5,1000);p.executeUpdate();}
  }return System.nanoTime()-t;
 }
 static long batch(Connection c,int n)throws Exception{
  clear(c);boolean old=c.getAutoCommit();c.setAutoCommit(false);long t=System.nanoTime();
  try(PreparedStatement p=c.prepareStatement("INSERT INTO bus_pass VALUES(?,?,?,?,?)")){
   int b=0;for(int i=1;i<=n;i++){p.setString(1,"B"+i);p.setString(2,"R"+i);p.setString(3,"Student"+i);p.setInt(4,1);p.setDouble(5,1000);p.addBatch();
    if(++b==1000){p.executeBatch();b=0;}}
   if(b>0)p.executeBatch();c.commit();
  }catch(Exception e){c.rollback();throw e;}finally{c.setAutoCommit(old);}
  return System.nanoTime()-t;
 }
 public static void main(String[] a)throws Exception{
  int n=a.length>0?Integer.parseInt(a[0]):1000;
  try(Connection c=DriverManager.getConnection(url,user,pw)){
   System.out.println("Rows,Statement_ms,Prepared_ms,Batch_ms");
   System.out.printf("%d,%.3f,%.3f,%.3f%n",n,statement(c,n)/1e6,prepared(c,n)/1e6,batch(c,n)/1e6);
  }
 }
}
