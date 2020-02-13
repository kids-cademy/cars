package com.kidscademy.cars;

import java.util.List;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.Balance;
import com.kidscademy.cars.model.Brand;
import com.kidscademy.cars.model.Level;
import com.kidscademy.cars.model.PlayContext;
import com.kidscademy.cars.util.Flags;
import com.kidscademy.cars.util.QuizEngine;

public class QuizActivity extends AppActivityBase implements OnClickListener
{
  private static final Log log = LogFactory.getLog(QuizActivity.class);

  private static final String ARG_PLAY_CONTEXT = "playContext";

  public static void start(Activity activity, PlayContext playContext)
  {
    log.trace("start(Activity, PlayContext)");
    Intent intent = new Intent(activity, QuizActivity.class);
    intent.putExtra(ARG_PLAY_CONTEXT, playContext.name());
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  private static final Random random = new Random();

  private PlayContext playContext;
  private int levelIndex;
  private QuizEngine engine;
  private ImageView brandLogoView;
  private TextView brandNameView;
  private Player player;
  private Handler handler;
  private Brand challengedBrand;
  private Button[] optionButtons;

  private RatingBar leftTriesStars;
  private TextView creditsView;
  private TextView levelCreditsView;
  private TextView solvedView;
  private TextView totalView;

  private DialogOverlay dialogOverlay;

  private Runnable update = new Runnable()
  {
    @Override
    public void run()
    {
      updateUI();
    }
  };

  public QuizActivity()
  {
    log.trace("SpellSound()");
    player = new Player(this);
    handler = new Handler();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quiz);

    Intent intent = getIntent();
    playContext = PlayContext.valueOf(intent.getStringExtra(ARG_PLAY_CONTEXT));
    levelIndex = Level.getFirstUncompletedLevelIndex(playContext);

    engine = new QuizEngine(playContext, levelIndex);
    brandLogoView = (ImageView)findViewById(R.id.quiz_logo);
    brandNameView = (TextView)findViewById(R.id.quiz_name);

    GridLayout optionsGrid = (GridLayout)findViewById(R.id.quiz_options);
    optionButtons = new Button[optionsGrid.getChildCount()];
    for(int i = 0; i < optionButtons.length; ++i) {
      optionButtons[i] = (Button)optionsGrid.getChildAt(i);
      optionButtons[i].setOnClickListener(this);
    }

    leftTriesStars = (RatingBar)findViewById(R.id.quiz_left_tries);
    dialogOverlay = (DialogOverlay)findViewById(R.id.quiz_dialog_overlay);

    creditsView = (TextView)findViewById(R.id.quiz_credits);
    levelCreditsView = (TextView)findViewById(R.id.quiz_level_credits);
    solvedView = (TextView)findViewById(R.id.quiz_solved);
    totalView = (TextView)findViewById(R.id.quiz_total);

    setTitle(Strings.concat(playContext.display(), " ", levelIndex + 1));
    App.audit().playQuiz(playContext, levelIndex);
  }

  @Override
  public void onStart()
  {
    log.trace("onStart()");
    super.onStart();
    player.create();
    Flags.setCurrentLevel(levelIndex);
    updateUI();
  }

  @Override
  public void onStop()
  {
    log.trace("onStop()");
    player.destroy();
    dialogOverlay.hide();
    handler.removeCallbacks(update);
    super.onStop();
  }

  @Override
  public void onClick(View view)
  {
    if(dialogOverlay.isActive()) {
      return;
    }

    // selected option text is generated and also checked by quiz engine
    // since display language is not an issue we can use user interface text
    if(!engine.checkAnswer(((TextView)view).getText().toString())) {
      player.play("fx/click.mp3");
      updateUI();
      return;
    }

    log.debug("Correct answer for quiz on |%s|\n", challengedBrand);
    BitmapLoader loader = new BitmapLoader(this, challengedBrand.getLogoAssetPath(), brandLogoView);
    loader.start();
    brandNameView.setText(challengedBrand.getDisplay());
    player.play(String.format("fx/positive-%d.mp3", random.nextInt(5)));
    handler.postDelayed(update, 2000);
  }

  private void updateUI()
  {
    if(leftTriesStars != null) {
      leftTriesStars.setRating(engine.getLeftTries());
    }

    if(engine.noMoreTires()) {
      dialogOverlay.open(R.layout.dialog_no_tries, this);
      return;
    }
    challengedBrand = engine.nextChallenge();
    if(challengedBrand == null) {
      dialogOverlay.open(R.layout.dialog_quiz_complete, this);
      return;
    }

    Balance balance = App.storage().getBalance();
    creditsView.setText(Integer.toString(balance.getCredit()));
    levelCreditsView.setText(Integer.toString(engine.getCollectedCredits()));
    solvedView.setText(Integer.toString(engine.getBrandIndex()));
    totalView.setText(Integer.toString(engine.getBrandsCount()));

    BitmapLoader loader = new BitmapLoader(this, challengedBrand.getDimLogoAssetPath(), brandLogoView);
    loader.start();
    brandNameView.setText(null);

    List<String> options = engine.getOptions(optionButtons.length);
    for(int i = 0; i < optionButtons.length; ++i) {
      final String option = options.get(i);
      optionButtons[i].setText(option);
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.quiz, menu);

    MenuItem item = menu.findItem(R.id.quiz_left_tries);
    leftTriesStars = (RatingBar)MenuItemCompat.getActionView(item);

    leftTriesStars.setScaleX(0.6F);
    leftTriesStars.setScaleY(0.6F);

    leftTriesStars.setIsIndicator(true);
    leftTriesStars.setMax(3);
    leftTriesStars.setNumStars(3);
    leftTriesStars.setRating(3);
    leftTriesStars.setStepSize(1);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;

    case R.id.action_skip_next:
      player.stop();
      updateUI();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent(this, QuizSelectorActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
  }

  // ------------------------------------------------------
  // DIALOG OVERLAYS
  
  @SuppressWarnings("unused")
  private static class NoMoreTriesDialog extends FrameLayout implements DialogOverlay.Content, OnClickListener
  {
    private DialogOverlay dialog;
    private QuizActivity activity;

    public NoMoreTriesDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      this.dialog = dialog;
      activity = (QuizActivity)args[0];
      findViewById(R.id.no_more_tries_action).setOnClickListener(this);
    }

    @Override
    public void onClose()
    {
      MainActivity.start(activity);
    }

    @Override
    public void onClick(View view)
    {
      switch(view.getId()) {
      case R.id.no_more_tries_action:
        CatalogActivity.start(activity);
        dialog.hide();
        break;
      }
    }
  }

  @SuppressWarnings("unused")
  private static class QuizCompleteDialog extends FrameLayout implements DialogOverlay.Content
  {
    private QuizActivity activity;

    public QuizCompleteDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    public void onOpen(DialogOverlay dialog, Object... args)
    {
      activity = (QuizActivity)args[0];
      activity.player.play("fx/hooray.mp3");

      dialog.setText(R.id.quiz_complete_message, "%s LEVEL %d", activity.playContext.display(), activity.levelIndex + 1);
      dialog.setText(R.id.quiz_complete_credits, "+%d", activity.engine.getCollectedCredits());
    }

    public void onClose()
    {
      QuizSelectorActivity.start(activity);
      activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
  }
}
