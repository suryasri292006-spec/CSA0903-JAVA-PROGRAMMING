import java.sql.*;
public class PreparedLogin {
 public static boolean login(Connection c,String id,String pwd)throws SQLException{
  String q="SELECT user_id FROM app_user WHERE user_id=? AND password_text=?";
  try(PreparedStatement p=c.prepareStatement(q)){
   p.setString(1,id);p.setString(2,pwd);
   try(ResultSet r=p.executeQuery()){return r.next();}
  }
 }
}
