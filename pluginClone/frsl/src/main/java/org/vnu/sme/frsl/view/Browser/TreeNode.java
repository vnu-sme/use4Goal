package org.vnu.sme.frsl.view.Browser;

import java.util.ArrayList;
import java.util.Collection;

public class TreeNode {
    protected int lenght;
    protected ArrayList<Collection<?>> list;
    protected ArrayList<String> listName;
    protected String top;
    public TreeNode() {
        list = new ArrayList<>();
        listName = new ArrayList<>();
        lenght = 0;
    }

    public Collection<?> getValue(int index) {
        return list.get(index);
    }

    public String getName(int index) {
        return listName.get(index);
    }
    
    public String getTop() {
        return top;
    }

    public int getLenght() {
        return lenght;
    }
}
