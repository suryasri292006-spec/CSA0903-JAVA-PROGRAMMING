import java.sql.*;
public class WalletTransfer {
 public static void transfer(Connection c,int from,int to,double amount)throws SQLException{
  if(amount<=0||from==to)throw new IllegalArgumentException("Invalid transfer");
  boolean old=c.getAutoCommit();c.setAutoCommit(false);
  try{
   int n;
   try(PreparedStatement p=c.prepareStatement(
     "UPDATE wallet SET balance=balance-? WHERE wallet_id=? AND balance>=?")){
    p.setDouble(1,amount);p.setInt(2,from);p.setDouble(3,amount);n=p.executeUpdate();
   }
   if(n!=1)throw new SQLException("Missing source or insufficient funds");
   try(PreparedStatement p=c.prepareStatement(
     "UPDATE wallet SET balance=balance+? WHERE wallet_id=?")){
    p.setDouble(1,amount);p.setInt(2,to);
    if(p.executeUpdate()!=1)throw new SQLException("Destination missing");
   }
   c.commit();
  }catch(SQLException|RuntimeException e){c.rollback();throw e;}
  finally{c.setAutoCommit(old);}
 }
}
