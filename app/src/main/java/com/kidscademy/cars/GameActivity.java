package com.kidscademy.cars;

import java.util.Locale;
import java.util.Random;

import js.util.BitmapLoader;
import js.util.Player;
import js.util.Strings;
import js.log.Log;
import js.log.LogFactory;
import js.view.DialogOverlay;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.kidscademy.cars.util.GameEngine;
import com.kidscademy.cars.view.KeyboardView;
import com.kidscademy.cars.view.NameView;

public class GameActivity extends AppActivityBase implements OnClickListener, KeyboardView.Listener, NameView.Listener
{
  private static final Log log = LogFactory.getLog(GameActivity.class);

  private static final String ARG_LEVEL_INDEX = "levelIndex";
  private static final String ARG_BRAND_NAME = "brandName";

  public static void start(Activity activity, int levelIndex)
  {
    log.trace("start(Activity, int)");
    Intent intent = new Intent(activity, GameActivity.class);
    intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  public static void start(Context context, int levelIndex, String brandName)
  {
    log.trace("start(Context, int, String)");
    Intent intent = new Intent(context, GameActivity.class);
    intent.putExtra(ARG_LEVEL_INDEX, levelIndex);
    intent.putExtra(ARG_BRAND_NAME, brandName);
    context.startActivity(intent);
  }

  private static final Random random = new Random();

  private GameEngine engine;
  private NameView nameView;
  private KeyboardView keyboardView;
  private ImageView imageView;
  private TextView scoreView;
  private TextView creditView;
  private TextView scorePlusView;
  private TextView brandsCountView;
  private TextView solvedBrandsCountView;
  private Player player;
  private Handler handler;
  private Menu menu;

  /** The level currently played. */
  private Level level;
  private LevelState levelState;

  /** Overlay revealed to notify about next level being unlocked. */
  private DialogOverlay dialogOverlay;

  /** Car brand currently displayed as challenge. */
  private Brand challengedBrand;

  private Balance balance;

  /** Run {@link #nextChallenge()}. */
  private Runnable nextChallenge = new Runnable()
  {
    @Override
    public void run()
    {
      nextChallenge();
    }
  };

  /** Reveal {@link #dialogOverlay} and wait for user to close it. On overlay close load next challenge. */
  private Runnable nextLevelUnlocked = new Runnable()
  {
    @Override
    public void run()
    {
      // last argument is true for level complete, so that in case of level unlocked is false
      dialogOverlay.open(R.layout.dialog_level_state, GameActivity.this, false);
    }
  };

  public GameActivity()
  {
    log.trace("Game()");
    player = new Player(this);
    handler = new Handler();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    int levelIndex = getIntent().getIntExtra(ARG_LEVEL_INDEX, 0);
    level = App.storage().getLevel(levelIndex);
    levelState = App.storage().getLevelState(PlayContext.GAME, levelIndex);
    engine = new GameEngine(levelState);
    balance = App.storage().getBalance();

    nameView = (NameView)findViewById(R.id.game_brand_name);
    nameView.setPlayer(player);
    nameView.setListener(this);

    keyboardView = (KeyboardView)findViewById(R.id.game_keyboard);
    keyboardView.setPlayer(player);
    keyboardView.setListener(this);

    scoreView = (TextView)findViewById(R.id.game_score);
    creditView = (TextView)findViewById(R.id.game_credit);
    scorePlusView = (TextView)findViewById(R.id.game_score_plus);
    brandsCountView = (TextView)findViewById(R.id.game_brands_count);
    solvedBrandsCountView = (TextView)findViewById(R.id.game_solved_brands_count);
    imageView = (ImageView)findViewById(R.id.game_brand_image);

    dialogOverlay = (DialogOverlay)findViewById(R.id.game_unlock_level_overlay);

    setTitle(Strings.concat("level ", levelIndex + 1).toUpperCase(Locale.getDefault()));
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
    super.onStop();
  }

  @Override
  public void onClick(View view)
  {
    view.setVisibility(View.INVISIBLE);
    player.play("fx/click.mp3");
  }

  @Override
  public boolean onKeyboardChar(char c)
  {
    return handleKeyboardChar(c);
  }

  private boolean handleKeyboardChar(char c)
  {
    if(nameView.hasAllCharsFilled()) {
      player.play("fx/negative.mp3");
      return false;
    }
    nameView.putChar(c);
    if(!nameView.hasAllCharsFilled()) {
      return true;
    }

    if(!engine.checkAnswer(nameView.getValue())) {
      player.play("fx/negative.mp3");
      return true;
    }

    scorePlusView.setText(String.format("+%d", Balance.getScoreIncrement(level.getIndex())));
    scorePlusView.setVisibility(View.VISIBLE);
    imageView.setVisibility(View.INVISIBLE);

    player.play(String.format("fx/positive-%d.mp3", random.nextInt(5)));
    BitmapLoader loader = new BitmapLoader(this, challengedBrand.getLogoAssetPath(), imageView);
    loader.start();

    // engine check answer takes care to unlock next level if current level threshold was reached
    if(engine.wasNextLevelUnlocked()) {
      handler.postDelayed(nextLevelUnlocked, 2000);
    }
    else {
      handler.postDelayed(nextChallenge, 2000);
    }

    return true;
  }

  @Override
  public void onNameChar(char c)
  {
    keyboardView.ungetChar(c);
  }

  private void nextChallenge()
  {
    challengedBrand = engine.nextChallenge();
    updateUI();
  }

  private void updateUI()
  {
    if(menu != null) {
      menu.getItem(0).setVisible(false);
    }

    if(challengedBrand == null) {
      // last argument is true to signal level complete
      dialogOverlay.open(R.layout.dialog_level_state, GameActivity.this, true);
      return;
    }

    scoreView.setText(Integer.toString(engine.getBalance().getScore()));
    creditView.setText(Integer.toString(engine.getBalance().getCredit()));

    brandsCountView.setText(Integer.toString(level.getBrandsCount()));
    solvedBrandsCountView.setText(Integer.toString(levelState.getSolvedBrandsCount()));
    nameView.init(challengedBrand.getName());
    keyboardView.init(challengedBrand.getName());

    BitmapLoader loader = new BitmapLoader(this, challengedBrand.getDimLogoAssetPath(), imageView);
    loader.setRunnable(new Runnable()
    {
      @Override
      public void run()
      {
        scorePlusView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
      }
    });
    loader.start();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    this.menu = menu;
    getMenuInflater().inflate(R.menu.game, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;

    case R.id.action_play:
      player.play(challengedBrand.getVoiceAssetPath());
      return true;

    case R.id.action_info:
      if(!engine.getBalance().hasCredit()) {
        dialogOverlay.open(R.layout.dialog_no_credit, this);
      }
      else {
        dialogOverlay.open(R.layout.dialog_trade_hints, this);
      }
      return true;

    case R.id.action_view_grid:
      LevelBrandsActivity.start(this, PlayContext.GAME, level.getIndex());
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
  private static class LevelStateDialog extends GameDialog
  {
    private boolean levelComplete;

    public LevelStateDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      super.onOpen(dialog, args);
      levelComplete = (Boolean)args[1];
      activity.player.play("fx/hooray.mp3");

      if(levelComplete) {
        dialog.setText(R.id.level_state_message, "LEVEL %d COMPLETE", activity.level.getIndex() + 1);
        dialog.setText(R.id.level_state_bonus, "+%d", Balance.getScoreLevelCompleteBonus(activity.level.getIndex()));
      }
      else {
        dialog.setText(R.id.level_state_message, "LEVEL %d UNLOCKED", activity.engine.getUnlockedLevelIndex() + 1);
        dialog.setText(R.id.level_state_bonus, "+%d", Balance.getScoreLevelUnlockBonus(activity.level.getIndex()));
      }
    }

    @Override
    public void onClose()
    {
      super.onClose();
      if(levelComplete) {
        if(Level.areAllLevelsComplete(PlayContext.GAME)) {
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

  @SuppressWarnings("unused")
  private static class NoCreditDialog extends GameDialog implements OnClickListener
  {
    private boolean openQuizSelector;

    public NoCreditDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      super.onOpen(dialog, args);
      dialog.findViewById(R.id.no_credit_action).setOnClickListener(this);
    }

    @Override
    public void onClose()
    {
      super.onClose();
      if(openQuizSelector) {
        QuizSelectorActivity.start(activity);
      }
    }

    @Override
    public void onClick(View view)
    {
      switch(view.getId()) {
      case R.id.no_credit_action:
        openQuizSelector = true;
        dialog.close();
        break;
      }
    }
  }

  @SuppressWarnings("unused")
  private static class TradeHintsDialog extends GameDialog implements OnClickListener
  {
    public TradeHintsDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      super.onOpen(dialog, args);

      dialog.setText(R.id.trade_hints_title, activity.getString(R.string.trade_hints_title), activity.balance.getCredit());
      dialog.setText(R.id.reveal_letter_deduction, "%d", Balance.getRevealLetterDeduction());
      dialog.setText(R.id.verify_deduction, "%d", Balance.getVerifyInputDeduction());
      dialog.setText(R.id.hide_letters_deduction, "%d", Balance.getHideLettersDeduction());
      dialog.setText(R.id.say_name_deduction, "%d", Balance.getSayNameDeduction());

      dialog.findViewById(R.id.reveal_letter_action).setOnClickListener(this);
      dialog.findViewById(R.id.verify_action).setOnClickListener(this);
      dialog.findViewById(R.id.hide_letters_action).setOnClickListener(this);
      dialog.findViewById(R.id.say_name_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
      boolean deductionPerformed = false;

      switch(view.getId()) {
      case R.id.reveal_letter_action:
        if(activity.balance.deductRevealLetter()) {
          deductionPerformed = true;
          int firstMissingCharIndex = activity.nameView.getFirstMissingCharIndex();
          assert firstMissingCharIndex != -1;
          activity.handleKeyboardChar(activity.keyboardView.getExpectedChar(firstMissingCharIndex));
        }
        break;

      case R.id.verify_action:
        if(activity.balance.deductVerifyInput()) {
          deductionPerformed = true;
          activity.handler.postDelayed(new Runnable()
          {
            @Override
            public void run()
            {
              activity.dialogOverlay.open(R.layout.dialog_input_verify, activity);
            }
          }, 1000);
        }
        break;

      case R.id.hide_letters_action:
        if(activity.balance.deductHideLettersInput()) {
          deductionPerformed = true;
          activity.keyboardView.hideUnusedLetters();
        }
        break;

      case R.id.say_name_action:
        if(activity.balance.deductSayName()) {
          deductionPerformed = true;
          activity.menu.findItem(R.id.action_play).setVisible(true);
          activity.handler.postDelayed(new Runnable()
          {
            @Override
            public void run()
            {
              activity.player.play(activity.challengedBrand.getVoiceAssetPath());
            }
          }, 1000);
        }
        break;
      }

      if(deductionPerformed) {
        activity.creditView.setText(Integer.toString(activity.engine.getBalance().getCredit()));
      }
      else {
        activity.handler.postDelayed(new Runnable()
        {
          @Override
          public void run()
          {
            activity.dialogOverlay.open(R.layout.dialog_no_credit, activity);
          }
        }, 1000);
      }
      dialog.close();
    }
  }

  @SuppressWarnings("unused")
  private static class InputVerifyDialog extends GameDialog
  {
    private NameView brandName;

    public InputVerifyDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      super.onOpen(dialog, args);
      brandName = (NameView)findViewById(R.id.input_verify_brand_name);
      brandName.init(activity.challengedBrand.getName());
      brandName.verify(activity.nameView.getInput());
    }
  }

  private static class GameDialog extends FrameLayout implements DialogOverlay.Content
  {
    protected DialogOverlay dialog;
    protected GameActivity activity;

    public GameDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      this.dialog = dialog;
      activity = (GameActivity)args[0];
      activity.keyboardView.disable();
      activity.nameView.disable();
    }

    @Override
    public void onClose()
    {
      activity.keyboardView.enable();
      activity.nameView.enable();
    }
  }
}
