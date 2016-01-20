package com.sweetsound.piechart;

import android.util.Log;

public class TextPositionInfo {
    private static final String TAG = TextPositionInfo.class.getSimpleName();
    
    public double mX;
    public double mY;
    
    public void setX(double x, double centerX) {
	if (x < 0) {
	    mX = moveSymmetry(centerX, Math.abs(x));
	} else {
	    mX = centerX + Math.abs(x);
	}
    }
    
    public void setY(double y, double centerY) {
	if (y < 0) {
	    mY = moveSymmetry(centerY, Math.abs(y));
	} else {
	    mY = centerY + Math.abs(y);
	}
    }
    
    public double getX() {
	return mX;
    }
    
    public double getY() {
	return mY;
    }
    
    private double moveSymmetry(double centerCoordinate, double targetCoordinate) {
	return Math.abs((centerCoordinate + targetCoordinate) - (targetCoordinate * 2));
    }
}
