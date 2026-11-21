import java.io.*;
import java.nio.file.*;

public class BufferLineCounter {
    static long count(Path p, boolean buffered)throws IOException{
        long lines=0; boolean any=false; int last=-1;
        try(InputStream in=buffered
                ? new BufferedInputStream(new FileInputStream(p.toFile()),65536)
                : new FileInputStream(p.toFile())){
            int b; while((b=in.read())!=-1){any=true;last=b;if(b=='\\n')lines++;}
        }
        if(any && last!='\\n') lines++;
        return lines;
    }
    public static void main(String[] args)throws Exception{
        Path p=Files.createTempFile("attendance",".log");
        Files.writeString(p,"one\\ntwo\\nthree");
        long t=System.nanoTime(); long a=count(p,false); long u=System.nanoTime()-t;
        t=System.nanoTime(); long b=count(p,true); long v=System.nanoTime()-t;
        System.out.println("Unbuffered lines="+a+" time(ns)="+u);
        System.out.println("Buffered lines="+b+" time(ns)="+v);
        System.out.println("Counts match="+(a==b));
        Files.deleteIfExists(p);
    }
}
