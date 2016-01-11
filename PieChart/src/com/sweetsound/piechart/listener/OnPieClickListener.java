package com.sweetsound.piechart.listener;

/** 파이를 클릭 했을 때 호출되는 Listener
 * @author Lee Jeongseok
 *
 */
public interface OnPieClickListener {
    /** 파이를 클릭 했을 때 호출되며 선택된 파이의 정보를 받는다.
     * @param pieIndex 선택된 파이의 index
     * @param isSelected 선택되어 있는지 여부<br>true - 선택됨<br>false - 선택이 해제됨
     */
    public void onPieClick(int pieIndex, boolean isSelected);
}
