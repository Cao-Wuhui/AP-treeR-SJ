//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.base;

import java.util.ArrayList;
import java.util.List;

//The format of result
public class MineResult {
    private String uid;
    private List<Double> weightList = new ArrayList();
    private List<String> uidList = new ArrayList();

    public MineResult(String uid) {
        this.uid = uid;
    }

    public void add(String uid, double weight) {
        this.uidList.add(uid);
        this.weightList.add(weight);
    }

    public String getUid() {
        return this.uid;
    }

    public int size() {
        return this.weightList.size();
    }

    public String getResult() {
        if (this.weightList.size() == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(this.uid.length() + this.weightList.size() * 10);

            for(int i = 0; i < this.weightList.size(); ++i) {
                sb.append((String)this.uidList.get(i)).append(',').append(this.weightList.get(i)).append(' ');
            }

            return sb.toString();
        }
    }

    public void merge(MineResult r) {
        this.weightList.addAll(r.weightList);
        this.uidList.addAll(r.uidList);
    }

    public String toString() {
        if (this.weightList.size() == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(this.uid.length() + this.weightList.size() * 10);
            sb.append(this.uid).append(' ');

            for(int i = 0; i < this.weightList.size(); ++i) {
                sb.append((String)this.uidList.get(i)).append(',').append(this.weightList.get(i)).append(' ');
            }

            sb.append('\n');
            return sb.toString();
        }
    }
}

