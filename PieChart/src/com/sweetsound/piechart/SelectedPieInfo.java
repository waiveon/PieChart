package com.sweetsound.piechart;

import android.graphics.Color;

/** 선택된 파이의 정보를 저장하는 class
 * @author Lee Jeongseok
 *
 */
class SelectedPieInfo {
    private static final int PIE_BORDER_DEFAULT_COLOR = Color.TRANSPARENT;
    
    private float mStartAngle;
    private float mPieSize;
    
    private int mPieColor;
    private int mSelectedPieIndex = -1;
    
    /** 시작 각도를 설정
     * @param angle 각도
     */
    public void setStartAngle(float angle) {
	mStartAngle = angle;
    }
    
    /** 파이의 크기를 설정
     * @param size 크기
     */
    public void setPieSize(float size) {
	mPieSize = size;
    }
    
    /** 파이의 색을 설정
     * @param color 색
     */
    public void setPieColor(int color) {
	mPieColor = color;
    }
    
    /** 선택된 파이의 index를 설정
     * @param index index
     */
    public void setSelectedPieIndex(int index) {
	mSelectedPieIndex = index;
    }
    
    /** 파이를 그리기 위한 시작 각도를 반환한다.
     * @return 각도 {@link float}
     */
    public float getStartAngle() {
	return mStartAngle;
    }
    
    /** 파이의 크기를 반환한다.
     * @return 크기 {@link float}
     */
    public float getPieSize() {
	return mPieSize;
    }
    
    /** 파이의 색을 반환한다.
     * @return 색 {@link int}
     */
    public int getPieColor() {
	return mPieColor;
    }
    
    /** 파이의 테두리 색을 반환한다.
     * @return 투명한 색으로 고정임
     */
    public int getPieBorderColor() {
	return PIE_BORDER_DEFAULT_COLOR;
    }
    
    /** 선택된 파이의 index를 반환한다.
     * @return index {@link int}
     */
    public int getSelectedPieIndex() {
	return mSelectedPieIndex;
    }
}
