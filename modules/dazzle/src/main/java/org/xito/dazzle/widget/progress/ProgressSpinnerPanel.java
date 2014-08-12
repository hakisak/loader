// Copyright 2007 Xito.org
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
package org.xito.dazzle.widget.progress;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.utilities.UIUtilities;
import org.xito.dazzle.widget.DecorationComponent;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import org.xito.dazzle.animation.timing.*;

import org.xito.dazzle.widget.TransparencyDecorator;
import org.xito.dazzle.widget.button.BasicButton;
import org.xito.dialog.TableLayout;

/**
 * Provides a Panel that includes a text description and ProgressSpinner
 * 
 * @author Deane Richan
 */
public class ProgressSpinnerPanel extends DecorationComponent {

    //Serial Version ID
    private static final long serialVersionUID = 1L;
    private ProgressSpinner.SpinnerTheme myTheme;
    private JLabel textLbl;
    private CancelButton cancelBtn;
    private ProgressSpinner spinner;
    private Box compPanel;
    private Component cancelPadding;
    private int fadeInTime = 1000;
    private int fadeOutTime = 500;
    
    private TransparencyDecorator transparencyDecorator = new TransparencyDecorator();
   private boolean cancel_enabled_flag;
   private Runnable beforeStartTask;
   private Runnable afterStopTask;
   private Runnable cancelTask;
   private boolean running_flag;

    public ProgressSpinnerPanel() {
        this(ProgressSpinner.defaultTheme);
    }

    public ProgressSpinnerPanel(ProgressSpinner.SpinnerTheme theme) {

        myTheme = theme;

        setLayout(new BorderLayout());
        setOpaque(false);
        spinner = new ProgressSpinner(myTheme);
        add(spinner, BorderLayout.WEST);
        
        //FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 0, 0);
        compPanel = new Box(BoxLayout.X_AXIS);
        compPanel.setOpaque(false);
        add(compPanel, BorderLayout.CENTER);
        
        textLbl = new JLabel();
        textLbl.setHorizontalAlignment(JLabel.LEFT);
        compPanel.add(textLbl);
                
        int padding = myTheme.size / 5;
        cancelPadding = Box.createHorizontalStrut(padding);
        cancelBtn = new CancelButton();
        cancelBtn.setBorder(new EmptyBorder(0,0,0,padding));
        compPanel.add(cancelBtn);
        compPanel.add(cancelPadding);
        
        cancelBtn.setVisible(false);
        cancelPadding.setVisible(false);
        
        initValues();
    }

    private void initValues() {

        textLbl.setForeground(myTheme.stepHandColor);
        textLbl.setFont(textLbl.getFont().deriveFont(Font.PLAIN, myTheme.size / 5));

        // start fully transparent
        transparencyDecorator.setAlpha(0f);
        addPaintDecorator(transparencyDecorator);
    }

    public void setText(String text) {
        
       textLbl.setText(text);

        // set padding
        int padding = myTheme.size / 5;
        if (text != null || text.length() != 0) {
            textLbl.setBorder(new EmptyBorder(0, 0, 0, padding));
        }
        else {
            textLbl.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
    }
    
    public void setCancelEnabled(boolean cancelEnabled) {
       cancel_enabled_flag = cancelEnabled;
       cancelPadding.setVisible(cancel_enabled_flag);
       cancelBtn.setVisible(cancel_enabled_flag);
    }
    
    public boolean isCancelEnabled() {
       return cancel_enabled_flag;
    }
    
    public void setFadeInTime(int fadeInTime) {
       this.fadeInTime = fadeInTime;
    }
    
    public int getFadeInTime() {
       return fadeInTime;
    }

    public void setFadeOutTime(int fadeOutTime) {
       this.fadeOutTime = fadeOutTime;
    }
    
    public int getFadeOutTime() {
       return fadeOutTime;
    }
    
    /**
     * Task to execute before the spinner is started
     * @param beforeStartTask
     */
    public void setBeforeStartTask(Runnable beforeStartTask) {
       this.beforeStartTask = beforeStartTask;
    }
    
    /**
     * Task to execute after the spinner is stopped
     * @param afterStopTask
     */
    public void setAfterStopTask(Runnable afterStopTask) {
       this.afterStopTask = afterStopTask;
    }
    
    /**
     * The cancel task is executed when the user hits cancel if it is enabled.
     * The default cancel task will call stop. If a custom cancelTask is specified
     * then that task will need to call stop on its own.
     * @param cancelTask
     */
    public void setCancelTask(Runnable cancelTask) {
       this.cancelTask = cancelTask;
    }
    
    public void start() {
        
        //already running
        if(running_flag) return;
        
        running_flag = true;
        if(getWidth()==0 || getHeight()==0) {
           setSize(getPreferredSize());
        }
       
        Animator anim = new Animator(fadeInTime);
        anim.addTarget(new TimingTargetAdapter(){

            @Override
            public void begin() {
                if(beforeStartTask != null) {
                    SwingUtilities.invokeLater(beforeStartTask);
                }
                spinner.start();
            }

        });
        anim.addTarget(transparencyDecorator.createFadeIn(this, myTheme.transparency));
        anim.setStartDirection(Animator.Direction.FORWARD);
        anim.start();
    }

    public void stop() {
        
        //not running so just return       
        if(!running_flag) return;
        
        try {
           Animator anim = new Animator(fadeOutTime);
           anim.addTarget(new TimingTargetAdapter(){
   
               @Override
               public void end() {
                   spinner.stop();
                   if(afterStopTask != null) {
                       SwingUtilities.invokeLater(afterStopTask);
                   }
               }
   
           });
           anim.addTarget(transparencyDecorator.createFadeOut(this, myTheme.transparency));
           anim.setStartFraction(1.0f);
           anim.setStartDirection(Animator.Direction.BACKWARD);
           anim.start();
        }
        finally {
        
           running_flag = false;
        }
    }

    public void paint(Graphics g) {

        //let the decorators have a chance to affect painting
        processPrePaintDecorators(g, this);

        Graphics2D g2 = (Graphics2D) g;
        HashMap hints = new HashMap();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(hints);

        // setting both clip and Composite at the same time is problematic.
        // so we only set clip if component is Opaque and set composite if
        // component is NOT Opaque

        Composite orgComp = g2.getComposite();

        // set clip
        if (isOpaque()) {
            g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, myTheme.panelRadius, myTheme.panelRadius));
        }
        // set alpha composite
        else {
            g2.setColor(new Color(255, 255, 255, 0));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        int radius = myTheme.panelRadius;

        if (myTheme.drawPanelOutline) {
            g2.setColor(myTheme.panelOutlineColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }

        radius = radius - myTheme.panelOutline;
        g2.setColor(myTheme.panelBackground);
        g2.fillRoundRect(
              myTheme.panelOutline,
              myTheme.panelOutline, 
              getWidth() - (1 + myTheme.panelOutline * 2), 
              getHeight() - (1 + myTheme.panelOutline * 2),
              radius,
              radius);

        super.paint(g);

        //let decorators clean up any painting
        processPostPaintDecorators(g, this);
    }

    //-------------------------------------------------------------------------
    
    private class CancelButton extends AbstractButton {
       
       public CancelButton() {
          
          setUI(new CancelButtonUI());
          setModel(new DefaultButtonModel());
          setOpaque(false);
          
          int s = myTheme.size/3;
          setPreferredSize(new Dimension(s,s));
          setMaximumSize(getPreferredSize());
          addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if(cancelTask != null) {
                   SwingUtilities.invokeLater(cancelTask);
                }
                else {
                   ProgressSpinnerPanel.this.stop();
                }
             }
          });
       }
       
       //-------------------------------------------------------------------------
       private class CancelButtonUI extends BasicButtonUI {

         @Override
         public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
            CancelButton button = (CancelButton)c;
            
            paintButton(g2, button.getModel().isPressed());
         }

         protected void paintButton(Graphics2D g2, boolean pressed) {
            
            Color bg = pressed ? myTheme.handOutlineColor : myTheme.panelBackground;
            Color fg = pressed ? myTheme.panelBackground : myTheme.handOutlineColor;
            
            int strokeSize = 2;
            if(myTheme.size >= 150) strokeSize = 3;
            if(myTheme.size <= 50) strokeSize = 1;
                           
            g2.setColor(bg);
            g2.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.fillOval(1, 1, getWidth()-(strokeSize+1), getHeight()-(strokeSize+1));
            g2.setColor(fg);
            if(strokeSize > 1) {
               g2.drawOval(1, 1, getWidth()-(strokeSize+1), getHeight()-(strokeSize+1));
            }
            
            int x = getWidth() / 3;
            int w = getWidth() - x;
            int y = getHeight() / 3;
            int h = getHeight() - y;
            g2.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(fg);
            g2.drawLine(x, y, w, h);
            g2.drawLine(x, h, w, y);
         }
         
       }
    }
    
}
