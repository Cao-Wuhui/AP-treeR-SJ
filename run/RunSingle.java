package run;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import tree.base.Query;
import tree.base.RunnerBase;
import tree.base.TreeBase;
import utils.Pair;
import org.apache.log4j.Logger;

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
        String treeType = "telpstree";
        //String treeType = "telpstree";
        String rInputPath = "src/main/java/input/dblp1ws";//rinput建树
        String sInputPath = "src/main/java/input/dblp1wr";//sinput搜素
        String outputPath = "src/main/java/output/out";
        double threshold = (double)(0.7);
        int numThread = 8;
        String logpath = "src/main/java/output/lo";

        System.out.println("rInputPath = " + rInputPath);
        System.out.println("sInputPath = " + sInputPath);
        System.out.println("outputPath = " + outputPath);
        System.out.println("threshold = " + threshold);
        System.out.println("numThread = " + numThread);
        TreeBase tree = TreeBase.getTree(treeType);
        Map<String, List<Pair>> aidToPairList = countAndReverse(tree, rInputPath);
//        final Logger logger = Logger.getLogger(RunSingle.class.getName());//获取记录器
//        BasicConfigurator.configure();//自动快速地使用缺省Log4j环境
        //PropertyConfigurator.configure();读取使用Java的特性文件编写的配置文件
        //DOMConfigurator.configure ( String filename ) ：读取XML形式的配置文件。
//        logger.debug("Debug level message");
//        logger.info("Info level message");
//        logger.warn("Warn level message");
//        logger.error("Error level message");
//        logger.fatal("Fatal level message");

        Date startTime = new Date();
        Date startIndexTime = new Date();
        tree.createTree(aidToPairList);//转置表按照包含该元素的集合个数降序排列
        Date endIndexTime = new Date();
        double indexTime = ((endIndexTime.getTime() - startIndexTime.getTime())*1.0 / 1000L);
        System.out.println("Index Time is " + indexTime + " second");
        Date startMineTime = new Date();
        mineLocal(tree, sInputPath, outputPath, threshold, numThread);
        Date endMineTime = new Date();
        double mineTime = ((endMineTime.getTime() - startMineTime.getTime())*1.0 / 1000L);
        System.out.println("Mine Time is " + mineTime + " second");
        Date endTime = new Date();
        double allTime = ((endTime.getTime() - startTime.getTime())*1.0 / 1000L);
        System.out.println("Time is " + allTime + " second");
        FileOutputStream out = new FileOutputStream(logpath, true);
        PrintWriter writer = new PrintWriter(out);

        writer.write("************FPSJoin************\n");
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

    public static void info_aidtopairlist(long A, Map<String, List<Pair>> aidToPairList)
    {
        //A为总元素个数
        long maxseq=0;//最大长度
        double avgseq=0.0;//平均长度
        System.out.println("A is "+A);
        System.out.println("A = ?"+aidToPairList.size());
        ArrayList<Map.Entry<String, List<Pair>>> entryList = new ArrayList(aidToPairList.entrySet());
        Iterator seq = entryList.iterator();
        while(seq.hasNext())//遍历每个seq
        {
            Map.Entry<String, List<Pair>> p = (Map.Entry)seq.next();
            int size=p.getValue().size();//该行seq的值大小
            avgseq=avgseq+size;
            if(size>maxseq)
                maxseq=size;

        }
        System.out.println("max seq is " + maxseq);
        System.out.println("avg seq is " + avgseq/A);
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
        tree.parseQueryInReader(reader, queryList, threshold);//解析查询集
        Collections.sort(queryList);//对于每个r查询集，对|r|进行降序排列，优先搜索次数最多的r
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
