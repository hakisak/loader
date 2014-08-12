// Copyright (C) 2002 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm). 
//
// This is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this application.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.1.1.1 $
 * @since $Date: 2003/09/13 04:39:26 $
 */
public class DownloadStatusDialog extends JDialog
{
  public static final String CLOCK_IMAGE = "org/xito/launcher/images/clock.gif";
  JProgressBar progressBar;

  /** Creates new DownloadStatusDialog */
  public DownloadStatusDialog(Frame pOwner, String pTitle)
  {
    super(pOwner, pTitle);
    if(pTitle == null || pTitle.length()==0)
    {
      setTitle("Download...");
    }

    init();
  }

  private void init()
  {
    setSize(400,200);
    setResizable(false);
    getContentPane().setLayout(null);
    getContentPane().setBackground(Color.white);

    //Clock Image
    ImageIcon _image = new ImageIcon(getClass().getResource(CLOCK_IMAGE));
    JLabel _clock = new JLabel(_image);
    _clock.setBounds(5,5, _image.getIconWidth(), _image.getIconHeight());
    getContentPane().add(_clock);

    //ProgressBar
    progressBar = new JProgressBar();
    progressBar.setBounds(15,150, 370, 20);
    progressBar.setStringPainted(true);
    getContentPane().add(progressBar);

  }

  public void updateData(int pTotalProgress, int pCurrentProgress)
  {
    progressBar.setMaximum(pTotalProgress);
    progressBar.setValue(pCurrentProgress);
    progressBar.setString(((int)(progressBar.getPercentComplete()*100)) + " %");


  }

}
