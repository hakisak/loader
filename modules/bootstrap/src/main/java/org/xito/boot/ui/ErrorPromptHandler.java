package org.xito.boot.ui;

/**
 * Created by deane on 1/1/15.
 */
public interface ErrorPromptHandler {

    public enum PromptResult{OK, CANCEL, YES, NO}

    public void showError(String title, String subtitle, String message, Exception exp);
}
