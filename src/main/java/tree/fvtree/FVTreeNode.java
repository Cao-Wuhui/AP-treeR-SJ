//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.fvtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import utils.Pair;

public class FVTreeNode {
    private String name;
    private int size;
    private FVTreeNode parent;
    private List<FVTreeNode> children = new ArrayList();
    private String endFlag;
    private Pair pair;

    public FVTreeNode() {
    }

    public FVTreeNode(Pair pair, FVTreeNode parent) {
        this.name = pair.getFirst();
        this.size = pair.getSecond();
        this.pair = pair;
        this.parent = parent;
        parent.children.add(this);
        this.endFlag = "";
    }

    public List<FVTreeNode> getChildren() {
        return this.children;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public FVTreeNode getParent() {
        return this.parent;
    }

    public String getEndFlag() {
        return this.endFlag;
    }

    public Pair getPair() {
        return this.pair;
    }

    public void setEndFlag(String endFlag) {
        this.endFlag = endFlag;
    }

    public FVTreeNode getChildByName(String name) {
        Iterator var2 = this.children.iterator();

        FVTreeNode child;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            child = (FVTreeNode)var2.next();
        } while(!child.getName().equals(name));//遍历孩子 找是否有跟name同名的孩子

        return child;
    }

    public String toString() {
        return "FPSTreeNode{name='" + this.name + '\'' + ", size=" + this.size + ", parents=" + this.parent + ", children=" + this.children + ", endFlag='" + this.endFlag + '\'' + '}';
    }
}
