package com.sweetsound.piechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Join;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.sweetsound.piechart.listener.OnPieClickListener;

public abstract class ChartImageView extends ImageView implements OnTouchListener {
    private static final String TAG = ChartImageView.class.getSimpleName();
    
    protected static final double DEFAULT_ANGLE_FOR_TEXT_SIZE = 20;
    
    private OnPieClickListener mOnPieClickListener;
    
    protected SelectedPieInfo mSelectedPieInfo;
    
    protected SHOW_TEXT_TYPE mShowTextType;
    
    protected Paint mPiePaint;
    protected Paint mPieBorderPaint;
    protected Paint mSelectedPiePaint;
    protected Paint mTextPaint;
    protected Paint mCenterCirclePaint;
    
    protected RectF mPieChartRectf;
    protected RectF mSelectedPieRectf;
    
    protected float[] mPieSizeArr;
    
    protected float mPieTotalSize = -1;
    
    protected double mRadiusForTextSize = -1;
    protected double mCenterX;
    protected double mCenterY;
    
    protected int[] mPieColorArr;
    
    protected int mSelectPiePadding;
    protected int mPaddingSize;
    protected int mDiameter;
    protected int mTextSize = -1;
    
    public enum SHOW_TEXT_TYPE {
	CENTER,
	OVERLAP,
	HIDE
    }
    
    public ChartImageView(Context context) {
	super(context);
	
	init();
    }

    public ChartImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
	
	init();
    }

    public ChartImageView(Context context, AttributeSet attrs,
	    int defStyle) {
	super(context, attrs, defStyle);
	
	init();
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
	
	// 여기서 기본 크기를 정하자.. 위치는 이정도.. 
	mRadiusForTextSize = ((mDiameter - mPaddingSize * 2) / 2) * 5/7;
	
	if (mTextSize == -1) {
	    // 기본 크기는 이정도..
	    mTextSize = (int) (mRadiusForTextSize * Math.sin((Math.toRadians(DEFAULT_ANGLE_FOR_TEXT_SIZE / 2))));
	}
	
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
    
    private void init() {
	mShowTextType = SHOW_TEXT_TYPE.HIDE;
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
	mTextPaint.setAntiAlias(true);
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
    
    /** 파이 Size를 표시 할 Text의 크기
     * @param size Text 크기(픽셀)
     */
    public void setTextSize(int size) {
	mTextSize = size;
    }
    
    /** Text를 보여줄 타입을 설정한다.
     * @param showTextType {@link SHOW_TEXT_TYPE}<br>Pie Chart에서는 CENTER값은 무시함.
     */
    public void setShowTextType(SHOW_TEXT_TYPE showTextType) {
	mShowTextType = showTextType;
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
    protected int toPercent(float angle) {
	return (int) ((angle / 360) * 100);
    }
    
    protected void drawPie(Canvas canvas, float drawPieStartAngle, float pieSize, int pieColor, boolean isSelectedPie) {
	// Pie 색 설정
	mPiePaint.setColor(pieColor);
	
	// 선택된 Pie를 표시
	if (isSelectedPie == true) {
	    mSelectedPiePaint.setStrokeWidth(mSelectPiePadding);
	    mSelectedPiePaint.setColor(pieColor);
	    mSelectedPiePaint.setAlpha(70);
	    
	    canvas.drawArc(mSelectedPieRectf, (int)drawPieStartAngle, (int)pieSize, !isSelectedPie, mSelectedPiePaint);
	    
	    mPieBorderPaint.setStrokeWidth(mPieChartRectf.width() / 40);
	} else {
	    mPieBorderPaint.setStrokeWidth(1.5f);
	}
	
	drawPie(canvas, drawPieStartAngle, pieSize);
    }
    
    protected abstract void drawPie(Canvas canvas, float drawPieStartAngle, float pieSize);
    
}
