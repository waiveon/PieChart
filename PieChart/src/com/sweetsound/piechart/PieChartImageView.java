package com.sweetsound.piechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View.OnTouchListener;

public class PieChartImageView extends ChartImageView implements OnTouchListener {
    private static final String TAG = PieChartImageView.class.getSimpleName();

    public PieChartImageView(Context context) {
	super(context);
    }

    public PieChartImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    public PieChartImageView(Context context, AttributeSet attrs,
	    int defStyleAttr) {
	super(context, attrs, defStyleAttr);
    }

    /** 파이를 그린다.
     * @param canvas 파이를 그릴 Canvas
     * @param drawPieStartAngle 파이 시작 각도
     * @param pieSize 파이 크기
     * @param pieColor 파이 색
     * @param isSelectedPie 해당 파이의 선택 여부
     */
    protected void drawPie(Canvas canvas, float drawPieStartAngle, float pieSize) {
	// Pie의 검은색 테두리
	canvas.drawArc(mPieChartRectf, drawPieStartAngle, pieSize, true, mPieBorderPaint);
	// Pie를 그림
	canvas.drawArc(mPieChartRectf, drawPieStartAngle, pieSize, true, mPiePaint);
	
	// Text를 보여줄 방식에 따라 Text를 그린다.
	if (mShowTextType != SHOW_TEXT_TYPE.HIDE) {
	    drawPieSize(canvas, mRadiusForTextSize, (drawPieStartAngle + (pieSize / 2)), pieSize);
	}
    }
    
    private void drawPieSize(Canvas canvas, double radius, double angle, float pieSize) {
	String pieSizeStr = toPercent(pieSize) + " %";
	TextPositionInfo textPositionInfo = getLocation(radius, angle);

	// 현재 상태 저장
	canvas.save();
	
	// Text 크기 설정
	mTextPaint.setTextSize(getTextSize(radius, pieSize));
	
	// Text를 그릴 위치 선청을 위한 Text의 크기 값 가져 오기
	Rect rect = new Rect();
	mTextPaint.getTextBounds(pieSizeStr, 0, pieSizeStr.length(), rect);
	
	if (angle > 90 && angle < 270) {
	    angle -= 180;
	}

	// 파이 위치에 따른 Text도 회전
	canvas.rotate((float) angle, 
		(float) textPositionInfo.getX(), 
		(float) textPositionInfo.getY());
	
	// Text 그리기
	canvas.drawText(pieSizeStr, 
		(float) textPositionInfo.getX() - rect.centerX(), 
		(float) textPositionInfo.getY() - rect.centerY(),
		mTextPaint);
	
	// 각도 돌린거 원복
	canvas.restore();
    }
    
    /** Overlab일 떄 텍스트를 보여주기 위한 위치를 계산하여 반환한다.
     * @param radius 반지름
     * @param angle 각도
     * @return 텍스트를 보여줄 위치
     */
    private TextPositionInfo getLocation(double radius, double angle) {
	// (r * cos A, r * sin A)
	TextPositionInfo textPositionInfo = new TextPositionInfo();
	textPositionInfo.setX(radius * Math.cos(Math.toRadians(angle)), mCenterX);
	textPositionInfo.setY(radius * Math.sin(Math.toRadians(angle)), mCenterY);
	
	return textPositionInfo;
    }
    
    /** 텍스트의 크기를 가져온다.
     * @param hypotenuse 빗면
     * @param angle 각도
     * @return 밑면의 길이
     */
    private int getTextSize(double hypotenuse, double angle) {
	int lowerBaseLine = -1;
	
	if (angle > 170) {
	    lowerBaseLine = mTextSize;
	} else {
	    // 삼각함수를 이용하여 크기를 구한다.
	    // a = b * sin(A/2)
	    // 밑변의 1/2 크기정도면 될 것 같음..
	    double baseLine = hypotenuse * Math.sin((Math.toRadians(angle / 2)));
	    
	    if (baseLine > mTextSize) {
		lowerBaseLine = mTextSize;
	    } else {
		lowerBaseLine = (int) baseLine;
	    }
	}
	
	return lowerBaseLine;
    }
}
