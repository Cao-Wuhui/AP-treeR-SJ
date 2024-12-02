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
    public long getreturns()
    {
        return  numofcandidates;
    }
    private FVTreeNode insert(List<Pair> transaction) {
        FVTreeNode currentNode = this.root;

        FVTreeNode child;
        for(Iterator var4 = transaction.iterator(); var4.hasNext(); currentNode = child) {
            Pair pair = (Pair)var4.next();
            child = currentNode.getChildByName(pair.getFirst());//1主要是这一步耗时间
            if (child == null) {
                child = new FVTreeNode(pair, currentNode);//2
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

        while(var6.hasNext()) {
            Map.Entry<String, List<Pair>> entry = (Map.Entry)var6.next();//对于每一行转置表
            String aid = (String)entry.getKey();
            List<Pair> pairs = (List)entry.getValue();
            Collections.sort(pairs);//每一行转置表中，每个集合按照元素大小排列
            FVTreeNode lastNode = this.insert(pairs);
            lastNode.setEndFlag((String)entry.getKey());//最后一个结点连向 唯一元素
            this.aidToLastNode.put(aid, lastNode);
            this.aidToCount.put(aid, pairs.size());
        }

    }

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
//            if(this.aidToLastNode.containsKey(aid)){
//                Integer size = (Integer)this.aidToCount.get(aid);
//                query.addPair(new Pair(aid, size));
//            }
        }
        query.sortAirPairs();
        return query;
    }

    public MineResult search(Query query) {
        Map<FVTreeNode, Integer> aidSet = new HashMap();
        Iterator var3 = query.getAidPairs().iterator();

        while(var3.hasNext()){//将查询集中的（元素 及其 包含该元素的集合个数）进行操作
            Pair aidPair = (Pair)var3.next();
            FVTreeNode node = (FVTreeNode)this.aidToLastNode.get(aidPair.getFirst());
            aidSet.put(node, (Integer)aidSet.getOrDefault(node, 0) + 1);//保存（结点，aidSet中该结点出现次数）
        }//访问每个元素指向的树结点，存到aidSet里

        Map<Pair, Integer> uidPairToSupport = new HashMap();//集合对共同交集的 集合
        double size = (double)query.getUidSize();//r的大小
        double threshold = query.getThreshold();//阈值
        double minSize = threshold * size;
        double var10000 = size / threshold;//上界
        String queryUid = query.getUid();//r标识符
        Iterator var17 = query.getAidPairs().iterator();//var17 为query的元素集合
//遍历一个r的集合 集合由(ai,|S'(ai)|)组成
        while(true) {
            FVTreeNode currentNode;
            int support;
            do {
                if (!var17.hasNext()) {//此乃汇总 已经遍历完一个集合r的所有元素了 进行清算
                    //统计候选集个数
                    //System.out.println("candidate has" +uidPairToSupport.size());
                    //numofcandidates+=uidPairToSupport.size();
                    //System.out.println("The numofcandidates of " + queryUid +" is " + uidPairToSupport.size());
                    MineResult result = new MineResult(queryUid);
                    Iterator var21 = uidPairToSupport.keySet().iterator();//遍历 访问过的S结点
                    numofcandidates += uidPairToSupport.keySet().size();
                    while(var21.hasNext()) {
                        Pair uidPair = (Pair)var21.next();//(si,|si|)
                        String uid = uidPair.getFirst();  //si
                        support = uidPairToSupport.get(uidPair); //得到fi,j
                        double weight = (double)Math.round((double)support / ((double)uidPair.getSecond() + size - (double)support) * 100.0) / 100.0;//得到 并集/交集
                        if (weight >= threshold) {
                            result.add(uid, weight);//判断r跟这个s是否相似
                        }
                        //uid大小和
                       // double weight = support
                    }

                    return result;
                }
                //aidPair还有元素，继续搜素，获得指向的树结点向上搜素
                Pair aidPair = (Pair)var17.next();//遍历下一元素
                String aid = aidPair.getFirst();
                currentNode = this.aidToLastNode.get(aid);
            } while(aidSet.getOrDefault(currentNode, 0) == 0);//此时该元素 指向的树结点已经被遍历了；换下一个元素
            //对于一条 从特定结点到根 并没有被遍历完的情况 我们进行该路径上的遍历 而且这一条路径上的f是一起的
            support = 0;

            for(double curSize = currentNode.getSize(); currentNode != this.root && curSize * threshold <= size; curSize = (double)currentNode.getSize()) {//判断是小于 上界
                Integer c = (Integer)aidSet.getOrDefault(currentNode, 0);
                if (c != 0) {//如果存在另外一个结点指向该末尾结点 c=次数 如果是0，说明没有其他元素指向该结点
                    //System.out.println("++++");
                    aidSet.put(currentNode, 0);
                    support += c;
                }

                if (curSize >= minSize) {//判断大于下界
                    uidPairToSupport.put(currentNode.getPair(), (Integer)uidPairToSupport.getOrDefault(currentNode.getPair(), 0) + support);//只保存(si,|si|) 更新的fi,j会覆盖原来
                }

                currentNode = currentNode.getParent();
            }
        }
    }

    public long info() {
        Stack<FVTreeNode> st = new Stack();
        Iterator var2 = this.root.getChildren().iterator();

        while(var2.hasNext()) {
            FVTreeNode child = (FVTreeNode)var2.next();
            st.push(child);
        }

        new ArrayList();
        new ArrayList();
        int count = 0;

        Iterator var6;
        while(!st.empty()) {
            ++count;
            FVTreeNode node = (FVTreeNode)st.pop();
            var6 = node.getChildren().iterator();

            while(var6.hasNext()) {
                FVTreeNode child = (FVTreeNode)var6.next();
                st.push(child);
            }
        }

        System.out.println("count = " + count);
        int c = 0;

        Integer i;
//        for(var6 = this.aidToCount.values().iterator(); var6.hasNext(); c += i) {
//            i = (Integer)var6.next();
//        }

        System.out.println("c = " + c);
        double f = (double)count / (double)c;
        System.out.println("f = " + f);

        long A = 2;
        return A;
    }
    public long getnum(){
        return  numofcandidates;
    }
}
