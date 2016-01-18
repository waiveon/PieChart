package com.sweetsound.piechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.sweetsound.piechart.listener.OnPieClickListener;

public class PieChartImageView extends ImageView implements OnTouchListener {
    private static final String TAG = PieChartImageView.class.getSimpleName();
    
    private int mPaddingSize;
    private int mSelectPiePadding;
    
    private Paint mPiePaint;
    private Paint mPieBorderPaint;
    private Paint mSelectedPiePaint;
    private Paint mTextPaint;
    private Paint mCenterCirclePaint;
    
    private RectF mPieChartRectf;
    private RectF mSelectedPieRectf;
    
    private OnPieClickListener mOnPieClickListener;
    
    private SelectedPieInfo mSelectedPieInfo;
    
    private float[] mPieSizeArr;
    
    private float mPieTotalSize = -1;
    
    private int[] mPieColorArr;
    
    private double mCenterX;
    private double mCenterY;
    
    private int mDiameter;
    
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

    @Override
    public void draw(Canvas canvas) {
	float startAngle = 0;
	float pieSize = 0;

	int left 	= -1;
	int top 	= -1;
	int right 	= -1;
	int bottom 	= -1;
	int margin	= -1;
	
	// TODO 화면 전환 시에만 다시 설정하는 것으로 변경 해보자
	Rect rect = new Rect();
	getDrawingRect(rect);
	
	mCenterX = rect.centerX();
	mCenterY = rect.centerY();
	
	// 높이와 너비 중 작은 것을 기준으로 원을 그린다.
	if (getWidth() > getHeight()) {
	    // 높이가 기준
	    top = getTop();
	    bottom = getBottom();
	    
	    margin = (getWidth() - getHeight()) / 2;
	    
	    left = getLeft() + margin;
	    right = getRight() - margin;
	    
	    mDiameter = getHeight();
	} else {
	    // 너비가 기준
	    left = getLeft();
	    right = getRight();
	    
	    margin = (getHeight() - getWidth()) / 2;
	    
	    top = getTop() + margin;
	    bottom = getBottom() - margin;
	    
	    mDiameter = getWidth();
	}
	
	// 그냥 개인적으로 적당하다고 생각한 크기.. -_-;;
	mPaddingSize = (int) (((double)1 / (double)20) * mDiameter);
	
	// 파이 차트의 크기를 설정
	mPieChartRectf = new RectF(
		left + mPaddingSize,
		top + mPaddingSize,
		right - mPaddingSize,
		bottom - mPaddingSize
		);
	
	// 그냥 개인적으로 적당하다고 생각한 크기.. -_-;;
	mSelectPiePadding = mPaddingSize * 3 / 5;
	
	// 선택된 파이의 크기를 설정
	mSelectedPieRectf = new RectF(
		left + mSelectPiePadding,
		top + mSelectPiePadding,
		right - mSelectPiePadding,
		bottom - mSelectPiePadding
		);
	
	// 최초로 그리는 것인지 체크
	if (mPieTotalSize < 0) {
	    // 각 파이의 크기를 더해 총 크기를 구한다.
	    for (float datum : mPieSizeArr) {
		mPieTotalSize += datum;
	    }
	}
	
	// 파이 크기별로 파이를 그린다.
	for (int i = 0; i <  mPieSizeArr.length; i++) {
	    pieSize = mPieSizeArr[i];
	    
	    if (pieSize == 0) {
		continue;
	    }
	    
	    // 선택된 파이가 있을 경우 따로 처리한다.
	    if (i != mSelectedPieInfo.getSelectedPieIndex()) {
		drawPie(canvas, 
			startAngle, 
			pieSize, 
			mPieColorArr[i], 
			false);
	    } else {
		mSelectedPieInfo.setPieColor(mPieColorArr[i]);
		mSelectedPieInfo.setPieSize(pieSize);
		mSelectedPieInfo.setStartAngle(startAngle);
	    }
	    
	    startAngle = startAngle + pieSize;
	    
	    // text를 그린다.
	}
	
	// 선택된 파이를 그린다.
	if (mSelectedPieInfo.getSelectedPieIndex() > -1) {
	    drawPie(canvas, 
		    mSelectedPieInfo.getStartAngle(), 
		    mSelectedPieInfo.getPieSize(), 
		    mSelectedPieInfo.getPieColor(), 
		    true);
	}
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
	if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    float startAngle = 0;
	    
	    // 클릭한 좌표를 구한다.
	    float xPos = event.getX() - mPieChartRectf.centerX();
	    float yPos = event.getY() - mPieChartRectf.centerY();
	    
	    double clickAngle = Math.atan2(yPos,xPos) * 180 / Math.PI;
	    
	    if (clickAngle < 0) {
		clickAngle = 360 + clickAngle;
	    }
	    
	    // 각 파이 중 선택된 것이 있는지 확인한다.
	    for (int i = 0; i < mPieSizeArr.length; i++) {
		float datum = mPieSizeArr[i];
		
		if (datum == 0) {
		    continue;
		}
		
		float endAngle = mPieTotalSize == 0 ? 0 : 360 * datum / (float) mPieTotalSize;
		float newStartAngle = startAngle + endAngle;
		
		// 선택된 파이가 있을 경우 해당 파이를 그리기 위한 정보를 저장하고 ClickListener를 호출한다.
		if(mSelectedPieRectf.contains(event.getX(),event.getY())
			&& clickAngle > startAngle && clickAngle < newStartAngle) {
		    boolean isCancel = false;
		    
		    // 선택되었던 파이를 다시 선택 했을 경우 해당 파이의 선택을 해제 한다.
		    if (mSelectedPieInfo.getSelectedPieIndex() > -1) {
			isCancel = mSelectedPieInfo.getSelectedPieIndex() == i;
			
			if (isCancel == true) {
			    mSelectedPieInfo.setSelectedPieIndex(-1);
			} else {
			    mSelectedPieInfo.setSelectedPieIndex(i);
			}
		    } else {
			mSelectedPieInfo.setSelectedPieIndex(i);
		    }
		    
		    if (mOnPieClickListener != null) {
			mOnPieClickListener.onPieClick(i, !isCancel);
		    }
		    
		    break;
		}
		
		startAngle = newStartAngle;
	    }
	    
	    invalidate();
	}
	
	return false;
    }
    
    /** 기본적인 파이의 정보를 저장
     * @param pieColors 각 파이의 색갈들
     * @param pieSizes 각 파이의 크기
     * @param isSizePercent 각 파이의 크기를 퍼센트로 설정 했는지 여부<br>true - 100%를 기준으로 파이의 크기를 정함<br>false - 360도를 기준으로 파이의 크기를 정함
     */
    public void setPieInfo(String[] pieColors, float[] pieSizes, boolean isSizePercent) {
	String pieColor = null;
	
	// String으로 받은 색을 int로 변환 하여 저장한다.
	int[] pieColorsIntArr = new int[pieColors.length];
	
	for (int i = 0; i < pieColors.length; i++) {
	    pieColor = pieColors[i];
	    
	    if (TextUtils.isEmpty(pieColor) == false) {
		pieColorsIntArr[i] = Color.parseColor(pieColor);
	    } else {
		pieColorsIntArr[i] = Color.TRANSPARENT;
	    }
	}
	
	setPieInfo(pieColorsIntArr, pieSizes, isSizePercent);
    }
    
    /** 기본적인 파이의 정보를 저장
     * @param pieColors 각 파이의 색갈들
     * @param pieSizes 각 파이의 크기
     * @param isSizePercent 각 파이의 크기를 퍼센트로 설정 했는지 여부<br>true - 100%를 기준으로 파이의 크기를 정함<br>false - 360도를 기준으로 파이의 크기를 정함
     */
    public void setPieInfo(int[] pieColors, float[] pieSizes, boolean isSizePercent) {
	setOnTouchListener(this);
	
	mSelectedPieInfo = new SelectedPieInfo();
	
	if (isSizePercent == true) {
	    mPieSizeArr = toAngle(pieSizes);
	} else {
	    mPieSizeArr = pieSizes;
	}
	
	mPieColorArr = pieColors;
	
	// 파이를 그리기 위한 기본 정보를 설정
	mPiePaint = new Paint();
	mPiePaint.setAntiAlias(true);
	mPiePaint.setStyle(Paint.Style.FILL);
	
	// 테두리를 그리기 위한 기본 정보를 설정
	mPieBorderPaint = new Paint();
	mPieBorderPaint.setAntiAlias(true);
	mPieBorderPaint.setStyle(Paint.Style.STROKE);
	mPieBorderPaint.setStrokeJoin(Join.ROUND);
	mPieBorderPaint.setColor(Color.BLACK);
	mPieBorderPaint.setStrokeWidth(1.5f);
	// x, y 는 구성될 원형의 중점 좌표이며, radius 는 그라데이션이 구성될 반지름의 크기를 의미 합니다.
//	mPieBorderPaint.setShader(new RadialGradient(80,80,70, Color.BLUE, Color.WHITE, TileMode.CLAMP));
	// 선의 끝부분을 둥글게 처리함
//	mPieBorderPaint.setStrokeCap(Cap.ROUND);
	
	// 선택된 Pie를 표시
	mSelectedPiePaint = new Paint();
	mSelectedPiePaint.setAntiAlias(true);
	mSelectedPiePaint.setStyle(Paint.Style.STROKE);
	mSelectedPiePaint.setStrokeJoin(Join.ROUND);
	
	// 센터에 선택된 파이의 크기를 표시 하기 위한 원
	mCenterCirclePaint = new Paint();
	mCenterCirclePaint.setAntiAlias(true);
	mCenterCirclePaint.setStyle(Paint.Style.FILL);
	mCenterCirclePaint.setColor(Color.WHITE);
	
	mTextPaint = new Paint();
	mTextPaint.setStyle(Paint.Style.FILL);
	mTextPaint.setColor(Color.BLACK);
    }
    
    /** 파이를 클릭 했을 때 호출되는 callback 리스너를 설정한다.
     * @param onPieClickListener 파이를 선택 했을 때 호출되는 리스너
     */
    public void setOnPieClickListener(OnPieClickListener onPieClickListener) {
	mOnPieClickListener = onPieClickListener;
    }
    
    /** 특정 파이를 선택한다.
     * @param index 해당 파이의 index
     */
    public void setSelectPid(int index) {
	mSelectedPieInfo.setSelectedPieIndex(index);
	
	invalidate();
    }
    
    /** 파이를 그린다.
     * @param canvas 파이를 그릴 Canvas
     * @param drawPieStartAngle 파이 시작 각도
     * @param pieSize 파이 크기
     * @param pieColor 파이 색
     * @param isSelectedPie 해당 파이의 선택 여부
     */
    private void drawPie(Canvas canvas, float drawPieStartAngle, float pieSize, int pieColor, boolean isSelectedPie) {
	// Draw Pie
	mPiePaint.setColor(pieColor);
	
	if (isSelectedPie == true) {
	    mSelectedPiePaint.setStrokeWidth(mSelectPiePadding);
	    mSelectedPiePaint.setColor(pieColor);
	    mSelectedPiePaint.setAlpha(70);
	    
	    canvas.drawArc(mSelectedPieRectf, (int)drawPieStartAngle, (int)pieSize, !isSelectedPie, mSelectedPiePaint);
	}
	
	canvas.drawArc(mPieChartRectf, (int)drawPieStartAngle, (int)pieSize, true, mPieBorderPaint);
	canvas.drawArc(mPieChartRectf, (int)drawPieStartAngle, (int)pieSize, true, mPiePaint);
	
	drawPieSize(canvas, ((mDiameter - mPaddingSize * 2) / 4), (drawPieStartAngle + (pieSize / 2)), toPercent(pieSize));
	
//	double a = (Math.pow(b, 2) + Math.pow(c, 2) - 2 * (b * c) * Math.cos(angle)) / 2
		
	// Draw Text
//	canvas.translate(mCenterX + , mCenterY + );
	
	// draw bounding rect before rotating text
//	paint.setStyle(Paint.Style.FILL);
//	// draw unrotated text
//	canvas.drawText("!Rotated", 0, 0, paint);
//	paint.setStyle(Paint.Style.STROKE);
//	canvas.drawRect(rect, paint);
//	// undo the translate
//	canvas.translate(-x, -y);
//	
//	// rotate the canvas on center of the text to draw
//	canvas.rotate(-45, x + rect.exactCenterX(),
//		y + rect.exactCenterY());
//	// draw the rotated text
//	paint.setStyle(Paint.Style.FILL);
//	canvas.drawText(str2rotate, x, y, paint);
//	
//	//undo the rotate
//	canvas.restore();
    }
    
    private void drawPieSize(Canvas canvas, double radius, double angle, float pieSize) {
	String pieSizeStr = (int) pieSize + " %";
	TextPositionInfo textPositionInfo = getLocation(radius, angle);

	canvas.save();
	
	mTextPaint.setTextSize(30);
	
	Rect rect = new Rect();
	mTextPaint.getTextBounds(pieSizeStr, 0, pieSizeStr.length(), rect);
	
	Log.e(TAG, "LJS== pieSizeStr : " + pieSizeStr);
	Log.e(TAG, "LJS== angle : " + angle);
	Log.e(TAG, "LJS== textPositionInfo.x : " + textPositionInfo.getX());
	Log.e(TAG, "LJS== textPositionInfo.y : " + textPositionInfo.getY());
	Log.e(TAG, "LJS== rect.exactCenterX() : " + rect.exactCenterX());
	Log.e(TAG, "LJS== rect.exactCenterY() : " + rect.exactCenterY());
	Log.e(TAG, "LJS== rect.left : " + rect.left);
	Log.e(TAG, "LJS== rect.right : " + rect.right);
	Log.e(TAG, "LJS== rect.top : " + rect.top);
	Log.e(TAG, "LJS== rect.bottom : " + rect.bottom);
	
	canvas.translate((float) textPositionInfo.getX() + rect.centerX(), (float) textPositionInfo.getY() + rect.centerY());
	canvas.drawRect(rect, mTextPaint);
	canvas.translate((float) -textPositionInfo.getX() + rect.centerX(), (float) -textPositionInfo.getY() + rect.centerY());
	
	// 이거 다시 체크
	canvas.rotate((float) angle, (float) textPositionInfo.getX() + rect.centerX(), (float) textPositionInfo.getY() + rect.centerY());
	canvas.drawText(pieSizeStr, 
		(float) textPositionInfo.getX(), 
		(float) textPositionInfo.getY(),
		mTextPaint);
	// 각도 돌린거 원복
	canvas.restore();
    }
    
    /** 퍼센트로 받은 값을 각도로 변경한다.
     * @param inputPercents 퍼센트 입력값
     * @return 각도로 변경한 값
     */
    private float[] toAngle(float[] inputPercents) {
	float[] angles = new float[inputPercents.length];
	
	for (int i = 0; i < inputPercents.length; i++) {
	    angles[i] = (inputPercents[i] / 100) * 360;
	}
	
	return angles;
    }
    
    /** 각도를 퍼센트로 바꾼다.
     * @param angle 각도
     * @return 각도를 퍼센트로 변경한 값
     */
    private float toPercent(float angle) {
	return (angle / 360) * 100;
    }
    
    private TextPositionInfo getLocation(double radius, double angle) {
	// (r * cos A, r * sin A)
	
	Log.e(TAG, "LJS== mCenterX : " + mCenterX);
	Log.e(TAG, "LJS== mCenterY : " + mCenterY);
	Log.e(TAG, "LJS== x : " + radius * Math.cos(Math.toRadians(angle)));
	Log.e(TAG, "LJS== y : " + radius * Math.sin(Math.toRadians(angle)));
	
	TextPositionInfo textPositionInfo = new TextPositionInfo();
	textPositionInfo.setX(radius * Math.cos(Math.toRadians(angle)), mCenterX);
	textPositionInfo.setY(radius * Math.sin(Math.toRadians(angle)), mCenterY);
	
	return textPositionInfo;
    }
    
//    private void getLowerBase(double b, double c, double angle) {
//	// a^2 = b^2 + c^2 - 2bc * cosA
//	;
//	
//	double t = Math.sqrt(Math.pow(b, 2) - Math.pow((a / 2) , 2));
//	
//	// a / 2, t
//    }
}
