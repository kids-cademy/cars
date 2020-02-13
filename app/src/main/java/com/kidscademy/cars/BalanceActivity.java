package com.kidscademy.cars;

import js.log.Log;
import js.log.LogFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.app.ConfirmDialog;
import com.kidscademy.cars.model.Balance;
import com.kidscademy.cars.model.Counters;
import com.kidscademy.cars.model.Level;
import com.kidscademy.cars.model.PlayContext;

public class BalanceActivity extends AppActivityBase
{
  private static final Log log = LogFactory.getLog(BalanceActivity.class);

  public static void start(Activity activity)
  {
    log.trace("start(Context)");
    Intent intent = new Intent(activity, BalanceActivity.class);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  private Balance balance;
  private Counters counters;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    App.audit().viewBalance();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_balance);

    balance = App.storage().getBalance();
    counters = App.storage().getCounters();

    if(App.prefs().isKidsMode()) {
      findViewById(R.id.balance_standard_game).setVisibility(View.GONE);
    }
    else {
      findViewById(R.id.balance_kids_game).setVisibility(View.GONE);
    }
  }

  @Override
  public void onStart()
  {
    super.onStart();
    updateUI();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.balance, menu);
    if(!App.prefs().isKidsMode()) {
      menu.findItem(R.id.action_view_list).setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;

    case R.id.action_view_list:
      App.audit().viewScoresList();
      ScoresListActivity.start(this);
      return true;

    case R.id.action_remove:
      ConfirmDialog dialog = new ConfirmDialog(R.string.scores_list_confirm_remove);
      dialog.open(getSupportFragmentManager(), new Runnable()
      {
        @Override
        public void run()
        {
          log.debug("Reset cars balance.");
          App.audit().resetScore();
          counters.reset();
          balance.reset();
          App.storage().resetLevels();
          updateUI();
        }
      });
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

  private void updateUI()
  {
    if(App.prefs().isKidsMode()) {
      TextView scoreView = (TextView)findViewById(R.id.balance_kids_score);
      scoreView.setText(String.format("+%04d", balance.getScore()));
    }
    else {
      TextView totalLevelsView = (TextView)findViewById(R.id.balance_total_levels);
      totalLevelsView.setText(Integer.toString(Level.getTotalLevels()));

      TextView unlockedLevelsView = (TextView)findViewById(R.id.balance_unlocked_levels);
      unlockedLevelsView.setText(Integer.toString(Level.getUnlockedLevels(PlayContext.GAME)));

      TextView completedLevelsView = (TextView)findViewById(R.id.balance_completed_levels);
      completedLevelsView.setText(Integer.toString(Level.getCompletedLevels(PlayContext.GAME)));

      TextView totalBrandsView = (TextView)findViewById(R.id.balance_total_brands);
      totalBrandsView.setText(Integer.toString(Level.getTotalBrands()));

      TextView completedBrandsView = (TextView)findViewById(R.id.balance_completed_brands);
      completedBrandsView.setText(Integer.toString(Level.getSolvedBrands(PlayContext.GAME)));

      TextView pointsView = (TextView)findViewById(R.id.balance_points);
      pointsView.setText(String.format("+ %04d", balance.getScore()));

      TextView creditsView = (TextView)findViewById(R.id.balance_credits);
      creditsView.setText(String.format("+ %04d", balance.getCredit()));
    }
  }
}
