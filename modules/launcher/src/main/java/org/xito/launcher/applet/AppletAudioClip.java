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

package org.xito.launcher.applet;

import java.net.*;
import java.applet.*;
import javax.sound.sampled.*;

// based on Deane Richan's AppletAudioClip

/**
 * An applet audio clip using the javax.sound API.
 *
 * @author Deane Richan
 * @version $Revision: 1.5 $ 
 */
public class AppletAudioClip implements AudioClip {

    /** the clip */
    private Clip clip;


    /** 
     * Creates new AudioClip.  If the clip cannot be opened no
     * exception is thrown, instead the methods of the AudioClip
     * return without performing any operations.
     *
     * @param location the clip location
     */
    public AppletAudioClip(URL location) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(location);

            clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            clip.open(stream);
        }
        catch (Exception ex) {
           ex.printStackTrace();
           clip = null;
        }
    }

    /**
     * Plays the clip in a continuous loop until the stop method is
     * called.
     */
    public void loop() {
        if (clip == null)
            return;

        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Plays the clip from the beginning.
     */
    public void play() {
        if (clip == null)
            return;

        // applet audio clip resets to beginning when played again
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Stops playing the clip.
     */
    public void stop() {
        if (clip == null)
            return;

        clip.stop();
    }

    /**
     * Stops playing the clip and disposes it; the clip cannot be
     * played after being disposed.
     */
    void dispose() {
        if (clip != null) {
            clip.stop();
            clip.flush();
            clip.close();
        }

        clip = null;
    }

}


