//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.lfvtree;

import utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LFVTreeNode {
    private List<LFVTreeEntry> entrys;
    private LFVTreeNode parent;
    private List<LFVTreeNode> childs = new ArrayList();
    private List<Integer> endEntrys = new ArrayList();

    public LFVTreeNode() {
    }

    public LFVTreeNode(Pair pair, LFVTreeNode parent) {
        this.entrys = new ArrayList();
        this.addEntry(pair);
        this.parent = parent;
        parent.childs.add(this);
    }

    public List<Integer> getEndEntrys() {
        return this.endEntrys;
    }

    public List<LFVTreeEntry> getEntrys() {
        return this.entrys;
    }

    public LFVTreeNode getParent() {
        return this.parent;
    }

    public List<LFVTreeNode> getChilds() {
        return this.childs;
    }

    public String getName() {
        return ((LFVTreeEntry)this.entrys.get(0)).getName();
    }

    public int getSize() {
        return ((LFVTreeEntry)this.entrys.get(0)).getSize();
    }

    public LFVTreeNode getChildByName(String name) {
        Iterator var2 = this.childs.iterator();

        LFVTreeNode child;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            child = (LFVTreeNode)var2.next();
        } while(!name.equals(child.getName()));

        return child;
    }

    public LFVTreeEntry addEntry(Pair p) {
        LFVTreeEntry entry = new LFVTreeEntry(p, this, this.entrys.size());
        this.entrys.add(entry);
        return entry;
    }

    public LFVTreeEntry getEntry(int index) {
        return (LFVTreeEntry)this.entrys.get(index);
    }

    public void resetEntryIndex() {
        int i = 0;

        for(Iterator var2 = this.entrys.iterator(); var2.hasNext(); ++i) {
            LFVTreeEntry entry = (LFVTreeEntry)var2.next();
            entry.setNode(this);
            entry.setIndex(i);
            if (entry.isEnd()) {
                this.endEntrys.add(i);
            }
        }

    }

    public void setParent(LFVTreeNode parent) {
        this.parent = parent;
    }

    public LFVTreeNode split(int index, Pair pair) {
        LFVTreeNode node = new LFVTreeNode();//新建一个结点
        LFVTreeEntry p = (LFVTreeEntry)this.entrys.get(index);//
        node.parent = this;//结点父指针=this
        node.entrys = new ArrayList(this.entrys.subList(index, this.entrys.size()));//结点的数组 为原数组的子数组
        node.resetEntryIndex();//更新数组的index
        this.entrys.subList(index, this.entrys.size()).clear();
        List<Integer> newEndEntrys = new ArrayList();
        Iterator var6 = this.endEntrys.iterator();

        while(var6.hasNext()) {
            Integer integer = (Integer)var6.next();
            if (integer < index) {
                newEndEntrys.add(integer);
            }
        }

        this.endEntrys = newEndEntrys;
        List<LFVTreeNode> temp = this.childs;//this.childs与node.childs进行交换
        this.childs = node.childs;
        node.childs = temp;
        Iterator var10 = node.childs.iterator();

        while(var10.hasNext()) {
            LFVTreeNode child = (LFVTreeNode)var10.next();
            child.setParent(node);
        }

        this.childs.add(node);
        return new LFVTreeNode(pair, this);
    }

    public String toString() {
        return "TELPSTreeNode{, entrys=" + this.entrys + '}';
    }
}
