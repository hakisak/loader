package test.xito.dazzle;

import org.xito.dazzle.widget.panel.SourceListPanel;
import org.xito.dazzle.widget.panel.SourceListTreeBadgeProvider;
import org.xito.dazzle.widget.panel.SplitPane;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * SourceListTest
 *
 * @author drichan
 */
public class SourceListTest {

   public static void main(String[] args) {

      JFrame frame = new JFrame("SplitPane Test");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      SplitPane splitPane = new SplitPane(SplitPane.HORIZONTAL_SPLIT);

      SourceListPanel sourceListPanel = new SourceListPanel();
      sourceListPanel.setSourceListTreeBadgeProvider(new MyBadgeProvider());
      sourceListPanel.addSection("mailboxes", new JTree().getModel(), null);
      sourceListPanel.addSection("reminders", new JTree().getModel(), null);

      JScrollPane sp = new JScrollPane(sourceListPanel);
      sp.setBorder(null);

      splitPane.setLeftComponent(sp);
      splitPane.setRightComponent(new JPanel());
      splitPane.setDividerLocation(250);

      frame.setContentPane(splitPane);
      frame.setSize(600, 600);
      frame.setVisible(true);

   }

   public static class MyBadgeProvider implements SourceListTreeBadgeProvider {
      public String getBadgeTextForPath(TreeModel model, TreePath path) {
         return "3";
      }
   }
}
