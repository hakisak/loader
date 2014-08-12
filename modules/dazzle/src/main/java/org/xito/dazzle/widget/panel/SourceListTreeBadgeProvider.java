package org.xito.dazzle.widget.panel;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * SourceListTreeBadgeProvider
 *
 * @author drichan
 */
public interface SourceListTreeBadgeProvider {

   public String getBadgeTextForPath(TreeModel model, TreePath path);
}
