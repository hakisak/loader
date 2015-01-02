package org.xito.boot.ui;

/**
 * Created by deane on 1/2/15.
 */
public class DefaultProxyPromptHandler implements ProxyPromptHandler {

    /*
    ProxyConfigPanel configPanel = new ProxyConfigPanel(this);
    DialogDescriptor desc = new DialogDescriptor();

    //desc.setWidth(375);
    //desc.setHeight(425);
    desc.setWindowTitle("Proxy Configuration");
    desc.setTitle("Proxy Configuration");
    desc.setSubtitle("Configure a Proxy Server for this application");
    desc.setCustomPanel(configPanel);
    desc.setType(DialogManager.OK_CANCEL);
    desc.setGradiantColor(Defaults.DIALOG_GRAD_COLOR);
    desc.setGradiantOffsetRatio(Defaults.DIALOG_GRAD_OFFSET);
    desc.setIcon(new ImageIcon(this.getClass().getResource("ui/org.xito.launcher.images/proxy32.gif")));
    desc.setPack(true);
    //desc.setResizable(true);

    int result = DialogManager.showDialog(desc);

    //If cancel we don't save the settings meaning they will
    //be prompted again
    if(result == DialogManager.CANCEL) {
        return;
    }
    //If Ok then we save and use those settings
    else if(result == DialogManager.OK) {
        configPanel.updateConfig();
        if(useProxy()) {
            storeProperties(System.getProperties());
        }

        storeSettings();
    }*/
}
