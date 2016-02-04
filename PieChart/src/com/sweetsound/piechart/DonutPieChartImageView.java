package com.sweetsound.piechart;

import com.sweetsound.piechart.ChartImageView.SHOW_TEXT_TYPE;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class DonutPieChartImageView extends ChartImageView {

    public DonutPieChartImageView(Context context) {
	super(context);
    }

    public DonutPieChartImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    public DonutPieChartImageView(Context context, AttributeSet attrs,
	    int defStyle) {
	super(context, attrs, defStyle);
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
	canvas.drawArc(mPieChartRectf, (int)drawPieStartAngle, (int)pieSize, true, mPieBorderPaint);
	// Pie를 그림
	canvas.drawArc(mPieChartRectf, (int)drawPieStartAngle, (int)pieSize, true, mPiePaint);
	
	// Text를 보여줄 방식에 따라 Text를 그린다.
	if (mShowTextType != SHOW_TEXT_TYPE.HIDE) {
//	    drawPieSize(canvas, mRadiusForTextSize, (drawPieStartAngle + (pieSize / 2)), pieSize);
	}
    }
}
