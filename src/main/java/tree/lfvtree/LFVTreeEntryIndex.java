//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.lfvtree;

public class LFVTreeEntryIndex {
    private String aid;
    private LFVTreeNode node;
    private int index;

    public LFVTreeEntryIndex(LFVTreeNode node, int index) {
        this.node = node;
        this.index = index;
    }

    public LFVTreeNode getNode() {
        return this.node;
    }

    public void setNode(LFVTreeNode node) {
        this.node = node;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

