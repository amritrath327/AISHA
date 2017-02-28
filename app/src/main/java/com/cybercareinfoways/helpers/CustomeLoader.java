package com.cybercareinfoways.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.cybercareinfoways.aisha.R;


/**
 * Created by Tanmay on 2/27/2017.
 */

public class CustomeLoader extends View implements InvalidateListener {
  private LoaderView loaderView;

  public CustomeLoader(Context context) {
    super(context);
    initialize(context, null, 0);
  }

  public CustomeLoader(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs, 0);
  }

  public CustomeLoader(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs, defStyleAttr);
  }

  private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomeLoader);

    String loaderType = typedArray.getString(R.styleable.CustomeLoader_mk_type);
    if (loaderType == null) loaderType = "ClassicSpinner";
    loaderView = LoaderGenerator.generateLoaderView(loaderType);
    loaderView.setColor(typedArray.getColor(R.styleable.CustomeLoader_mk_color, Color.parseColor("#ffffff")));
    loaderView.setInvalidateListener(this);

    typedArray.recycle();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int measuredWidth = resolveSize(loaderView.getDesiredWidth(), widthMeasureSpec);
    final int measuredHeight = resolveSize(loaderView.getDesiredHeight(), heightMeasureSpec);

    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    loaderView.setSize(getWidth(), getHeight());
    loaderView.initializeObjects();
    loaderView.setUpAnimation();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    loaderView.draw(canvas);
  }

  @Override public void reDraw() {
    invalidate();
  }
}
