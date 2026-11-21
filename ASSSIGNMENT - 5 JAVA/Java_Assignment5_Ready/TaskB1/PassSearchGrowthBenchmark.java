import java.nio.file.*;
public class PassSearchGrowthBenchmark {
 public static void main(String[] x)throws Exception{
  for(int n:new int[]{1000,10000,100000}){
   Path p=Files.createTempFile("growth",".csv"); PassStore s=new PassStore(p);
   for(int i=0;i<n;i++)s.add("P"+i,"R"+i,"Student"+i,i%20,1000+i);
   long t=System.nanoTime(); String[] f=s.find("P"+(n-1)); long ms=System.nanoTime()-t;
   System.out.printf("records=%d found=%s elapsed_ms=%.3f%n",n,f!=null,ms/1e6);
   Files.deleteIfExists(p);
  }
 }
}
