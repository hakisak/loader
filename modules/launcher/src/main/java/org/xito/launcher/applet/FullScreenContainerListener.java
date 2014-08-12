package org.xito.launcher.applet;

/**
 * Listen for changes to FullScreenContainer Mode. Listeners will be notified when a FullScreenContainer switches to 
 * full screen mode or when it switched back to windowed mode
 * 
 * @author drichan
 */
public interface FullScreenContainerListener {

   public void switchedFullScreenMode(FullScreenContainer fullScreenContainer, boolean isFullScreen);
   
}
