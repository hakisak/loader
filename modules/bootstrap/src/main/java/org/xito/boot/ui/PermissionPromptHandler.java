package org.xito.boot.ui;

import org.xito.boot.ExecutableDesc;
import org.xito.boot.PolicyStore;

import java.security.Permission;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

/**
 * Copyright 2017
 * Licensed by Apache License 2.0
 * https://www.apache.org/licenses/LICENSE-2.0.txt
 */
public interface PermissionPromptHandler {


    boolean promptForPermission(PolicyStore policyStore,
                                Logger securityLogger,
                                String subtitle,
                                String msg,
                                Permission perm,
                                ExecutableDesc execDesc);


    int promptForPermission(PolicyStore policyStore,
                            Logger securityLogger,
                            ProtectionDomain domain,
                            ExecutableDesc execDesc,
                            Permission permission);
}
