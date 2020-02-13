package com.kidscademy.cars.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kidscademy.cars.R;

public class NumericKeyboardView extends GridLayout implements OnClickListener
{
  private static final List<Character> keys = new ArrayList<Character>();
  static {
    String s = "123456789B0D";
    for(int i = 0; i < s.length(); ++i) {
      keys.add(s.charAt(i));
    }
  }

  private int attachedFieldId;
  private TextView attachedField;

  public NumericKeyboardView(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public NumericKeyboardView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);

    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumericKeyboardView, 0, 0);
    attachedFieldId = a.getResourceId(R.styleable.NumericKeyboardView_attachedFieldId, 0);
    a.recycle();
  }

  @Override
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    update();
    for(int i = 0; i < getChildCount(); ++i) {
      getChildAt(i).setOnClickListener(this);
    }
  }

  public void setAttachedFieldId(int attachedFieldId)
  {
    this.attachedFieldId = attachedFieldId;
    update();
  }

  private void update()
  {
    if(attachedFieldId != 0) {
      attachedField = (TextView)getRootView().findViewById(attachedFieldId);
    }
  }

  @Override
  public void onClick(View view)
  {
    char c = keys.get(indexOfChild(view));
    switch(c) {
    case 'B':
      attachedField.setText(null);
      break;
      
    case 'D':

    default:
      attachedField.setText(Character.toString(c));
    }
  }

  public void attachField(TextView attachedField)
  {
    this.attachedField = attachedField;
  }
}
