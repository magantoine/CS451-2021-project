package cs451;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LCBConfigParser {

        private String path;
        private int numberOfMsg;
        private int dependencies [][];


        public boolean populate(String value) {
            System.out.println("start prasing config");
            String args[] = null;
            String content = null;
            String lines [] = null;

            try {
                Scanner in = new Scanner(new FileReader(value));
                content = "";
                while(true){
                    try{
                        content += in.nextLine() + "\n";
                    } catch(Exception e){
                        break;
                    }
                }


                lines = content.split("\n");
            } catch (FileNotFoundException e){
                return true;
            }

            path = value;
            System.out.println("path : " + path);
            numberOfMsg = Integer.parseInt(lines[0]);
            // number of messages is contained in the first line

            System.out.println("number of msg : " + numberOfMsg);
            System.out.println("nb lines : " + lines.length + " lines[0]  = " + lines[0]);
            var procDependencies = new int[lines.length-1][];
            for (int i = 1; i < lines.length; i++){
                var comps  = lines[i].split(" ");
                var cur_dep = new int[comps.length];
                for (int j = 0; j < comps.length; j++){
                    cur_dep[j] = Integer.parseInt(comps[j]);
                }
                procDependencies[i - 1] = cur_dep;

            }

            dependencies = procDependencies;
            System.out.println("dependencies : " + Arrays.toString(dependencies[0]));
            System.out.println("dependencies : " + Arrays.toString(dependencies[1]));
            System.out.println("dependencies : " + Arrays.toString(dependencies[2]));
            System.out.println("dependencies : " + Arrays.toString(dependencies[3]));
            return false;
        }

        public String getPath() {
            return path;
        }

        public int getNumberOfMessage(){
            return numberOfMsg;
        }


        public int[][] getDependencies(){
            return dependencies;

    }

}
