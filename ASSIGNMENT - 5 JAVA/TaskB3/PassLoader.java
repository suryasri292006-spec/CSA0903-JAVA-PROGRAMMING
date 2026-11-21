import java.sql.*;
import java.util.List;
public class PassLoader {
 public static int loadAll(Connection c,List<String[]> rows)throws SQLException{
  String q="INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES(?,?,?,?,?)";
  boolean old=c.getAutoCommit();c.setAutoCommit(false);
  try(PreparedStatement p=c.prepareStatement(q)){
   int n=0,pending=0;
   for(String[] r:rows){
    if(r==null||r.length!=5)throw new IllegalArgumentException("Expected five fields");
    p.setString(1,r[0]);p.setString(2,r[1]);p.setString(3,r[2]);
    p.setInt(4,Integer.parseInt(r[3]));p.setDouble(5,Double.parseDouble(r[4]));
    p.addBatch();n++;pending++;
    if(pending==1000){p.executeBatch();pending=0;}
   }
   if(pending>0)p.executeBatch();
   c.commit();return n;
  }catch(SQLException|RuntimeException e){c.rollback();throw e;}
  finally{c.setAutoCommit(old);}
 }
}
