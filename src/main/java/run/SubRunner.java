package run;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tree.base.MineResult;
import tree.base.Query;
import tree.base.RunnerBase;
import tree.base.TreeBase;

class SubRunner extends RunnerBase {
    List<Query> queryList = new ArrayList();
    TreeBase tree;
    final BufferedWriter writer;
    public SubRunner(TreeBase tree, BufferedWriter writer) {
        this.tree = tree;
        this.writer = writer;
    }

    public void add(Query query) {
        this.queryList.add(query);
    }

    public void run() {
        Iterator var1 = this.queryList.iterator();//遍历该线程的查询集

        while(var1.hasNext()) {
            Query query = (Query)var1.next();
            MineResult res = this.tree.search(query);

            try {
                synchronized(this.writer) {
                    this.writer.write(res.toString());
                }
            } catch (IOException var7) {
                var7.printStackTrace();
            }
        }
    }
}
