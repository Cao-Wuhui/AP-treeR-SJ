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
    private Map<String, Integer> aidToCount = new HashMap();//维持头表结构
//    public long numofcandidates=0;
//    public long returns=0;
    public LFVTree() {
    }

    public Map<String, Integer> getAidToCount() {
        return this.aidToCount;
    }

    public Map<String, LFVTreeEntry> getAidToEntry() {
        return this.aidToEntry;
    }

    public LFVTreeNode getRoot() {
        return this.root;
    }

    public void insert(String aid, List<Pair> transaction) {
        Iterator<Pair> it = transaction.iterator();
        LFVTreeNode currentNode = this.root;
        LFVTreeEntry currentEntry = null;
        //int entryIndex = false;

        while(true) {
            while(it.hasNext()) {//对于每行si,seq(ai)
                Pair p = (Pair)it.next();//对于每一个si,|si|
                LFVTreeNode child = currentNode.getChildByName(p.getFirst());//看看根节点的孩子有没有 si
                if (child == null) {//没有 新创一个 通用处理
                    currentNode = new LFVTreeNode(p, currentNode);

                    for(currentEntry = currentNode.getEntry(0); it.hasNext(); currentEntry = currentNode.addEntry(p)) {
                        p = (Pair)it.next();
                    }//处理完一行
                } else {//有
                    currentNode = child;
                    List<LFVTreeEntry> entrys = child.getEntrys();//得到结点数组
                    currentEntry = (LFVTreeEntry)entrys.get(0);//进入数组第一个
                    boolean divFlag = false;//是否要产生分支

                    for(int entryIndex = 1; entryIndex < entrys.size() && it.hasNext(); ++entryIndex) {//遍历当前结点的数组，找出分支在哪
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
                        }//分裂后 把剩下的pair填入结点
                    } else if (child.getChilds().size() == 0) {
                        while(it.hasNext()) {
                            p = (Pair)it.next();
                            currentEntry = currentNode.addEntry(p);
                        }
                    }
                }
            }//插入完毕

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
                return ((List)o2.getValue()).size() - ((List)o1.getValue()).size();
            }
        });
        System.out.println(timer.end());
        Iterator var6 = entryList.iterator();

        while(var6.hasNext()) {//var6 迭代每一行seq()
            Map.Entry<String, List<Pair>> entry = (Map.Entry)var6.next();
            String aid = (String)entry.getKey();
            List<Pair> pairs = (List)entry.getValue();
            Collections.sort(pairs);
            this.insert(aid, pairs);//插入结点
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

        //query.sortAirPairs();
        return query;
    }

    public MineResult search1(Query query) {
        Map<Pair, Integer> uidPairToSupport = new HashMap();
        Map<LFVTreeEntry, Integer> entrySet = new HashMap();
        Iterator var4 = query.getAidPairs().iterator();

        while(var4.hasNext()) {
            Pair aidPair = (Pair)var4.next();
            LFVTreeEntry entry = (LFVTreeEntry)this.aidToEntry.get(aidPair.getFirst());
            entrySet.put(entry, (Integer)entrySet.getOrDefault(entry, 0) + 1);
        }

        double size = (double)query.getUidSize();
        double threshold = query.getThreshold();
        double minSize = threshold * size;
        double var10000 = size / threshold;
        String queryUid = query.getUid();
        Iterator var17 = query.getAidPairs().iterator();

        while(true) {
            LFVTreeEntry entry;
            int support;
            do {
                if (!var17.hasNext()) {
                    MineResult result = new MineResult(queryUid);
                    Iterator var24 = uidPairToSupport.keySet().iterator();

                    while(var24.hasNext()) {
                        Pair uidPair = (Pair)var24.next();
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
            } while((Integer)entrySet.getOrDefault(entry, 0) == 0);

            LFVTreeNode currentNode = entry.getNode();
            int index = entry.getIndex();

            for(double curSize = (double)entry.getSize(); curSize * threshold <= size; curSize = (double)entry.getSize()) {
                Integer c = (Integer)entrySet.get(entry);
                if (c != null) {
                    entrySet.put(entry, 0);
                    support += c;
                }

                if (curSize >= minSize) {
                    uidPairToSupport.put(entry.getPair(), (Integer)uidPairToSupport.getOrDefault(entry.getPair(), 0) + support);
                }

                --index;
                if (index < 0) {
                    currentNode = currentNode.getParent();
                    if (currentNode == this.root) {
                        break;
                    }

                    index = currentNode.getEntrys().size() - 1;
                }

                entry = currentNode.getEntry(index);
            }
        }
    }

    public MineResult search(Query query) {//执行|r|次
        Map<Pair, Integer> uidPairToSupport = new HashMap();
        Map<LFVTreeEntry, Integer> entrySet = new HashMap();//记录候选集和备忘录
        Iterator var4 = query.getAidPairs().iterator();

        while(var4.hasNext()) {
            Pair aidPair = (Pair)var4.next();
            LFVTreeEntry entry = (LFVTreeEntry)this.aidToEntry.get(aidPair.getFirst());
            if (entry != null) {
                entrySet.put(entry, (Integer)entrySet.getOrDefault(entry, 0) + 1);
            }
        }

        double size = (double)query.getUidSize();//totalnum为搜索集合的元素总数
        double threshold = query.getThreshold();
        double minSize = threshold * size;
        //double var10000 = size / threshold;
        String queryUid = query.getUid();
        Iterator var17 = query.getAidPairs().iterator();

        label99:
        while(true) {
            LFVTreeEntry entry;
            int support;
            do {
                do {
                    if (!var17.hasNext()) {//集合的元素都遍历完了
                        //System.out.println("The numofcandidates of " + queryUid +" is " + uidPairToSupport.size());
                        MineResult result = new MineResult(queryUid);
                        Iterator var31 = uidPairToSupport.keySet().iterator();
//                        numofcandidates += uidPairToSupport.keySet().size();
                        while(var31.hasNext()) {

                            Pair uidPair = (Pair)var31.next();
                            String uid = uidPair.getFirst();
                            support = (Integer)uidPairToSupport.get(uidPair);
                            double weight = (double)Math.round((double)support / ((double)uidPair.getSecond() + size - (double)support) * 100.0) / 100.0;
                            if (weight >= threshold) {
                                result.add(uid, weight);
//                                returns++;
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
            while(currentNode != this.root && (double)currentNode.getSize() < minSize) {//结点没达到下限要求 累加f
                Iterator var22 = currentNode.getEndEntrys().iterator();//得到结点的最后一个 entry

                while(var22.hasNext()) {
                    Integer i = (Integer)var22.next();
                    if (i <= index) {//i>index则跳出该entry
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

            while(currentNode != this.root) {//还没到root
                List<LFVTreeEntry> entryList = currentNode.getEntrys();

                for(int i = index; i >= 0; --i) {
                    entry = (LFVTreeEntry)entryList.get(i);
                    double curSize = (double)entry.getSize();
                    if (curSize * threshold > size) {//cursize<=size/t 此处为上限长度过滤
                        continue label99;
                    }

                    if (entry.isEnd()) {
                        c = (Integer)entrySet.get(entry);
                        if (c != null) {
                            entrySet.put(entry, 0);
                            support += c;
                        }
                    }

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

    public long info() {
        Stack<LFVTreeNode> st = new Stack();
        Iterator var2 = this.root.getChilds().iterator();//得到所有根节点的孩子结点

        while(var2.hasNext()) {
            LFVTreeNode child = (LFVTreeNode)var2.next();//把这些孩子结点入栈
            st.push(child);
        }

        List<Integer> items = new ArrayList();
        //new ArrayList();
        List<Integer> childs = new ArrayList();
        int count = 0;
        int itemCount = 0;

        while(!st.empty()) {
            ++count;//计算一共又多少个结点
            LFVTreeNode node = (LFVTreeNode)st.pop();
            itemCount += node.getEntrys().size();//这个节点里有多少个entry
            items.add(node.getEntrys().size());//这个结点的entry数量
            childs.add(node.getChilds().size());//这个结点的孩子结点数量
            Iterator var8 = node.getChilds().iterator();//获取其孩子结点

            while(var8.hasNext()) {
                LFVTreeNode child = (LFVTreeNode)var8.next();
                st.push(child);
            }//孩子结点入栈
        }//此乃广搜，直到全部结点被搜完

        List<Integer> aidCount = new ArrayList(this.aidToCount.values());//一共多少个元素
        Collections.sort(aidCount);
        Collections.sort(items);
        Collections.sort(childs);
        System.out.println("count = " + count);//结点个数
        System.out.println("childs = " + childs.size());
        System.out.println("items = " + items.size());
        System.out.println("aidCount = " + aidCount.size());
        System.out.println("aidCount max is " + aidCount.get(aidCount.size() - 1));
        System.out.println("childs.size() = " + childs.size());
        System.out.println("itemCount = " + itemCount);
        long A=aidCount.size();
        return A;
    }

    public long getnum()
    {
        return  1;
    }
    public long getreturns()
    {
        return  1;
    }
}
