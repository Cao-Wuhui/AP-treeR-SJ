//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.lfvtree;

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
import utils.Timer;

public class LFVTree extends TreeBase {
    private LFVTreeNode root = new LFVTreeNode();
    private Map<String, LFVTreeEntry> aidToEntry = new HashMap();
    private Map<String, Integer> aidToCount = new HashMap();
    public LFVTree() {
    }

    public LFVTreeNode getRoot() {
        return this.root;
    }

    public void insert(String aid, List<Pair> transaction) {
        Iterator<Pair> it = transaction.iterator();
        LFVTreeNode currentNode = this.root;
        LFVTreeEntry currentEntry = null;
        /* Different cases of inserting tree nodes */
        while(true) {
            while(it.hasNext()) {
                Pair p = (Pair)it.next();
                LFVTreeNode child = currentNode.getChildByName(p.getFirst());
                if (child == null) {
                    currentNode = new LFVTreeNode(p, currentNode);
                    for(currentEntry = currentNode.getEntry(0); it.hasNext(); currentEntry = currentNode.addEntry(p)) {
                        p = (Pair)it.next();
                    }
                } else {
                    currentNode = child;
                    List<LFVTreeEntry> entrys = child.getEntrys();
                    currentEntry = (LFVTreeEntry)entrys.get(0);
                    boolean divFlag = false;
                    for(int entryIndex = 1; entryIndex < entrys.size() && it.hasNext(); ++entryIndex) {
                        p = (Pair)it.next();
                        currentEntry = (LFVTreeEntry)entrys.get(entryIndex);
                        if (!currentEntry.getName().equals(p.getFirst())) {
                            divFlag = true;
                            break;
                        }
                    }
                    if (divFlag) {
                        currentNode = child.split(currentEntry.getIndex(), p);

                        for(currentEntry = currentNode.getEntry(0); it.hasNext(); currentEntry = currentNode.addEntry(p)) {
                            p = (Pair)it.next();
                        }
                    } else if (child.getChilds().size() == 0) {
                        while(it.hasNext()) {
                            p = (Pair)it.next();
                            currentEntry = currentNode.addEntry(p);
                        }
                    }
                }
            }
            this.aidToEntry.put(aid, currentEntry);
            currentEntry.setEnd(true);
            this.aidToCount.put(aid, transaction.size());
            return;
        }
    }

    public void createTree(Map<String, List<Pair>> aidToPairList) {
        Timer timer = new Timer("sort pairs");
        ArrayList<Map.Entry<String, List<Pair>>> entryList = new ArrayList(aidToPairList.entrySet());
        entryList.sort(new Comparator<Map.Entry<String, List<Pair>>>() {
            public int compare(Map.Entry<String, List<Pair>> o1, Map.Entry<String, List<Pair>> o2) {
                return (o2.getValue()).size() - (o1.getValue()).size();
            }
        });
        System.out.println(timer.end());
        Iterator var6 = entryList.iterator();
        while(var6.hasNext()) {
            Map.Entry<String, List<Pair>> entry = (Map.Entry)var6.next();
            String aid = entry.getKey();
            List<Pair> pairs = entry.getValue();
            Collections.sort(pairs);
            this.insert(aid, pairs);
        }
    }

    public Query generateQuery(String line, double threshold) {
        StringTokenizer st = new StringTokenizer(line);
        String uid = st.nextToken();
        int uidSize = st.countTokens();
        Query query = new Query(uid, uidSize, threshold);
        while(st.hasMoreTokens()) {
            String aid = st.nextToken();
            Integer size = this.aidToCount.get(aid);
            if (size != null) {
                query.addPair(new Pair(aid, size));
            }
        }
        return query;
    }

    public MineResult search(Query query) {
        Map<Pair, Integer> uidPairToSupport = new HashMap();
        Map<LFVTreeEntry, Integer> entrySet = new HashMap();
        Iterator var4 = query.getAidPairs().iterator();

        while(var4.hasNext()) {
            Pair aidPair = (Pair)var4.next();
            LFVTreeEntry entry = (LFVTreeEntry)this.aidToEntry.get(aidPair.getFirst());
            if (entry != null) {
                entrySet.put(entry, (Integer)entrySet.getOrDefault(entry, 0) + 1);
            }
        }
        double size = (double)query.getUidSize();
        double threshold = query.getThreshold();
        double minSize = threshold * size;
        String queryUid = query.getUid();
        Iterator var17 = query.getAidPairs().iterator();
        label99:
        while(true) {
            LFVTreeEntry entry;
            int support;
            do {
                do {
                    if (!var17.hasNext()) {

                        MineResult result = new MineResult(queryUid);
                        Iterator var31 = uidPairToSupport.keySet().iterator();
//
                        while(var31.hasNext()) {
                            Pair uidPair = (Pair)var31.next();
                            String uid = uidPair.getFirst();
                            support = (Integer)uidPairToSupport.get(uidPair);
                            double weight = (double)Math.round((double)support / ((double)uidPair.getSecond() + size - (double)support) * 100.0) / 100.0;
                            if (weight >= threshold) {
                                result.add(uid, weight);
                            }
                        }

                        return result;
                    }
                    Pair aidPair = (Pair)var17.next();
                    String aid = aidPair.getFirst();
                    support = 0;
                    entry = (LFVTreeEntry)this.aidToEntry.get(aid);
                } while(entry == null);
            } while((Integer)entrySet.getOrDefault(entry, 0) == 0);

            LFVTreeNode currentNode = entry.getNode();
            int index = entry.getIndex();//得到当前结点及其index
            if (entry.getNode() == null) {
                System.out.println("entry = " + entry);
            }

            Integer c;
            while(currentNode != this.root && (double)currentNode.getSize() < minSize) {
                Iterator var22 = currentNode.getEndEntrys().iterator();
                while(var22.hasNext()) {
                    Integer i = (Integer)var22.next();
                    if (i <= index) {
                        c = (Integer)entrySet.get(entry);
                        if (c != null) {
                            entrySet.put(entry, 0);
                            support += c;
                        }
                    }
                }
                currentNode = currentNode.getParent();
                if (currentNode != this.root) {
                    index = currentNode.getEntrys().size() - 1;
                }
            }

            while(currentNode != this.root) {
                List<LFVTreeEntry> entryList = currentNode.getEntrys();

                for(int i = index; i >= 0; --i) {
                    entry = (LFVTreeEntry)entryList.get(i);
                    double curSize = (double)entry.getSize();
                    if (curSize * threshold > size) {
                        continue label99;
                    }
                    if (entry.isEnd()) {
                        c = (Integer)entrySet.get(entry);
                        if (c != null) {
                            entrySet.put(entry, 0);
                            support += c;
                        }
                    }
                    /* Determine whether the current node meets the length requirements using the length filter strategy */
                    if (curSize >= minSize) {
                        uidPairToSupport.put(entry.getPair(), (Integer)uidPairToSupport.getOrDefault(entry.getPair(), 0) + support);
                    }
                }
                currentNode = currentNode.getParent();
                if (currentNode != this.root) {
                    index = currentNode.getEntrys().size() - 1;
                }
            }
        }
    }
}
