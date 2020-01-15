

import java.io.*;
import java.util.*;
import java.nio.file.FileVisitResult.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.stream.*;


public class ShowGitObj {

    public static void main(String args[]) {
        try {
            System.out.println("******************Git Object dump*******************");
            File file= new File("./.git/objects/");
            climbDirTree(file);

            System.out.println("******************Heads Refs dump*******************");
            dumpRefs(new File("./.git/refs/heads"));
                        System.out.println("******************remotes Refs dump*******************");
            dumpRefs(new File("./.git/refs/remotes"));
                        System.out.println("******************tags Refs dump*******************");
            dumpRefs(new File("./.git/refs/tags"));
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    static String doExec(String str, String option)throws Exception
    {
        String s;
        String ret="";
        String CmdStr="git cat-file -"+option+ " "+str;

        //    System.out.println("CMD=>"+CmdStr);

        Process p = Runtime.getRuntime().exec(CmdStr);

        BufferedReader stdInput = new BufferedReader(new 
                                                     InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new 
                                                     InputStreamReader(p.getErrorStream()));       
        while((s = stdInput.readLine()) != null) {
            //  System.out.println("STDOUT=>\n");
            System.out.println(s); 
            ret=s;
        }       
        while((s = stdError.readLine()) != null) {
            System.out.println("STDERR=>\n");
            System.out.println(s);

        } 

        return ret;
    }

    public static void climbDirTree(File node)throws Exception
    {

        //    System.out.println(node.getAbsoluteFile());

        if(node.isDirectory()) {
            String[] subNote = node.list();
            for(String filename : subNote) {
                climbDirTree(new File(node, filename));
            }
        } else {
            String parent = node.getParent();
            String sub = parent.substring(parent.length()-3);
            if(sub.startsWith(File.separator)) {
                sub = sub.substring(sub.length()-2);
            }
            String sha=sub+node.getName();
            System.out.println("Sha="+sha);
            System.out.print("Type:");
            String type = doExec(sha, "t");
            if(!type.equals("blob")) {
                System.out.println("Content:");
                doExec(sha, "p"); 
            }
            System.out.println("*****************************************************");
        }
    }

    public static void dumpRefs(File node)throws Exception
    {
        if(node.isDirectory()) {
            String[] subNote = node.list();
            for(String filename : subNote) {
                dumpRefs(new File(node, filename));
            }
        } else {

            String name=node.getName();
            System.out.println("Refname="+name);

            Stream<String> linesStream = Files.lines(node.toPath());
            linesStream.forEach(line -> {
                                    System.out.println(line);
                                });
        }

    }
}

