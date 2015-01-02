package org.xito.boot.ui;

import org.xito.boot.*;
import org.xito.dialog.DialogManager;

import javax.swing.*;
import java.awt.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by deane on 1/1/15.
 */
public class DefaultPermissionPromptHandler implements PermissionPromptHandler {

    private SimplePermPromptDialog permPromptDialog;

    /**
     * Build initial Permission Prompt Dialogs
     */
    private void initDialogs() {

        permPromptDialog = new SimplePermPromptDialog();
        //securityDialog = new SecurityPermissionDialog(policyStore, securityManager.securityLogger);
    }

    @Override
    public boolean promptForPermission(PolicyStore policyStore,
                                       Logger securityLogger,
                                       String subtitle,
                                       String msg,
                                       Permission perm,
                                       ExecutableDesc execDesc) {

        //The caller must be trusted so display the Permission Prompt
        permPromptDialog.setTitles(null , subtitle);
        permPromptDialog.setMessageText(msg);
        if(EventQueue.isDispatchThread()) {
            permPromptDialog.setVisible(true);
        }
        else {
            Boot.invokeAndWait(new Runnable() {
                public void run() {
                    permPromptDialog.setVisible(true);
                }
            });
        }

        //Store Option
        int option = permPromptDialog.getSelectedPermOption();
        boolean granted = (permPromptDialog.getResult() == DialogManager.YES);
        permPromptDialog.reset();

        if(granted && option == BootPolicy.ALWAYS_APP) {
            try {
                Permissions perms = new Permissions();
                perms.add(perm);
                policyStore.storePermissions(execDesc, perms);
            }
            catch(PolicyStoreException exp) {
                String title = Resources.bundle.getString("policy.store.error.title");
                String error_msg = Resources.bundle.getString("policy.store.error.msg");
                Boot.showError(title, "", error_msg, exp);
            }
        }

        return granted;

    }

    @Override
    public int promptForPermission(PolicyStore policyStore,
                                   Logger securityLogger,
                                   ProtectionDomain domain,
                                   ExecutableDesc execDesc,
                                   Permission permission) {

        final SecurityPermissionDialog securityDialog = new SecurityPermissionDialog(policyStore, securityLogger);

        //The caller must be trusted to display the Permission Prompt
        Integer result = (Integer) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                securityDialog.setSecurityInfo(domain, execDesc, permission);
                if (EventQueue.isDispatchThread()) {
                    securityDialog.setVisible(true);
                } else {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                Toolkit.getDefaultToolkit().beep();
                                securityDialog.setVisible(true);
                            }
                        });
                    } catch (Exception exp) {
                        securityLogger.log(Level.SEVERE, exp.getMessage(), exp);
                    }
                }

                return new Integer(securityDialog.getGrantOption());
            }
        });

        return result.intValue();
    }
}
