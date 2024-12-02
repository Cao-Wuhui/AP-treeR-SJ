//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.Pair;

public class Query implements Comparable<Query> {
    private String uid;
    private int uidSize;
    private List<Pair> aidPairs = new ArrayList();
    double threshold;

    public Query() {
    }

    public Query(String uid, int uidSize, double threshold) {
        this.uid = uid;
        this.uidSize = uidSize;
        this.threshold = threshold;
    }

    public String getUid() {
        return this.uid;
    }

    public int getUidSize() {
        return this.uidSize;
    }

    public List<Pair> getAidPairs() {
        return this.aidPairs;
    }

    public void addPair(Pair pair) {
        this.aidPairs.add(pair);
    }

    public double getThreshold() {
        return this.threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUidSize(int uidSize) {
        this.uidSize = uidSize;
    }

    public int compareTo(Query o) {
        int compare = o.uidSize - this.uidSize;
        return compare == 0 ? this.uid.compareTo(o.uid) : compare;
    }

    public String toString() {
        return "Query{uid='" + this.uid + '\'' + ", uidSize=" + this.uidSize + ", aidPairs=" + this.aidPairs + ", threshold=" + this.threshold + '}';
    }

    public void sortAirPairs() {
        Collections.sort(this.aidPairs);
        //System.out.println(this.aidPairs);
    }
}
