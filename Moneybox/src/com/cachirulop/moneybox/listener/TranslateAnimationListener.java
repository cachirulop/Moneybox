package com.cachirulop.moneybox.listener;

import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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

        // Get the position and apply the scroll
        final float x = values[Matrix.MTRANS_X];
        final float y = values[Matrix.MTRANS_Y];
        
        //_view.scrollTo(-(int)x, -(int)y);
        //_view.scrollTo(0, 0);
        //_view.scrollTo((int)x, (int)y);
        //_view.scrollTo(10, 10);
        
		RelativeLayout.LayoutParams lpParams;
		RelativeLayout.LayoutParams current;
        
		current = (RelativeLayout.LayoutParams) _view.getLayoutParams();
		lpParams = new RelativeLayout.LayoutParams(current.width, current.height);
		lpParams.leftMargin = current.leftMargin;
		lpParams.rightMargin = current.rightMargin;
		//lpParams.topMargin = (int) (y + (lpParams.height / 2));
		// lpParams.topMargin = (int) (y + lpParams.height);
		lpParams.topMargin = Math.abs(_layout.getHeight() - current.height);
		lpParams.bottomMargin = _layout.getHeight() + current.height;
        
		_view.setLayoutParams(lpParams);
    }
}
