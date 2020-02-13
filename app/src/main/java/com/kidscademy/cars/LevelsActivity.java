package com.kidscademy.cars;

import js.util.Player;
import js.log.Log;
import js.log.LogFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.LevelState;
import com.kidscademy.cars.model.LevelsAdapter;
import com.kidscademy.cars.model.PlayContext;
import com.kidscademy.cars.util.Flags;

public class LevelsActivity extends AppActivityBase implements OnItemClickListener
{
  private static final Log log = LogFactory.getLog(LevelsActivity.class);

  public static void start(Activity activity)
  {
    log.trace("start(Activity)");
    Intent intent = new Intent(activity, LevelsActivity.class);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  private ListView listView;
  private LevelsAdapter adapter;
  private Player player;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_levels);

    player = new Player(this);

    listView = (ListView)findViewById(R.id.levels);
    listView.setOnItemClickListener(this);

    adapter = new LevelsAdapter(this, App.storage().getLevels());
    listView.setAdapter(adapter);
  }

  @Override
  public void onStart()
  {
    log.trace("onStart()");
    super.onStart();
    player.create();
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onStop()
  {
    log.trace("onStop()");
    super.onStop();
    player.destroy();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    // level view into list is level index from levels array
    final int levelIndex = position;

    Flags.setCurrentLevel(levelIndex);
    PlayContext playContext = App.prefs().isKidsMode() ? PlayContext.KIDS_GAME : PlayContext.GAME;
    LevelState levelState = App.storage().getLevelState(playContext, levelIndex);

    if(!levelState.isUnlocked()) {
      player.play("fx/negative.mp3");
      return;
    }

    if(levelState.isComplete()) {
      LevelBrandsActivity.start(this, playContext, levelIndex);
    }
    else {
      App.audit().playGame(App.storage().getLevel(levelIndex).getName());
      if(App.prefs().isKidsMode()) {
        KidsGameActivity.start(this, position);
      }
      else {
        GameActivity.start(this, position);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.levels, menu);
    return true;
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
