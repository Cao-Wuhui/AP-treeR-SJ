package run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import tree.base.Query;
import tree.base.RunnerBase;
import tree.base.TreeBase;
import utils.Pair;

public class RunSingle {
    public RunSingle() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        String treeType = args[0];
//        String rInputPath = args[1];
//        String sInputPath = args[2];
//        String outputPath = args[3];
//        double threshold = Double.parseDouble(args[4]);
//        int numThread = Integer.parseInt(args[5]);
//        String logpath = args[6];

        //String treeType = "lfvtree";
        String treeType = "fvtree";
        String rInputPath = "src/main/java/input/dblp1ws";//Input set collection R
        String sInputPath = "src/main/java/input/dblp1wr";//Input set collection S
        String outputPath = "src/main/java/output/out";//Output set pairs
        //Setting the threshold and number of threads
        double threshold = 0.5;
        int numThread = 8;
        String logpath = "src/main/java/output/lo";

        System.out.println("rInputPath = " + rInputPath);
        System.out.println("sInputPath = " + sInputPath);
        System.out.println("outputPath = " + outputPath);
        System.out.println("threshold = " + threshold);
        System.out.println("numThread = " + numThread);
        TreeBase tree = TreeBase.getTree(treeType);//Select the type of tree
        Map<String, List<Pair>> aidToPairList = countAndReverse(tree, rInputPath);//Reorganize the collection S

        Date startTime = new Date();
        Date startIndexTime = new Date();
        tree.createTree(aidToPairList);//Create a tree
        Date endIndexTime = new Date();
        double indexTime = ((endIndexTime.getTime() - startIndexTime.getTime())*1.0 / 1000L);
        System.out.println("Index Time is " + indexTime + " second");
        Date startMineTime = new Date();
        mineLocal(tree, sInputPath, outputPath, threshold, numThread);//Search the tree
        Date endMineTime = new Date();
        double mineTime = ((endMineTime.getTime() - startMineTime.getTime())*1.0 / 1000L);
        System.out.println("Mine Time is " + mineTime + " second");
        Date endTime = new Date();
        double allTime = ((endTime.getTime() - startTime.getTime())*1.0 / 1000L);
        System.out.println("Time is " + allTime + " second");
        FileOutputStream out = new FileOutputStream(logpath, true);
        PrintWriter writer = new PrintWriter(out);

        writer.write("************FV-treeR-SJ************\n");
        writer.write("treeType: " + treeType + "\n");
        writer.write("rInput: " + rInputPath + "\n");
        writer.write("sInput: " + sInputPath + "\n");
        writer.write("output: " + outputPath + "\n");
        writer.write("threshold: " + threshold + "\n");
        writer.write("indexTime: " + indexTime + "\n");
        writer.write("mineTime: " + mineTime + "\n");
        writer.write("time: " + allTime + "\n");
        writer.close();
    }
    public static Map<String, List<Pair>> countAndReverse(TreeBase tree, String inputPath) throws IOException {
        FileInputStream f = new FileInputStream(inputPath);
        InputStreamReader inputStreamReader = new InputStreamReader(f);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        Map<String, List<Pair>> aidToPairList = new HashMap();
        tree.parseAidToPairListInReader(reader, aidToPairList);
        return aidToPairList;
    }
    public static void mineLocal(TreeBase tree, String inputPath, String outputPath, double threshold, int numThread) throws IOException, InterruptedException {
        FileInputStream f = new FileInputStream(inputPath);
        InputStreamReader inputStreamReader = new InputStreamReader(f);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        FileOutputStream f2 = new FileOutputStream(outputPath);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(f2);
        BufferedWriter writer = new BufferedWriter(outputStreamWriter);
        List<Query> queryList = new ArrayList();
        tree.parseQueryInReader(reader, queryList, threshold);
        Collections.sort(queryList);
        //Split the collection R by number of threads
        if (numThread > queryList.size()) {
            numThread = queryList.size();
        }
        List<RunnerBase> runners = new ArrayList();
        for(int i = 0; i < numThread; ++i) {
            runners.add(new SubRunner(tree, writer));
        }
        tree.mineParallel(queryList, runners);
        writer.close();
    }
}
