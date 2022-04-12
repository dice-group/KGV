package org.diceresearch.KGV.ETL.DiskManager;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HDDManager {
    public String giveFileLocation(String fileName) throws ClassNotFoundException {
        //Class cls=this.getClass();
        Class cls = Class.forName("org.diceresearch.KGV.ETL.DiskManager.HDDManager");
        Path source = Paths.get(cls.getClass().getResource("/").getPath());
        return source.toString()+"/"+fileName;
    }
}
