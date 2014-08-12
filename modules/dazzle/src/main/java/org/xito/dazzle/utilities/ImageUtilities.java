// Copyright 2009 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.dazzle.utilities;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;

/**
 *
 *
 * Some Utility methods obtained from:
 * http://forums.sun.com/thread.jspa?threadID=522483&forumID=20
 *
 * @author deane
 */
public class ImageUtilities {

    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    public static BufferedImage createCompatibleBufferedImage(int width, int height, int transparency) {
       GraphicsConfiguration gc = getDefaultConfiguration();
       return gc.createCompatibleImage(width, height, transparency);
    }
    
    public static VolatileImage createCompaibleVolatileImage(int width, int height, int transparency) {
       GraphicsConfiguration gc = getDefaultConfiguration();
       return gc.createCompatibleVolatileImage(width, height, transparency);
    }
    
    public static BufferedImage toCompatibleImage(BufferedImage image) {
       return toCompatibleImage(image, null);
    }
    
    public static BufferedImage toCompatibleImage(BufferedImage image, GraphicsConfiguration gc) {
        if (gc == null) {
            gc = getDefaultConfiguration();
        }
        int w = image.getWidth();
        int h = image.getHeight();
        int transparency = image.getColorModel().getTransparency();
        BufferedImage result = gc.createCompatibleImage(w, h, transparency);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(image, null);
        g2.dispose();
        return result;
    }

    //returns target
    public static BufferedImage copy(BufferedImage source, BufferedImage target) {
        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scalex = (double) target.getWidth() / source.getWidth();
        double scaley = (double) target.getHeight() / source.getHeight();
        AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
        g2.drawRenderedImage(source, xform);
        g2.dispose();
        return target;
    }

    public static BufferedImage getScaledInstance(BufferedImage image, int width, int height) {
        GraphicsConfiguration gc = getDefaultConfiguration();
        
        int transparency = image.getColorModel().getTransparency();
        return copy(image, gc.createCompatibleImage(width, height, transparency));
    }

    public static BufferedImage getScaledInstance(BufferedImage image, int width, int height, GraphicsConfiguration gc) {
        if (gc == null) {
            gc = getDefaultConfiguration();
        }
        int transparency = image.getColorModel().getTransparency();
        return copy(image, gc.createCompatibleImage(width, height, transparency));
    }

    public static BufferedImage getScaledInstance(BufferedImage image, int width, int height, ColorModel cm) {
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isRasterPremultiplied = cm.isAlphaPremultiplied();
        return copy(image, new BufferedImage(cm, raster, isRasterPremultiplied, null));
    }
}
