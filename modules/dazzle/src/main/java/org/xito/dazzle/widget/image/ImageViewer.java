/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * ImageViewer provides a component for viewing org.xito.launcher.images. It supports various sizing
 * behaviors and visual attributes such as reflection
 *
 * @author deane
 */
public class ImageViewer extends JComponent {

    protected BufferedImage image;
    protected BufferedImage reflectionImage;
    protected boolean show_reflection_flag;


    public void setImage(BufferedImage image) {

    }

    /**
     * Set the current image using an image URL
     * This will use a BusyWorker to Obtain the Image
     * @param url
     */
    public void setImageURL(URL url) {
        try {
            ImageIO.read(url);
        }
        catch(IOException exp) {
            exp.printStackTrace();
            //place error image
        }
    }

}
