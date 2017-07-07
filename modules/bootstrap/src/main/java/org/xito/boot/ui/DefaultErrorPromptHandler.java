package org.xito.boot.ui;

import org.xito.boot.Boot;


/**
 * Created by deane on 1/1/15.
 */
public class DefaultErrorPromptHandler implements ErrorPromptHandler {

    @Override
    public void showError(String title, String subtitle, String message, Exception exp) {


        //TODO fix this to not call back to Boot
        Boot.showError(title, subtitle, message, exp);
    }
}
