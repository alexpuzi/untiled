package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private String filename;
    private long size;
    public static final String UP_TOKEN = "[..]";
    public FileInfo(Path path){
        this.filename = path.getFileName().toString();
        if(Files.isDirectory(path)){
            this.size = -1L;
        }else{
            try {
                this.size = Files.size(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileInfo(String filename, long length){
        this.filename = filename;
        this.size = length;
    }

    public void setFilename(String filename) {
        this.filename = filename;

    }
    public boolean isDerecrory(){
        return size == -1L;
    }

    public boolean isUpElement(){
        return size == -2L;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }


    public long getSize() {
        return size;
    }

}
