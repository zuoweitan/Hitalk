package com.vivifram.second.hitalk.ui.recycleview.address;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.LogUtil;
import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.DisplayUtil;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;


public class DividerDecoration extends RecyclerView.ItemDecoration {

  private static final int[] ATTRS = new int[]{
      android.R.attr.listDivider
  };

  public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

  public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

  private Divider mDivider;
  public DividerDecoration(Context context) {
    /*final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    a.recycle();*/
    mDivider = new Divider(context.getResources().getColor(R.color.color_eeeeee),
            context.getResources().getColor(R.color.colorWhite));
  }

  private int getOrientation(RecyclerView parent) {
    LinearLayoutManager layoutManager;
    try {
      layoutManager = (LinearLayoutManager) parent.getLayoutManager();
    } catch (ClassCastException e) {
      throw new IllegalStateException("DividerDecoration can only be used with a " +
          "LinearLayoutManager.", e);
    }
    return layoutManager.getOrientation();
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);

    if (getOrientation(parent) == VERTICAL_LIST) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  public void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();
    final int recyclerViewTop = parent.getPaddingTop();
    final int recyclerViewBottom = parent.getHeight() - parent.getPaddingBottom();
    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      final int top = Math.max(recyclerViewTop, child.getBottom() + params.bottomMargin);
      final int bottom = Math.min(recyclerViewBottom, top + mDivider.getIntrinsicHeight());
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  public void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();
    final int recyclerViewLeft = parent.getPaddingLeft();
    final int recyclerViewRight = parent.getWidth() - parent.getPaddingRight();
    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      final int left = Math.max(recyclerViewLeft, child.getRight() + params.rightMargin);
      final int right = Math.min(recyclerViewRight, left + mDivider.getIntrinsicWidth());
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    if (getOrientation(parent) == VERTICAL_LIST) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }

  class Divider extends Drawable{

    Paint paint;
    int lineColor;
    int lineBg;
    boolean vertical;
    int offset;
    public Divider(int lineColor,int lineBg){
      paint = new Paint();
      paint.setAntiAlias(true);
      paint.setStrokeWidth(DisplayUtil.dip2px(0.5f));

      offset = DisplayUtil.dip2px(10);
      this.lineColor = lineColor;
      this.lineBg = lineBg;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
      super.setBounds(left, top, right, bottom);
      vertical = right - left > bottom - top;
    }

    @Override
    public void draw(Canvas canvas) {
      Rect bounds = getBounds();
      //paint.setColor(lineBg);
      //canvas.drawLine(bounds.left,bounds.top,bounds.right,bounds.bottom,paint);

      if (vertical){
        paint.setColor(lineBg);
        canvas.drawLine(bounds.left ,bounds.top ,bounds.left + offset ,bounds.bottom,paint);
        paint.setColor(lineColor);
        canvas.drawLine(bounds.left + offset,bounds.top,bounds.right,bounds.bottom,paint);
      }else {
        paint.setColor(lineColor);
        canvas.drawLine(bounds.left,bounds.top,bounds.right,bounds.bottom,paint);
      }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
      return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
      return DisplayUtil.dip2px(0.5f);
    }

    @Override
    public int getIntrinsicWidth() {
      return DisplayUtil.dip2px(0.5f);
    }
  }
}