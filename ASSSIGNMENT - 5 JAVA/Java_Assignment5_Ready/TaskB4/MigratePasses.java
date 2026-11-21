import java.io.*;import java.nio.charset.StandardCharsets;import java.nio.file.*;import java.sql.*;import java.util.*;
public class MigratePasses {
 public static void main(String[] a)throws Exception{
  Path input=Path.of(a.length>0?a[0]:"../../Inputs/passes_for_migration.csv");
  List<String[]> valid=new ArrayList<>();List<String> bad=new ArrayList<>();
  try(BufferedReader r=Files.newBufferedReader(input,StandardCharsets.UTF_8)){
   String line;int no=0;while((line=r.readLine())!=null){no++;String[] f=line.split(",",-1);
    try{if(f.length!=5)throw new IllegalArgumentException("Expected 5 fields");
     Integer.parseInt(f[3]);if(Double.parseDouble(f[4])<0)throw new IllegalArgumentException("Negative amount");
     Double.parseDouble(f[4]);valid.add(f);
    }catch(RuntimeException e){bad.add("Line "+no+": "+e.getMessage()+" | "+line);}
   }
  }
  String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/buspass_db");
  try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),System.getenv().getOrDefault("DB_PASSWORD",""))){
   boolean old=c.getAutoCommit();c.setAutoCommit(false);
   try(PreparedStatement p=c.prepareStatement("INSERT INTO bus_pass VALUES(?,?,?,?,?)")){
    int b=0;for(String[] f:valid){p.setString(1,f[0]);p.setString(2,f[1]);p.setString(3,f[2]);p.setInt(4,Integer.parseInt(f[3]));p.setDouble(5,Double.parseDouble(f[4]));p.addBatch();if(++b==1000){p.executeBatch();b=0;}}
    if(b>0)p.executeBatch();c.commit();
   }catch(Exception e){c.rollback();throw e;}finally{c.setAutoCommit(old);}
  }
  Files.write(Path.of("migration_failures.txt"),bad,StandardCharsets.UTF_8);
  System.out.println("Valid rows migrated="+valid.size());System.out.println("Rejected rows="+bad.size());
  System.out.println("Failure report=migration_failures.txt");
 }
}
