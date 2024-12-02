//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.fvtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import tree.base.MineResult;
import tree.base.Query;
import tree.base.TreeBase;
import utils.Pair;

public class FVTree extends TreeBase {
    private Map<String, FVTreeNode> aidToLastNode = new HashMap();
    private Map<String, Integer> aidToCount = new HashMap();
    private FVTreeNode root = new FVTreeNode();
    public long numofcandidates=0;
    public FVTree() {
    }

    //insert a seq(a_i) to tree
    private FVTreeNode insert(List<Pair> transaction) {
        FVTreeNode currentNode = this.root;
        FVTreeNode child;
        for(Iterator var4 = transaction.iterator(); var4.hasNext(); currentNode = child) {
            Pair pair = (Pair)var4.next();
            child = currentNode.getChildByName(pair.getFirst());
            if (child == null) {
                child = new FVTreeNode(pair, currentNode);
            }
        }
        return currentNode;
    }

    public void createTree(Map<String, List<Pair>> aidToPairList) {
        ArrayList<Map.Entry<String, List<Pair>>> entryList = new ArrayList(aidToPairList.entrySet());
        entryList.sort(new Comparator<Map.Entry<String, List<Pair>>>() {
            public int compare(Map.Entry<String, List<Pair>> o1, Map.Entry<String, List<Pair>> o2) {
                return ((List)o2.getValue()).size() - ((List)o1.getValue()).size();
            }
        });
        Iterator var6 = entryList.iterator();
        //Add seq(a_i) to the tree and element table
        while(var6.hasNext()) {
            Map.Entry<String, List<Pair>> entry = (Map.Entry)var6.next();
            String aid = (String)entry.getKey();
            List<Pair> pairs = (List)entry.getValue();
            Collections.sort(pairs);
            FVTreeNode lastNode = this.insert(pairs);
            lastNode.setEndFlag((String)entry.getKey());
            this.aidToLastNode.put(aid, lastNode);
            this.aidToCount.put(aid, pairs.size());
        }
    }

//Split the query collection R
    public Query generateQuery(String line, double threshold) {
        StringTokenizer st = new StringTokenizer(line);
        String uid = st.nextToken();
        int uidSize = st.countTokens();
        Query query = new Query(uid, uidSize, threshold);

        while(st.hasMoreTokens()) {
            String aid = st.nextToken();
            Integer size = (Integer)this.aidToCount.get(aid);
            if (size != null) {
                query.addPair(new Pair(aid, size));
            }
        }
        return query;
    }

    //search the tree using a query set
    public MineResult search(Query query) {
        Map<FVTreeNode, Integer> aidSet = new HashMap();
        Iterator var3 = query.getAidPairs().iterator();
        while(var3.hasNext()){
            Pair aidPair = (Pair)var3.next();
            FVTreeNode node = (FVTreeNode)this.aidToLastNode.get(aidPair.getFirst());
            aidSet.put(node, (Integer)aidSet.getOrDefault(node, 0) + 1);
        }
        Map<Pair, Integer> uidPairToSupport = new HashMap();
        double size = (double)query.getUidSize();
        double threshold = query.getThreshold();
        double minSize = threshold * size;
        double var10000 = size / threshold;
        String queryUid = query.getUid();
        Iterator var17 = query.getAidPairs().iterator();
        while(true) {
            FVTreeNode currentNode;
            int support;
            do {
                if (!var17.hasNext()) {
                    MineResult result = new MineResult(queryUid);
                    Iterator var21 = uidPairToSupport.keySet().iterator();
                    numofcandidates += uidPairToSupport.keySet().size();
                    while(var21.hasNext()) {
                        Pair uidPair = (Pair)var21.next();
                        String uid = uidPair.getFirst();
                        support = uidPairToSupport.get(uidPair);
                        double weight = (double)Math.round((double)support / ((double)uidPair.getSecond() + size - (double)support) * 100.0) / 100.0;
                        if (weight >= threshold) {
                            result.add(uid, weight);
                        }
                    }
                    return result;
                }
                Pair aidPair = (Pair)var17.next();
                String aid = aidPair.getFirst();
                currentNode = this.aidToLastNode.get(aid);
            } while(aidSet.getOrDefault(currentNode, 0) == 0);
            support = 0;

            /* Determine whether the current node meets the length requirements using the length filter strategy */
            for(double curSize = currentNode.getSize(); currentNode != this.root && curSize * threshold <= size; curSize = (double)currentNode.getSize()) {
                Integer c = (Integer)aidSet.getOrDefault(currentNode, 0);
                if (c != 0) {
                    aidSet.put(currentNode, 0);
                    support += c;
                }
                if (curSize >= minSize) {
                    uidPairToSupport.put(currentNode.getPair(), (Integer)uidPairToSupport.getOrDefault(currentNode.getPair(), 0) + support);
                }
                currentNode = currentNode.getParent();
            }
        }
    }
}
