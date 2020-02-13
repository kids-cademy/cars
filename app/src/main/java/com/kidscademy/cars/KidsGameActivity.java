package com.kidscademy.cars;

import java.util.List;
import java.util.Random;

import js.util.BitmapLoader;
import js.util.Player;
import js.log.Log;
import js.log.LogFactory;
import js.view.DialogOverlay;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.Balance;
import com.kidscademy.cars.model.Brand;
import com.kidscademy.cars.model.Level;
import com.kidscademy.cars.model.LevelState;
import com.kidscademy.cars.model.PlayContext;
import com.kidscademy.cars.util.KidsGameEngine;

public class KidsGameActivity extends AppActivityBase implements OnClickListener
{
  private static final Log log = LogFactory.getLog(KidsGameActivity.class);

  private static final String ARG_LEVEL_INDEX = "levelIndex";
  private static final String ARG_BRAND_NAME = "brandName";

  public static void start(Activity activity, int levelIndex)
  {
    log.trace("start(Activity, int)");
    Intent intent = new Intent(activity, KidsGameActivity.class);
    intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  public static void start(Context context, int levelIndex, String brandName)
  {
    log.trace("start(Context, int, String)");
    Intent intent = new Intent(context, KidsGameActivity.class);
    intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
    intent.putExtra(ARG_BRAND_NAME, brandName);
    context.startActivity(intent);
  }

  private static final Random random = new Random();

  private KidsGameEngine engine;
  private GridLayout optionsGrid;
  private Player player;
  private Handler handler;
  private Brand challengedBrand;

  /** The level currently played. */
  private Level level;
  private LevelState levelState;

  private DialogOverlay dialogOverlay;

  private TextView brandNameView;
  private TextView scoreView;
  private TextView brandsCountView;
  private TextView solvedBrandsCountView;

  /**
   * Flag used to protect against many click event on rapid pace. This is a sort of noise reduction or debouncer. This
   * locking mechanism is necessary because click processing involve concurrency and potential race condition due to
   * postDelay, used to display dialogs.
   * 
   * When a click event arrive onClick() checks if is not already processing one and reject if it is. Only after correct
   * answer dialog is closed click processing is re-enabled. Takes care onStop() to leave click processing enabled.
   */
  private volatile boolean clickProcessing;

  private Runnable nextChallenge = new Runnable()
  {
    @Override
    public void run()
    {
      nextChallenge();
    }
  };

  private Runnable nextLevelUnlocked = new Runnable()
  {
    @Override
    public void run()
    {
      dialogOverlay.open(R.layout.dialog_kids_level_state, KidsGameActivity.this, false);
    }
  };

  private Runnable levelComplete = new Runnable()
  {
    @Override
    public void run()
    {
      dialogOverlay.open(R.layout.dialog_kids_level_state, KidsGameActivity.this, true);
    }
  };

  public KidsGameActivity()
  {
    log.trace("KidsGame()");
    player = new Player(this);
    handler = new Handler();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_kids_game);

    int levelIndex = getIntent().getIntExtra(ARG_LEVEL_INDEX, 0);
    level = App.storage().getLevel(levelIndex);
    levelState = App.storage().getLevelState(PlayContext.KIDS_GAME, levelIndex);
    engine = new KidsGameEngine(levelState);
    optionsGrid = (GridLayout)findViewById(R.id.kids_game_grid);

    for(int i = 0; i < optionsGrid.getChildCount(); ++i) {
      optionsGrid.getChildAt(i).setOnClickListener(this);
    }

    dialogOverlay = (DialogOverlay)findViewById(R.id.kids_game_dialog_overlay);
    brandNameView = (TextView)findViewById(R.id.kids_game_brand_name);
    scoreView = (TextView)findViewById(R.id.kids_game_score);
    brandsCountView = (TextView)findViewById(R.id.kids_game_total_brands);
    solvedBrandsCountView = (TextView)findViewById(R.id.kids_game_solved_brands);

    setTitle(String.format("LEVEL %d", levelIndex + 1));
  }

  @Override
  public void onStart()
  {
    log.trace("onStart()");
    super.onStart();
    player.create();

    challengedBrand = engine.initChallenge(getIntent().getStringExtra(ARG_BRAND_NAME));
    updateUI();
  }

  @Override
  public void onStop()
  {
    log.trace("onStop()");
    player.destroy();
    handler.removeCallbacks(nextChallenge);
    handler.removeCallbacks(playExpectedCar);
    clickProcessing = false;
    super.onStop();
  }

  @Override
  public void onClick(View view)
  {
    // protect against many click events in quick pace
    if(clickProcessing) {
      return;
    }
    clickProcessing = true;

    Brand brand = (Brand)view.getTag();
    if(!engine.checkAnswer(brand.getName())) {
      player.play("fx/click.mp3");
      nextChallenge();
      clickProcessing = false;
      return;
    }

    log.debug("Correct answer for quiz on |%s|\n", challengedBrand);

    dialogOverlay.open(R.layout.dialog_kids_correct_answer);
    dialogOverlay.setImage(R.id.kids_correct_answer_logo, challengedBrand.getLogoAssetPath());
    dialogOverlay.setText(R.id.kids_correct_asnwer_bonus, "+%d", Balance.getScoreIncrement(level.getIndex()));

    player.play(String.format("fx/positive-%d.mp3", random.nextInt(5)));
    handler.postDelayed(nextChallenge, 1500);
  }

  private void nextChallenge()
  {
    if(engine.wasNextLevelUnlocked()) {
      dialogOverlay.close();
      handler.postDelayed(nextLevelUnlocked, 1500);
      return;
    }

    challengedBrand = engine.nextChallenge();
    updateUI();

    // do not enable click processing if level is complete
    // wait for user to close level complete dialog and re-enable click processing onStart
    if(challengedBrand != null) {
      clickProcessing = false;
    }
  }

  private void updateUI()
  {
    if(challengedBrand == null) {
      dialogOverlay.close();
      handler.postDelayed(levelComplete, 1500);
      return;
    }

    brandNameView.setText(challengedBrand.getDisplay());
    scoreView.setText(Integer.toString(engine.getBalance().getScore()));
    brandsCountView.setText(Integer.toString(level.getBrandsCount()));
    solvedBrandsCountView.setText(Integer.toString(levelState.getSolvedBrandsCount()));

    List<Brand> options = engine.getBrandOptions(6);
    for(int i = 0; i < optionsGrid.getChildCount(); ++i) {
      final ImageView iconView = (ImageView)optionsGrid.getChildAt(i);
      final Brand brand = options.get(i);
      iconView.setTag(brand);

      BitmapLoader loader = new BitmapLoader(this, brand.getLogoAssetPath(), iconView);
      loader.start();
    }

    dialogOverlay.close();
    handler.postDelayed(playExpectedCar, 1000);
  }

  private Runnable playExpectedCar = new Runnable()
  {
    @Override
    public void run()
    {
      player.play(challengedBrand.getVoiceAssetPath());
    }
  };

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.kids_game, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;

    case R.id.action_view_carousel:
      CatalogActivity.start(this, challengedBrand);
      return true;

    case R.id.action_view_grid:
      LevelBrandsActivity.start(this, PlayContext.KIDS_GAME, level.getIndex());
      return true;

    case R.id.action_play:
      player.play(challengedBrand.getVoiceAssetPath());
      return true;

    case R.id.action_skip_next:
      player.stop();
      nextChallenge();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent(this, LevelsActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
  }

  // ------------------------------------------------------
  // DIALOG OVERLAY
  
  @SuppressWarnings("unused")
  private static class LevelStateDialog extends FrameLayout implements DialogOverlay.Content
  {
    private KidsGameActivity activity;
    private boolean levelComplete;

    public LevelStateDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      activity = (KidsGameActivity)args[0];
      levelComplete = (Boolean)args[1];
      activity.player.play("fx/hooray.mp3");

      int levelIndex = activity.level.getIndex();
      if(levelComplete) {
        dialog.setText(R.id.level_state_message, "LEVEL %d COMPLETE", activity.level.getIndex() + 1);
        dialog.setText(R.id.level_state_bonus, "+%d", Balance.getScoreLevelCompleteBonus(levelIndex));
      }
      else {
        dialog.setText(R.id.level_state_message, "LEVEL %d UNLOCKED", activity.engine.getUnlockedLevelIndex() + 1);
        dialog.setText(R.id.level_state_bonus, "+%d", Balance.getScoreLevelUnlockBonus(levelIndex));
      }
    }

    @Override
    public void onClose()
    {
      if(levelComplete) {
        if(Level.areAllLevelsComplete(PlayContext.KIDS_GAME)) {
          GameOverActivity.start(activity);
        }
        else {
          LevelsActivity.start(activity);
          activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
      }
      else {
        activity.handler.postDelayed(activity.nextChallenge, 1000);
      }
    }
  }
}
