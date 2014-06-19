package com.svo.platform.filechooser.tree;

public interface TreeListener {
    void onTreeNodesChanged(TreeEvent e);
    void onTreeNodesInserted(TreeEvent e);
    void onTreeNodesRemoved(TreeEvent e);
    void onTreeStructureChanged(TreeEvent e);
}
