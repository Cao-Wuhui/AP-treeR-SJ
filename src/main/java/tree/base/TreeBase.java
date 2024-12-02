//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import tree.fvtree.FVTree;
import tree.lfvtree.LFVTree;
import utils.Pair;

public abstract class TreeBase {
    public TreeBase() {
    }

    public abstract void createTree(Map<String, List<Pair>> var1) throws InterruptedException;

    public abstract Query generateQuery(String var1, double var2);

    public abstract MineResult search(Query var1);

    public void mineParallel(List<Query> queryList, List<RunnerBase> runners) throws InterruptedException {
        int i = 0;
        int numThread = runners.size();

        //A simple multithreaded implementation example
        Iterator var5;
        for(var5 = queryList.iterator(); var5.hasNext(); i = (i + 1) % numThread) {
            Query query = (Query)var5.next();
            ((RunnerBase)runners.get(i)).add(query);
        }

        var5 = runners.iterator();

        RunnerBase runner;
        while(var5.hasNext()) {
            runner = (RunnerBase)var5.next();
            runner.start();
        }

        var5 = runners.iterator();

        while(var5.hasNext()) {
            runner = (RunnerBase)var5.next();
            runner.join();
        }

    }

    public void insert(String aid, List<Pair> transaction) {
    }

    public void parseQueryInReader(BufferedReader reader, List<Query> queryList, double threshold) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            Query query = this.generateQuery(line, threshold);
            queryList.add(query);
        }
    }

    public void parseAidToPairListInReader(BufferedReader reader, Map<String, List<Pair>> aidToPairList) throws IOException {
        label20:
        while(true) {
            String line;
            if ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                int count = st.countTokens() - 1;
                String uid = st.nextToken();
                Pair pair = new Pair(uid, count);
                while(true) {
                    if (!st.hasMoreTokens()) {
                        continue label20;
                    }
                    String aid = st.nextToken();
                    List<Pair> pairs = aidToPairList.get(aid);
                    if (pairs == null) {
                        pairs = new ArrayList();
                        aidToPairList.put(aid, pairs);
                    }
                    pairs.add(pair);
                }
            }
            return;
        }
    }

    public static TreeBase getTree(String treeType) {
        if (treeType == null) {
            return null;
        } else if (treeType.equals("fvtree")) {
            return new FVTree();
        } else if (treeType.equals("lfvtree")){
            return new LFVTree();
        }
        else
            return null;
    }
}
