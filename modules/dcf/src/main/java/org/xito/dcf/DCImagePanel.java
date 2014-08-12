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

package org.xito.dcf;

import java.net.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.3 $
 * @since $Date: 2007/11/28 03:52:40 $
 */
public class DCImagePanel extends JPanel {

  Image image;
  Paint filterPaint;

  public DCImagePanel()
  {
    super();
  }

  public DCImagePanel(Image pImage)
  {
    super();
    image = pImage;
  }

  public DCImagePanel(String pStr)
  {
    super();
    URL _u = getClass().getResource(pStr);
    image = (new ImageIcon(_u)).getImage();
  }

  public void paintComponent(Graphics pGraphics)
  {
    pGraphics.drawImage(image,0,0,null);
    if(filterPaint != null)
    {
      ((Graphics2D)pGraphics).setPaint(filterPaint);
      pGraphics.fillRect(0,0,getWidth(), getHeight());
    }
  }

  public void setImage(Image pImage)
  {
    image = pImage;
  }

  public void setFilterPaint(Paint pPaint)
  {
    filterPaint = pPaint;
  }

}
