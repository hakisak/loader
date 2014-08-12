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

import java.util.*;
import java.awt.*;
import javax.swing.*;
import org.xito.dazzle.animation.timing.Animator;
import org.xito.dazzle.animation.timing.Animator.RepeatBehavior;
import org.xito.dazzle.animation.timing.TimingTargetAdapter;
import org.xito.dazzle.widget.DecorationComponent;

/**
 * Spinner UI Animation.Similar to the Apple Busy Spinner Animation 
 * that looks like a clock face moving around
 * 
 * @author Deane Richan
 */
public class ProgressSpinner extends DecorationComponent {

    //SerialVersionID - update if the structure of the class changes
    private static final long serialVersionUID = 1L;
    private double[] stepLocations; // in radians
    private int steps = 12;
    private int currentStep = 0;
    private int revolutionTime = 1000;
    private Dimension mySize;
    private int handWidth;
    private int handHeight;
    private Animator animator;
    private int delta = 360 / steps;
    private float handRatio = .5f;
    private float handHeightRatio = .3f;
    private int handXPad;
    private int handYPad;
    private int handX;
    private int handY;
    private boolean drawOutlines = true;
    private Stroke outlineStroke = new BasicStroke(.25f);
    private Color outlineColor = new Color(150, 150, 150);
    private Color stepHandColor = Color.BLACK;
    private Color stepHandColor1 = new Color(50, 50, 50);
    private Color stepHandColor2 = new Color(100, 100, 100);
    private Color stepHandColor3 = new Color(150, 150, 150);
    private Color handColor = new Color(225, 225, 225);
    private SpinnerTheme myTheme;

    public ProgressSpinner() {
        this(defaultTheme);
    }

    public ProgressSpinner(SpinnerTheme theme) {
        myTheme = theme;
        initValues();
    }

    private void initValues() {

        mySize = new Dimension(myTheme.size, myTheme.size);
        steps = myTheme.steps;
        drawOutlines = myTheme.drawHandOutlines;
        outlineStroke = myTheme.handStroke;
        outlineColor = myTheme.handOutlineColor;
        stepHandColor = myTheme.stepHandColor;
        stepHandColor1 = myTheme.stepHandColor1;
        stepHandColor2 = myTheme.stepHandColor2;
        stepHandColor3 = myTheme.stepHandColor3;
        handColor = myTheme.handColor;

        // set sizes
        setSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());

        // get step rotations
        stepLocations = new double[steps];
        for (int i = 0; i < steps; i++) {
            stepLocations[i] = Math.toRadians(delta * i);
        }

        // set sizes
        handWidth = (int) ((mySize.width / 2.5) * handRatio) - 1;
        handHeight = (int) (handWidth * handHeightRatio);

        // set paddings
        handXPad = handWidth;
        handYPad = -(handHeight / 2);

        handX = (mySize.width / 2) + handXPad;
        handY = (mySize.height / 2) + handXPad;
    }

    public void start() {

        animator = new Animator(revolutionTime);

        animator.addTarget(new TimingTargetAdapter() {

            @Override
            public void begin() {
                currentStep = 0;
            }


            @Override
            public void timingEvent(float fraction) {
                currentStep = (int)(steps * fraction);
                ProgressSpinner.this.repaint();
            }

        });
        animator.setRepeatBehavior(RepeatBehavior.LOOP);
        animator.setStartDirection(Animator.Direction.FORWARD);
        animator.setRepeatCount(Animator.INFINITE);
        animator.start();
    }

    public void stop() {
        animator.stop();
    }

    public Dimension getPreferredSize() {
        return mySize;
    }
   
    private Color getStepColor(int currentStep, int i) {

        int delta = currentStep - i;
        if (delta < 0) {
            delta = steps + delta;
        }

        switch (delta) {
            case 0:
                return stepHandColor;
            case 1:
                return stepHandColor1;
            case 2:
                return stepHandColor2;
            case 3:
                return stepHandColor3;
            default:
                return handColor;
        }
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        HashMap hints = new HashMap();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(hints);
        g2.setStroke(outlineStroke);

        // move to center
        g2.translate(handX - handXPad, handY - handXPad);
        
        for (int i = 0; i < steps; i++) {
            g2.rotate(stepLocations[i]);

            // fill all hands
            g2.setPaint(getStepColor(currentStep, i));
            g2.fillRoundRect(handXPad, handYPad, handWidth, handHeight, handHeight, handHeight);

            // draw outline of all hands
            if (drawOutlines) {
                g2.setPaint(outlineColor);
                g2.drawRoundRect(handXPad, handYPad, handWidth, handHeight, handHeight, handHeight);
            }

            // rotate back to origin
            g2.rotate(-stepLocations[i]);
        }
    }

    public static class SpinnerTheme {

        public int size = 100;
        public int steps = 12;
        public boolean drawHandOutlines = true;
        public int panelOutline = 4;
        public int panelRadius = 40;
        public Color panelBackground = Color.WHITE;
        public boolean drawPanelOutline = true;
        public Color panelOutlineColor = new Color(150, 150, 150);
        public Stroke handStroke = new BasicStroke(.25f);
        public Color stepHandColor = Color.BLACK;
        public Color stepHandColor1 = new Color(50, 50, 50);
        public Color stepHandColor2 = new Color(100, 100, 100);
        public Color stepHandColor3 = new Color(150, 150, 150);
        public Color handColor = new Color(225, 225, 225);
        public Color handOutlineColor = new Color(150, 150, 150);
        public float transparency = 1.0f;
        
        /**
         * Make a copy of the specified theme
         * @param this
         * @return
         */
        public SpinnerTheme copy() {
           SpinnerTheme dest = new SpinnerTheme();
           dest.size = this.size;
           dest.steps = this.steps;
           dest.drawHandOutlines  = this.drawHandOutlines;
           dest.panelOutline = this.panelOutline;
           dest.panelRadius = this.panelRadius;
           dest.panelBackground = this.panelBackground;
           dest.drawPanelOutline = this.drawPanelOutline;
           dest.panelOutlineColor = this.panelOutlineColor;
           dest.handStroke = this.handStroke;
           dest.stepHandColor = this.stepHandColor;
           dest.stepHandColor1 = this.stepHandColor1;
           dest.stepHandColor2 = this.stepHandColor2;
           dest.stepHandColor3 = this.stepHandColor3;
           dest.handColor = this.handColor;
           dest.handOutlineColor = this.handOutlineColor;
           dest.transparency = this.transparency;
           return dest;
        }
        
        /**
         * Sets the size of the spinner and automatically sets the panel radius to match
         * @return
         */
        public SpinnerTheme setSize(int size) {
           float radiusRatio = (float)this.panelRadius / this.size;
           float outlineRatio = (float)this.panelOutline / this.size;
           this.size = size;
           this.panelRadius = (int)(size * radiusRatio);
           this.panelOutline = (int)(size * outlineRatio);
           return this;
        }
    }
    public static SpinnerTheme defaultTheme = new SpinnerTheme();
    public static SpinnerTheme whiteTheme = defaultTheme;
    public static SpinnerTheme blackTheme = new SpinnerTheme();

    //black theme


    static {
        blackTheme.drawHandOutlines = false;
        blackTheme.stepHandColor = Color.WHITE;
        blackTheme.handOutlineColor = new Color(225, 225, 225);
        blackTheme.stepHandColor1 = new Color(200, 200, 200);
        blackTheme.stepHandColor2 = new Color(150, 150, 150);
        blackTheme.stepHandColor3 = new Color(100, 100, 100);
        blackTheme.handColor = new Color(50, 50, 50);
        blackTheme.transparency = .90f;
        blackTheme.panelBackground = Color.BLACK;
        blackTheme.drawPanelOutline = false;
        blackTheme.panelOutlineColor = blackTheme.panelBackground;
    }
}

