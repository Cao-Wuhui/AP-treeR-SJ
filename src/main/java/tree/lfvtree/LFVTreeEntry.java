//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.lfvtree;

import utils.Pair;

public class LFVTreeEntry {
    private Pair pair;
    private LFVTreeNode node;
    private int index = 0;
    private boolean isEnd = false;

    public LFVTreeEntry() {
    }

    public LFVTreeEntry(Pair pair, LFVTreeNode node, int index) {
        this.pair = pair;
        this.node = node;
        this.index = index;
    }

    public LFVTreeEntry up() {
        return this.node.getEntry(this.index - 1);
    }

    public Pair getPair() {
        return this.pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public LFVTreeNode getNode() {
        return this.node;
    }

    public void setNode(LFVTreeNode node) {
        this.node = node;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.pair.getFirst();
    }

    public int getSize() {
        return this.pair.getSecond();
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public void setEnd(boolean end) {
        this.isEnd = end;
        this.node.getEndEntrys().add(this.index);
    }

    public String toString() {
        return "TELPSTreeEntry{pair=" + this.pair + ", index=" + this.index + '}';
    }
}

