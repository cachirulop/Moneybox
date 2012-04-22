/*******************************************************************************
 * Copyright (c) 2012 David Magro Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     David Magro Martin - initial API and implementation
 ******************************************************************************/
package com.cachirulop.moneybox.listener;

import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class TranslateAnimationListener 
	implements AnimationListener {
	
	private boolean _ended = false;
	private View _view;
	private View _layout;
	
	public TranslateAnimationListener (View image, View layout) {
		_view = image;
		_layout = layout;
	}

    public void onAnimationEnd(Animation animation) {
        if (!_ended) {
            _ended = true;
            
            updateView(animation);
        }
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }

    private void updateView (Animation animation) {
        Transformation trans = new Transformation();
        
        animation.getTransformation(animation.getStartTime() + animation.getDuration(), 
        		trans);

        float[] values = new float[9];
        Matrix m = trans.getMatrix();
        m.getValues(values);

		RelativeLayout.LayoutParams lpParams;
		RelativeLayout.LayoutParams current;
        
		current = (RelativeLayout.LayoutParams) _view.getLayoutParams();

		lpParams = new RelativeLayout.LayoutParams(current.width, current.height);
		lpParams.leftMargin = current.leftMargin;
		lpParams.rightMargin = current.rightMargin;
		lpParams.topMargin = Math.abs(_layout.getHeight() - current.height);
		lpParams.bottomMargin = _layout.getHeight() + current.height;
        
		_view.setLayoutParams(lpParams);
    }
}
