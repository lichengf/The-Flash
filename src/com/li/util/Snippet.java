package com.li.util;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;

public class Snippet {
	/**
	 * ���������ť���е���ɫ����
	 */
	public final static float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -80, 0,
			1, 0, 0, -80, 0, 0, 1, 0, -80, 0, 0, 0, 1, 0 };

	/**
	 * ��ť�ָ�ԭ״����ɫ����
	 */
	public final static float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	/**
	 * ��ť��������Ч��
	 */
	public final static OnTouchListener buttonOnTouchListener = new OnTouchListener() {

		int x, y;

		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				x = (int) event.getX();
				y = (int) event.getY();

				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (Math.abs(event.getX() - x) > 5
						&& Math.abs(event.getY() - y) > 5) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				}
			}
			return false;
		}
	};

	/**
	 * ��ť��������Ч��
	 */
	public final static OnFocusChangeListener buttonOnFocusListener = new OnFocusChangeListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus == true) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				
			}
		}

	};

	/**
	 * ����ͼƬ��ť��ȡ����ı�״̬
	 * 
	 * @param inImageButton
	 */
	public final static void setButtonFocusChanged(View inView) {
		inView.setOnTouchListener(buttonOnTouchListener);
		inView.setOnFocusChangeListener(buttonOnFocusListener);
	}
}
