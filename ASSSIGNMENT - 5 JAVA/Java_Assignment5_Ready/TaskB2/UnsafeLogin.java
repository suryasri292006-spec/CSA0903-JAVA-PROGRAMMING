import java.sql.*;
/** Intentionally vulnerable demonstration. Use only in the local test database. */
public class UnsafeLogin {
 public static boolean login(Connection c,String id,String pwd)throws SQLException{
  String q="SELECT user_id FROM app_user WHERE user_id='"+id+"' AND password_text='"+pwd+"'";
  try(Statement s=c.createStatement();ResultSet r=s.executeQuery(q)){return r.next();}
 }
}
