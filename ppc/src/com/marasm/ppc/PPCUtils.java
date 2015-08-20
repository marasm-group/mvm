package com.marasm.ppc;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by sr3u on 20.08.15.
 */
public class PPCUtils {
    // inner class, generic extension filter
    public static class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }
    public static String[] listFiles(String folder, String ext) {

        GenericExtFilter filter = new GenericExtFilter(ext);

        File dir = new File(folder);

        if(dir.isDirectory()==false){
            System.out.println("Directory does not exists : " + folder);
            return null;
        }

        // list out all the file name and filter by the extension
        String[] list = dir.list(filter);

        if (list.length == 0) {
            System.out.println("no files end with : " + ext);
            return null;
        }
        ArrayList<String> res=new ArrayList<>();
        for (String file : list) {
            String temp = new StringBuffer(folder).append(file).toString();
            res.add(temp);
        }
        return res.toArray(new String[res.size()]);
    }


}
