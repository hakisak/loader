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

package org.xito.launcher.jnlp.service;

import java.awt.*;
import java.awt.print.*;
import java.security.*;
import java.util.*;
import java.text.*;
import javax.jnlp.*;
import org.xito.boot.*;
import org.xito.dialog.*;
import org.xito.launcher.Resources;

/**
 *
 * @author DRICHAN
 */
public class PrintServiceImpl extends AbstractServiceImpl implements PrintService  {
   
   private boolean printPermission;
      
   /** Creates a new instance of PrintServiceImpl */
   public PrintServiceImpl(AppInstance app) {
      super(app);
   }

   /**
    * Print a Printable PrintJob
    */
   public boolean print(Printable printable) {
      PrinterJob job = getPrinterJob();
      if(job ==null) return false;
      
      job.setPrintable(printable);
      boolean result = job.printDialog();
      if(result) {
         try {
            job.print();
            //Once we physically Print we reset the Permission
            printPermission = false;
         }
         catch(PrinterException exp) {
            exp.printStackTrace();
            return false;
         }
      }
            
      return result;
   }

   public boolean print(Pageable pageable) {
      PrinterJob job = getPrinterJob();
      if(job ==null) return false;
      
      job.setPageable(pageable);
      boolean result = job.printDialog();
      if(result) {
         try {
            job.print();
            //Once we physically Print we reset the Permission
            printPermission = false;
         }
         catch(PrinterException exp) {
            exp.printStackTrace();
            return false;
         }
      }
      
      return result;
   }

   /**
    * Show a Page Format Dialog. This doesn't cause the user to see a permission
    * prompt
    */
   public PageFormat showPageFormatDialog(final PageFormat pageFormat) {
   
      //We give the App permission just long enough to get a job so we
      //Can format a page.
      printPermission = true;
      PrinterJob job = getPrinterJob();
      printPermission = false;
      
      if(job ==null) return null;
            
      return job.pageDialog(pageFormat);
   }

   /**
    * Just get a Default Page. We don't prompt the user for this
    */
   public PageFormat getDefaultPage() {
      
      //We give the App permission just long enough to get a job so we
      //Can format a page.
      printPermission = true;
      PrinterJob job = getPrinterJob();
      printPermission = false;
      
      if(job ==null) return null;
            
      return job.defaultPage();
   }
   
   /**
    * Gets a Printer Job this may prompt the User for the Permission
    */
   private PrinterJob getPrinterJob() {
      
      final RuntimePermission perm = new RuntimePermission("queuePrintJob");
      boolean havePerm = printPermission;
      if(!havePerm) {
         havePerm = checkPermission(perm);
      }
      
      if(!havePerm) {
         String subtitle = Resources.jnlpBundle.getString("printer.access.subtitle");
         String msg = Resources.jnlpBundle.getString("printer.access.msg");
         msg = MessageFormat.format(msg, appInstance.getAppDesc().getDisplayName());
         havePerm = promptForPermission(subtitle, msg, perm);
      }
      
      PrinterJob job=null;
      if(havePerm) {
         printPermission = true;
         //Do in a PrivilegedAction
         job = (PrinterJob)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return PrinterJob.getPrinterJob();
            }
         });
      }

      return job;
   }
}
