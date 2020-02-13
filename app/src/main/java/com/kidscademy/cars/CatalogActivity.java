package com.kidscademy.cars;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import js.util.Player;
import js.util.Player.State;
import js.util.Strings;
import js.log.Log;
import js.log.LogFactory;
import js.view.DialogOverlay;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.Brand;
import com.kidscademy.cars.model.CatalogAdapter;
import com.kidscademy.cars.model.PlayContext;
import com.kidscademy.cars.util.Flags;

public class CatalogActivity extends AppActivityBase implements ViewPager.OnPageChangeListener, OnClickListener, Player.StateListener
{
  private static final Log log = LogFactory.getLog(CatalogActivity.class);

  private static final String ARG_BRAND = "brand";

  public static void start(Activity activity)
  {
    log.trace("start(Activity)");
    Intent intent = new Intent(activity, CatalogActivity.class);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  public static void start(Context context, Brand brand)
  {
    log.trace("start(Context, String)");
    Intent intent = new Intent(context, CatalogActivity.class);
    intent.putExtra(ARG_BRAND, brand);
    context.startActivity(intent);
  }

  public static final int OFFSCREEN_PAGE_LIMIT = 2;

  private ViewPager pager;
  private CatalogAdapter adapter;
  private Player player;
  private ImageView playButton;
  private int levelIndex;
  private DialogOverlay dialogOverlay;

  public CatalogActivity()
  {
    log.trace("CatalogActivity()");
    player = new Player(this);
    player.setStateListener(this);
  }

  public Player getPlayer()
  {
    return player;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    App.audit().openCatalog();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_catalog);

    pager = (ViewPager)findViewById(R.id.catalog_pager);
    pager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
    pager.addOnPageChangeListener(this);

    playButton = (ImageView)findViewById(R.id.catalog_play);
    playButton.setOnClickListener(this);

    findViewById(R.id.catalog_next).setOnClickListener(this);
    findViewById(R.id.catalog_previous).setOnClickListener(this);

    dialogOverlay = (DialogOverlay)findViewById(R.id.catalog_dialog_overlay);
    updateUI();

    Brand brand = getIntent().getParcelableExtra(ARG_BRAND);
    if(brand != null) {
      pager.setCurrentItem(adapter.getItemPosition(brand));
    }

    pager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
    {
      @Override
      public void onGlobalLayout()
      {
        pager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        sayBrandName();
      }
    });
  }

  public void onLevelSelected(int levelIndex)
  {
    Flags.setCurrentLevel(levelIndex);
    updateUI();
  }

  private void updateUI()
  {
    levelIndex = Flags.getCurrentLevel();
    adapter = new CatalogAdapter(getSupportFragmentManager(), App.storage().getBrands(levelIndex));
    pager.setAdapter(adapter);
    setTitle(Strings.concat("Level ", levelIndex + 1).toUpperCase(Locale.getDefault()));
  }

  @Override
  public void onStart()
  {
    log.trace("onStart()");
    super.onStart();
    player.create();
  }

  @Override
  public void onStop()
  {
    log.trace("onStop()");
    player.destroy();
    super.onStop();
  }

  @Override
  public void onClick(View view)
  {
    switch(view.getId()) {
    case R.id.catalog_play:
      if(player.isPlaying()) {
        playButton.setImageResource(R.drawable.action_audio_play);
        player.stop();
      }
      else {
        playButton.setImageResource(R.drawable.action_audio_stop);
        player.play(adapter.getItem(pager.getCurrentItem()).getSound().getVoiceAssetPath());
      }
      break;

    case R.id.catalog_next:
      pager.setCurrentItem(pager.getCurrentItem() + 1, false);
      break;

    case R.id.catalog_previous:
      pager.setCurrentItem(pager.getCurrentItem() - 1, false);
      break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.catalog, menu);

    MenuItem item = menu.findItem(R.id.action_speaker_notes);
    assert item != null;
    item.setIcon(Flags.isSpeakerNotes() ? R.drawable.ic_speaker_notes : R.drawable.ic_speaker_notes_off);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;

    case R.id.action_filter_list:
      dialogOverlay.open(R.layout.dialog_catalog_level, this);
      return true;

    case R.id.action_view_grid:
      LevelBrandsActivity.start(this, PlayContext.CATALOG, levelIndex);
      return true;

    case R.id.action_speaker_notes:
      Flags.toggleSpeakerNotes();
      item.setIcon(Flags.isSpeakerNotes() ? R.drawable.ic_speaker_notes : R.drawable.ic_speaker_notes_off);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onPageScrollStateChanged(int state)
  {
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffetPixels)
  {
  }

  @Override
  public void onPageSelected(int position)
  {
    log.debug("Pager page |%d| selected.", position);
    playButton.setImageResource(R.drawable.action_audio_play);
    player.stop();
    sayBrandName();
  }

  private void sayBrandName()
  {
    if(Flags.isSpeakerNotes()) {
      CatalogFragment fragment = adapter.getItem(pager.getCurrentItem());
      if(fragment != null) {
        // fragment could be null if catalog is opened with a specified brand
        player.play(fragment.getSound().getVoiceAssetPath());
      }
    }
  }

  @Override
  public void onPlayerStateChanged(State state)
  {
    if(state == Player.State.IDLE) {
      playButton.setImageResource(R.drawable.action_audio_play);
    }
  }

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
  }

  // ------------------------------------------------------
  // DIALOG OVERLAY

  @SuppressWarnings("unused")
  private static class LevelSelectorDialog extends FrameLayout implements DialogOverlay.Content, OnClickListener
  {
    private static final List<Character> keys = new ArrayList<Character>();
    static {
      String s = "123456789B0D";
      for(int i = 0; i < s.length(); ++i) {
        keys.add(s.charAt(i));
      }
    }

    private CatalogActivity activity;
    private DialogOverlay dialog;
    private TextView input;
    private GridLayout keyboard;
    private StringBuilder inputBuilder;

    public LevelSelectorDialog(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    @Override
    public void onOpen(DialogOverlay dialog, Object... args)
    {
      this.dialog = dialog;
      activity = (CatalogActivity)args[0];

      input = (TextView)findViewById(R.id.catalog_level_input);
      keyboard = (GridLayout)dialog.findViewById(R.id.catalog_level_keyboard);
      for(int i = 0; i < keyboard.getChildCount(); ++i) {
        keyboard.getChildAt(i).setOnClickListener(this);
      }
      inputBuilder = new StringBuilder();
    }

    @Override
    public void onClose()
    {
      activity.sayBrandName();
    }

    @Override
    public void onClick(View view)
    {
      activity.player.play("fx/click.mp3");
      char c = keys.get(keyboard.indexOfChild(view));
      switch(c) {
      case 'B':
        inputBuilder.setLength(0);
        break;

      case 'D':
        if(inputBuilder.length() == 0) {
          break;
        }
        int levelIndex = Integer.parseInt(inputBuilder.toString()) - 1;
        if(0 <= levelIndex && levelIndex < App.storage().getLevels().length) {
          activity.onLevelSelected(levelIndex);
          dialog.close();
          return;
        }
        inputBuilder.setLength(0);
        break;

      default:
        inputBuilder.append(c);
      }

      if(inputBuilder.length() < 4) {
        input.setText(inputBuilder.toString());
      }
    }
  }
}
