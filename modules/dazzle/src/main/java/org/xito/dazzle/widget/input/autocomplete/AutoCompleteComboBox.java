package org.xito.dazzle.widget.input.autocomplete;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * An editable combo class that will autocomplete the user entered text to the
 * entries in the combo drop down.
 * 
 * You can directly add auto-complete to existing JComboBox derived classes
 * using: ComboCompleterFilter.addCompletion(yourCombo);
 * 
 * @author ncochran
 */
public class AutoCompleteComboBox extends JComboBox {

   public AutoCompleteComboBox(ComboBoxModel aModel) {
      super(aModel);
      // TODO Auto-generated constructor stub
   }

   public AutoCompleteComboBox(Object[] items) {
      super(items);
      _init();
   }

   public AutoCompleteComboBox(Vector<?> items) {
      super(items);
      // TODO Auto-generated constructor stub
   }

   public AutoCompleteComboBox() {
      super();
      // TODO Auto-generated constructor stub
   }

   private void _init() {
      setEditable(true);

      _filter = ComboAutoCompleteFilter.addCompletionMechanism(this);
   }

   public boolean isCaseSensitive() {
      return _filter.isCaseSensitive();
   }

   public boolean isCorrectingCase() {
      return _filter.isCorrectingCase();
   }

   public void setCaseSensitive(boolean caseSensitive) {
      _filter.setCaseSensitive(caseSensitive);
   }

   public void setCorrectCase(boolean correctCase) {
      _filter.setCorrectCase(correctCase);
   }

   private ComboAutoCompleteFilter _filter;
}
