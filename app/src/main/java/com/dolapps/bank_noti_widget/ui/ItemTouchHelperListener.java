package com.dolapps.bank_noti_widget.ui;

public interface ItemTouchHelperListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemRemove(int position);
}
//[출처] Android RecyclerView에 대하여 - 동적인 RecyclerView 만들기|작성자 초코라떼
