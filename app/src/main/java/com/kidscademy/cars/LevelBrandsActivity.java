package com.kidscademy.cars;

import java.util.Locale;

import js.util.Strings;
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
import android.widget.GridView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.Brand;
import com.kidscademy.cars.model.Level;
import com.kidscademy.cars.model.LevelBrandsAdapter;
import com.kidscademy.cars.model.PlayContext;

public class LevelBrandsActivity extends AppActivityBase implements OnItemClickListener
{
  private static final Log log = LogFactory.getLog(LevelBrandsActivity.class);

  private static final String ARG_PLAY_CONTEXT = "playContext";
  private static final String ARG_LEVEL_INDEX = "levelIndex";

  public static void start(Activity activity, PlayContext playContext, int levelIndex)
  {
    log.trace("start(Context, Level)");
    Intent intent = new Intent(activity, LevelBrandsActivity.class);
    intent.putExtra(ARG_PLAY_CONTEXT, playContext.name());
    intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_right);
  }

  private GridView gridView;
  private LevelBrandsAdapter adapter;
  private Level level;
  private boolean unsolved;
  private PlayContext playContext;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_level_brands);

    gridView = (GridView)findViewById(R.id.level_brands);
    gridView.setSoundEffectsEnabled(false);
    gridView.setOnItemClickListener(this);

    Intent intent = getIntent();
    int levelIndex = intent.getIntExtra(ARG_LEVEL_INDEX, 0);
    level = App.storage().getLevel(levelIndex);
    playContext = PlayContext.valueOf(intent.getStringExtra(ARG_PLAY_CONTEXT));

    adapter = new LevelBrandsAdapter(this, playContext, level);
    gridView.setAdapter(adapter);

    setTitle(Strings.concat("Level ", levelIndex + 1, " / brands").toUpperCase(Locale.getDefault()));
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    Brand brand = adapter.getItem(position);

    if(playContext == PlayContext.CATALOG) {
      CatalogActivity.start(this, brand);
    }
    else {
      if(!level.getState(playContext).isSolvedBrand(brand)) {
        if(App.prefs().isKidsMode()) {
          KidsGameActivity.start(this, level.getIndex(), brand.getName());
        }
        else {
          GameActivity.start(this, level.getIndex(), brand.getName());
        }
      }
    }
    overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.level_brands, menu);
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
    if(unsolved) {
      if(App.prefs().isKidsMode()) {
        KidsGameActivity.start(this, level.getIndex());
      }
      else {
        GameActivity.start(this, level.getIndex());
      }
    }
    else {
      super.onBackPressed();
    }
    overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
  }
}
