package org.xito.boot.ui;

import org.xito.boot.ExecutableDesc;
import org.xito.boot.PolicyStore;

import java.security.Permission;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

/**
 * Created by deane on 1/1/15.
 */
public interface PermissionPromptHandler {

    public boolean promptForPermission(PolicyStore policyStore,
                                       Logger securityLogger,
                                       String subtitle,
                                       String msg,
                                       Permission perm,
                                       ExecutableDesc execDesc);

    public int promptForPermission(PolicyStore policyStore,
                                   Logger securityLogger,
                                   ProtectionDomain domain,
                                   ExecutableDesc execDesc,
                                   Permission permission);

}
