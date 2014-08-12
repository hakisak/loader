// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.appmanager;

import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.*;

import org.xito.appmanager.store.ApplicationNode;
import org.xito.appmanager.store.GroupNode;


/**
 *
 * @author Deane Richan
 */
public class ApplicationTreeNodeWrapper extends AppStoreTreeNodeWrapper {
   
   /** Creates a new instance of AppTreeNode */
   public ApplicationTreeNodeWrapper(GroupTreeNodeWrapper parent, ApplicationNode node) {
      setParent(parent);
      
      if(node == null) {
         throw new NullPointerException("node cannot be null");
      }
      setUserObject(node);
      super.allowsChildren = false;
   }
   
   public ApplicationNode getApplicationNode() {
      return (ApplicationNode)getUserObject();
   }
   
   public Action getAction() {
      return getApplicationNode().getAction();
   }
         
}
