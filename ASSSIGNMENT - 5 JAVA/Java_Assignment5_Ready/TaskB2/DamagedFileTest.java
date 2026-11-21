import java.nio.file.*;
public class DamagedFileTest {
 public static void main(String[] x)throws Exception{
  Path p=Files.createTempFile("bad",".csv");
  Files.writeString(p,"P1,R1,Asha,4,1200\\nP2,R2,WrongCount\\nP3,R3,Bala,5,abc\\n\\nP4,R4,Charan,6,1500\\n");
  PassStore s=new PassStore(p);
  System.out.println("P1 valid="+(s.find("P1")!=null));
  System.out.println("P2 damaged="+(s.find("P2")!=null));
  System.out.println("P3 damaged="+(s.find("P3")!=null));
  System.out.println("P4 after damaged rows="+(s.find("P4")!=null));
  Files.deleteIfExists(p);
 }
}
