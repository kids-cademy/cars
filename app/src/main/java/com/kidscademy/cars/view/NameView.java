package com.kidscademy.cars.view;

import java.util.Locale;

import js.lang.BugError;
import js.util.Player;
import js.util.Utils;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.cars.App;
import com.kidscademy.cars.R;

public class NameView extends LinearLayout implements OnClickListener
{
  public static interface Listener
  {
    void onNameChar(char c);
  }

  private Player player;
  private Listener listener;
  /** Upper case expected name with underscore (_) separator replaced by space. */
  private String expectedName;
  private boolean disabled;

  public NameView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    for(int i = 0; i < getChildCount(); ++i) {
      getChildAt(i).setOnClickListener(this);
    }
  }

  public void init(String expectedName)
  {
    this.expectedName = expectedName.toUpperCase(Locale.getDefault()).replace('_', ' ');

    // ensure capacity
    LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    for(int i = getChildCount(); i < expectedName.length(); ++i) {
      View view = inflater.inflate(R.layout.compo_letter, this, false);
      addView(view);
      view.setOnClickListener(this);
    }

    int i = 0;

    for(; i < expectedName.length(); ++i) {
      TextView view = (TextView)getChildAt(i);
      view.setText(getPlaceholderChar(i));
      view.setVisibility(View.VISIBLE);
    }

    for(; i < getChildCount(); ++i) {
      getChildAt(i).setVisibility(View.GONE);
    }
  }

  public void disable()
  {
    disabled = true;
  }

  public void enable()
  {
    disabled = false;
  }

  public String getInput()
  {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < expectedName.length(); ++i) {
      builder.append(((TextView)getChildAt(i)).getText());
    }
    return builder.toString();
  }

  private String getPlaceholderChar(int index)
  {
    return expectedName.charAt(index) == ' ' ? " " : "_";
  }

  public void setPlayer(Player player)
  {
    this.player = player;
  }

  public void setListener(Listener listener)
  {
    this.listener = listener;
  }

  public void putChar(char c)
  {
    // underscore is used for missing character
    // scan for first underscore, i.e. missing character, and put given character there

    for(int i = 0; i < expectedName.length(); ++i) {
      TextView view = (TextView)getChildAt(i);
      if(view.getText().equals("_")) {
        view.setText(Character.toString(c));
        return;
      }
    }
    throw new BugError("Attempt to put character after name complete.");
  }

  public boolean hasAllCharsFilled()
  {
    for(int i = 0; i < expectedName.length(); ++i) {
      if(((TextView)getChildAt(i)).getText().charAt(0) == '_') {
        return false;
      }
    }
    return true;
  }

  public String getValue()
  {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < expectedName.length(); ++i) {
      builder.append(((TextView)getChildAt(i)).getText());
    }
    return builder.toString().replace(' ', '_').toLowerCase(Locale.getDefault());
  }

  @Override
  public void onClick(View view)
  {
    if(disabled) {
      return;
    }

    char c = ((TextView)view).getText().charAt(0);
    if(c == ' ' || c == '_') {
      return;
    }

    ((TextView)view).setText("_");
    listener.onNameChar(c);

    player.play("fx/click.mp3");
    if(App.prefs().isKeyVibrator()) {
      Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(200);
    }
  }

  /**
   * Get the index of first missing char, that is underscore, but skipping spaces.
   * 
   * @return
   */
  public int getFirstMissingCharIndex()
  {
    int firstMissingCharIndex = 0;
    for(int i = 0; i < expectedName.length(); ++i) {
      TextView textView = (TextView)getChildAt(i);
      if(textView.getText().charAt(0) == '_') {
        return firstMissingCharIndex;
      }
      if(textView.getText().charAt(0) != ' ') {
        ++firstMissingCharIndex;
      }
    }
    return -1;
  }

  /**
   * Compare given name with internally stored expected name and mark letters accordingly.
   * 
   * @param name
   */
  public void verify(String name)
  {
    for(int i = 0; i < name.length(); ++i) {
      assert i < getChildCount();
      Character c = name.charAt(i);
      if(c == '_') {
        continue;
      }
      TextView textView = (TextView)getChildAt(i);
      textView.setText(c.toString());
      if(!c.equals(expectedName.charAt(i))) {
        textView.setTextColor(Utils.getThemeColor(getContext(), R.attr.textColorError));
      }
    }
  }
}
