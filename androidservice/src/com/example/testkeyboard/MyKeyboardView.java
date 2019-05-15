package com.example.testkeyboard;

import java.util.List;

import com.example.androidservice.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

public class MyKeyboardView extends KeyboardView {
	
	private Drawable mKeybgDrawable;
	private Drawable mOpKeybgDrawable;
	private Resources res;
//在initKeyboard()调用
	public MyKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initResources(context);
		// TODO Auto-generated constructor stub
	}

	public MyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initResources(context);
		// TODO Auto-generated constructor stub
	}
	
	private void initResources(Context context){
		res = context.getResources();
		
		mKeybgDrawable =res.getDrawable(R.drawable.btn_keyboard_key);
		mOpKeybgDrawable = res.getDrawable(R.drawable.btn_keyboard_opkey);
	}

	@Override
	public void onDraw(Canvas canvas) {
		List<Key> keys = getKeyboard().getKeys();
		/*
		 * save（）：把当前状态的状态进行保存，然后放入栈中
restore（）：把栈顶画布状态取出
		 * canvas.save();和canvas.restore();是两个相互匹配出现的，作用是用来保存画布的状态和取出保存的状态的。这里稍微解释一下，
  当我们对画布进行旋转，缩放，平移等操作的时候其实我们是想对特定的元素进行操作，比如图片，一个矩形等，但是当你用canvas的方法来进行这些操作的时候，其实是对整个画布进行了操作，
  那么之后在画布上的元素都会受到影响，所以我们在操作之前调用canvas.save()来保存画布当前的状态，当操作之后取出之前保存过的状态，这样就不会对其他的元素进行影响
		 */
		for (Key key : keys) {
			canvas.save();//用来保存Canvas的状态,save()方法之后的代码，可以调用Canvas的平移、放缩、旋转、裁剪等操作

			int offsety = 0;
			if (key.y == 0) {
				offsety = 1;
			}
			
			int initdrawy = key.y + offsety;
			
			Rect rect =new Rect(key.x, initdrawy, key.x + key.width, key.y
					+ key.height);	
			
			canvas.clipRect(rect);//clipRect是将当前画布裁剪为一个矩形

			int primaryCode = -1;
			if(null != key.codes && key.codes.length !=0){
				primaryCode = key.codes[0];
			}
			
			Drawable dr = null;
			if (primaryCode == -3 || key.codes[0] == -5) {
				dr = mOpKeybgDrawable;
			} else if(-1 != primaryCode){
				dr = mKeybgDrawable;
			}

			if(null != dr){
				int[] state = key.getCurrentDrawableState();
	
				dr.setState(state);
				dr.setBounds(rect);
				dr.draw(canvas);
			}

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextAlign(Paint.Align.CENTER);
			paint.setTextSize(50);
			paint.setColor(res.getColor(R.color.black));

			if (key.label != null) {
				canvas.drawText(
						key.label.toString(),
						key.x + (key.width / 2),
						initdrawy + (key.height + paint.getTextSize() - paint.descent()) / 2,
						paint);
			} else if (key.icon != null) {
				int intriWidth = key.icon.getIntrinsicWidth();
				int intriHeight = key.icon.getIntrinsicHeight();
				
				final int drawableX = key.x + (key.width - intriWidth) / 2;
				final int drawableY = initdrawy +(key.height - intriHeight) / 2;
				
				key.icon.setBounds(
						drawableX,drawableY,drawableX + intriWidth,
						drawableY + intriHeight);
				
				key.icon.draw(canvas);
			}

			canvas.restore();
		}
	}

}
