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

package org.xito.dazzle.widget;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import org.xito.dazzle.animation.timing.*;

/**
 *
 * @author deane
 */
public class TransparencyDecorator implements PaintDecorator {

    //used for fade animation
    public static enum FadeType {IN, OUT};

    protected Composite orgComposite;
    protected AlphaComposite composite;

    /**
     * Occurs right after the component is painted
     * @param g
     */
    public void postPaintComponent(Graphics g, JComponent comp) {
        Graphics2D g2 = (Graphics2D) g;
        if (orgComposite != null) {
            g2.setComposite(orgComposite);
        }
    }

    /**
     * Occurs right before a component is painted
     * @param g
     */
    public void prePaintComponent(Graphics g, JComponent comp) {
        Graphics2D g2 = (Graphics2D) g;
        orgComposite = g2.getComposite();
        if (composite != null) {
            g2.setComposite(composite);
        }
    }

    public AlphaComposite getComposite() {
        return composite;
    }

    public void setComposite(AlphaComposite composite) {
        this.composite = composite;
    }

    public void setAlpha(float fraction) {
        assert (fraction >= 0.0f && fraction <= 1.0f);
        setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fraction));
    }

    public FadeAnimationTarget createFadeIn(JComponent comp, float endAlpha) {
       return new FadeAnimationTarget(comp, this, 0, endAlpha);
    }

    public FadeAnimationTarget createFadeOut(JComponent comp, float startAlpha) {
       return new FadeAnimationTarget(comp, this, startAlpha, 0);
    }

    /**
     * Class used for animation of fades
     */
    public static class FadeAnimationTarget extends TimingTargetAdapter {

        protected FadeType type;
        protected JComponent animatedComponent;
        protected TransparencyDecorator transparencyDecorator;
        protected float startAlpha,  endAlpha;

        public FadeAnimationTarget(JComponent animatedComponent,
                TransparencyDecorator transparencyDecorator,
                float startAlpha, float endAlpha) {

            //if start is greater then end then we are fading out
            //else we are fading in
            this.type = (startAlpha > endAlpha) ? FadeType.OUT : FadeType.IN;

            this.animatedComponent = animatedComponent;
            this.transparencyDecorator = transparencyDecorator;
            this.startAlpha = startAlpha;
            this.endAlpha = endAlpha;
        }

        @Override
        public void begin() {
            transparencyDecorator.setAlpha(startAlpha);
        }

        @Override
        public void end() {
            transparencyDecorator.setAlpha(endAlpha);
        }

        @Override
        public void timingEvent(float fraction) {
            
            float fractionAlpha = (type == FadeType.IN) ? endAlpha * fraction : startAlpha * fraction;
            transparencyDecorator.setAlpha(fractionAlpha);
            animatedComponent.repaint();
        }
    }
}
