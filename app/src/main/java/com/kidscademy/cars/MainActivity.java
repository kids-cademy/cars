package com.kidscademy.cars;

import js.log.Log;
import js.log.LogFactory;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kidscademy.app.ExitDialog;
import com.kidscademy.cars.view.DrawerActivity;

public class MainActivity extends DrawerActivity
{
  /** Class logger. */
  private static final Log log = LogFactory.getLog(MainActivity.class);

  public static void start(Context context)
  {
    log.trace("start(Context)");
    Intent intent = new Intent(context, MainActivity.class);
    context.startActivity(intent);
  }

  public MainActivity()
  {
    log.trace("MainActivity()");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState, R.layout.activity_main);

    findViewById(R.id.main_catalog).setOnClickListener(this);
    findViewById(R.id.main_play).setOnClickListener(this);
    findViewById(R.id.main_quiz).setOnClickListener(this);
    findViewById(R.id.main_score).setOnClickListener(this);
  }

  @Override
  public void onStart()
  {
    log.trace("onStart()");
    super.onStart();
    findViewById(R.id.main_quiz).setVisibility(App.prefs().isKidsMode() ? View.INVISIBLE : View.VISIBLE);
  }

  @Override
  public void onClick(View view)
  {
    if(onDrawerClick(view)) {
      return;
    }

    switch(view.getId()) {
    case R.id.main_catalog:
      CatalogActivity.start(this);
      break;

    case R.id.main_play:
      LevelsActivity.start(this);
      break;

    case R.id.main_quiz:
      QuizSelectorActivity.start(this);
      break;

    case R.id.main_score:
      BalanceActivity.start(this);
      break;
    }
  }

  @Override
  public void onBackPressed()
  {
    if(closeDrawerIfOpened()) {
      return;
    }
    ExitDialog dialog = new ExitDialog();
    dialog.open(getSupportFragmentManager(), new Runnable()
    {
      @Override
      public void run()
      {
        MainActivity.this.finish();
      }
    });
  }
}
