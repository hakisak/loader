package org.xito.boot.ui;

/**
 * Copyright 2017
 * Licensed by Apache License 2.0
 * https://www.apache.org/licenses/LICENSE-2.0.txt
 */
public interface ErrorPromptHandler {

    void showError(String title, String subtitle, String message, Exception exp);

    enum PromptResult {
        NO,
        YES,
        CANCEL,
        OK
    }
}