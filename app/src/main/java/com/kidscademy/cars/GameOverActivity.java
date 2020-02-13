package com.kidscademy.cars;

import js.log.Log;
import js.log.LogFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kidscademy.app.AppActivityBase;

public class GameOverActivity extends AppActivityBase
{
  private static final Log log = LogFactory.getLog(GameOverActivity.class);

  public static void start(Activity activity)
  {
    log.trace("start(Activity)");
    Intent intent = new Intent(activity, GameOverActivity.class);
    activity.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_over);
  }
}
