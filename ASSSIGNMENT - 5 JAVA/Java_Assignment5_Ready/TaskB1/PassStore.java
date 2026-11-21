import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class PassStore {
    private final Path file;
    public PassStore(Path file){ this.file=file; }
    public void add(String id,String reg,String name,int route,double amount)throws IOException{
        try(BufferedWriter w=Files.newBufferedWriter(file,StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,StandardOpenOption.APPEND)){
            w.write(String.join(",",id,reg,name,String.valueOf(route),String.valueOf(amount)));
            w.newLine();
        }
    }
    public String[] find(String id)throws IOException{
        if(!Files.exists(file)) return null;
        try(BufferedReader r=Files.newBufferedReader(file,StandardCharsets.UTF_8)){
            String line;
            while((line=r.readLine())!=null){
                String[] f=line.split(",",-1);
                if(f.length!=5) continue;
                try{Integer.parseInt(f[3]);Double.parseDouble(f[4]);}
                catch(NumberFormatException e){continue;}
                if(f[0].equals(id)) return f;
            }
        }
        return null;
    }
    public List<String[]> listByRoute(int route)throws IOException{
        List<String[]> out=new ArrayList<>();
        if(!Files.exists(file)) return out;
        try(BufferedReader r=Files.newBufferedReader(file,StandardCharsets.UTF_8)){
            String line;
            while((line=r.readLine())!=null){
                String[] f=line.split(",",-1);
                if(f.length!=5) continue;
                try{if(Integer.parseInt(f[3])==route){Double.parseDouble(f[4]);out.add(f);}}
                catch(NumberFormatException ignored){}
            }
        }
        return out;
    }
    public static void main(String[] args)throws Exception{
        Path f=Files.createTempFile("passes",".csv"); PassStore s=new PassStore(f);
        s.add("P1001","192311001","Arun",12,4500);
        Files.writeString(f,"P1002,DAMAGED LINE\\n",StandardOpenOption.APPEND);
        s.add("P1003","192311003","Divya",7,3800);
        System.out.println(Arrays.toString(s.find("P1003")));
        System.out.println(s.find("P1002"));
        Files.deleteIfExists(f);
    }
}
