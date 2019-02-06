package org.xito.boot.ui;

import org.xito.boot.*;

import java.security.*;
import java.util.logging.Logger;

/**
 * Created by deane on 1/1/15.
 */
public class DefaultPermissionPromptHandler implements PermissionPromptHandler {

    @Override
    public boolean promptForPermission(PolicyStore policyStore,
                                       Logger securityLogger,
                                       String subtitle,
                                       String msg,
                                       Permission perm,
                                       ExecutableDesc execDesc) {

        //TODO FIX
        return true;

    }

    @Override
    public int promptForPermission(PolicyStore policyStore,
                                   Logger securityLogger,
                                   ProtectionDomain domain,
                                   ExecutableDesc execDesc,
                                   Permission permission) {
        //TODO FIX
      return 0;
    }
}
