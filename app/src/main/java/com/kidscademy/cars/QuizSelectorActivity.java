package com.kidscademy.cars;

import js.lang.BugError;
import js.log.Log;
import js.log.LogFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.Balance;
import com.kidscademy.cars.model.Level;
import com.kidscademy.cars.model.PlayContext;

public class QuizSelectorActivity extends AppActivityBase implements OnClickListener
{
  private static final Log log = LogFactory.getLog(QuizSelectorActivity.class);

  public static void start(Activity activity)
  {
    log.trace("start(Activity)");
    Intent intent = new Intent(activity, QuizSelectorActivity.class);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quiz_selector);
    
    setText(R.id.quiz_selector_brand_factor, factor(PlayContext.BRAND_QUIZ));
    if(Level.areAllLevelsComplete(PlayContext.BRAND_QUIZ)) {
      setText(R.id.quiz_selector_brand_level, "COMPLETED");
    }
    else {
      setText(R.id.quiz_selector_brand_level, level(PlayContext.BRAND_QUIZ));
      findViewById(R.id.quiz_selector_brand).setOnClickListener(this);
    }

    setText(R.id.quiz_selector_country_factor, factor(PlayContext.COUNTRY_QUIZ));
    if(Level.areAllLevelsComplete(PlayContext.COUNTRY_QUIZ)) {
      setText(R.id.quiz_selector_country_level, "COMPLETED");
    }
    else {
      setText(R.id.quiz_selector_country_level, level(PlayContext.COUNTRY_QUIZ));
      findViewById(R.id.quiz_selector_country).setOnClickListener(this);
    }

    setText(R.id.quiz_selector_year_factor, factor(PlayContext.YEAR_QUIZ));
    if(Level.areAllLevelsComplete(PlayContext.YEAR_QUIZ)) {
      setText(R.id.quiz_selector_year_level, "COMPLETED");
    }
    else {
      setText(R.id.quiz_selector_year_level, level(PlayContext.YEAR_QUIZ));
      findViewById(R.id.quiz_selector_year).setOnClickListener(this);
    }
  }

  private void setText(int textViewId, String format, Object... args)
  {
    TextView textView = (TextView)findViewById(textViewId);
    if(textView == null) {
      throw new BugError("Invalid layout. Missing text view with id |%d|.", textViewId);
    }
    textView.setText(format != null ? String.format(format, args) : null);
  }

  private static String factor(PlayContext playContext)
  {
    return String.format("%dX", Balance.getQuizDifficultyFactor(playContext));
  }

  private static String level(PlayContext playContext)
  {
    return String.format("LEVEL %d", Level.getFirstUncompletedLevelIndex(playContext) + 1);
  }

  @Override
  public void onClick(View view)
  {
    switch(view.getId()) {
    case R.id.quiz_selector_brand:
      QuizActivity.start(this, PlayContext.BRAND_QUIZ);
      break;

    case R.id.quiz_selector_country:
      QuizActivity.start(this, PlayContext.COUNTRY_QUIZ);
      break;

    case R.id.quiz_selector_year:
      QuizActivity.start(this, PlayContext.YEAR_QUIZ);
      break;

    default:
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
  }
}
