package com.kidscademy.cars.view;

import js.log.Log;
import js.log.LogFactory;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.kidscademy.app.AboutActivity;
import com.kidscademy.app.AppActivityBase;
import com.kidscademy.app.AppShareActivity;
import com.kidscademy.app.NoAdsManifestActivity;
import com.kidscademy.app.PreferencesActivity;
import com.kidscademy.app.RecommendedAppsActivity;
import com.kidscademy.cars.App;
import com.kidscademy.cars.R;
import com.kidscademy.cars.FeaturesActivity;

/**
 * Base class for activities implementing drawer.
 * 
 * @author Iulian Rotaru
 */
public abstract class DrawerActivity extends AppActivityBase implements OnClickListener
{
  private static final Log log = LogFactory.getLog(DrawerActivity.class);

  private DrawerLayout drawerLayout;
  private View drawerView;
  private ActionBarDrawerToggle drawerToggle;
  private Intent drawerIntent;

  public void onCreate(Bundle savedInstanceState, int layoutResID)
  {
    log.trace("onCreate(Bundle, int)");
    super.onCreate(savedInstanceState);
    setContentView(layoutResID);

    drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
    drawerView = findViewById(R.id.drawer);

    findViewById(R.id.drawer_kids_cademy).setOnClickListener(this);
    findViewById(R.id.drawer_settings).setOnClickListener(this);
    findViewById(R.id.drawer_help).setOnClickListener(this);
    findViewById(R.id.drawer_rate).setOnClickListener(this);
    findViewById(R.id.drawer_recommended).setOnClickListener(this);
    findViewById(R.id.drawer_share).setOnClickListener(this);
    findViewById(R.id.drawer_about).setOnClickListener(this);
    findViewById(R.id.drawer_no_ads).setOnClickListener(this);

    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.app_name)
    {
      @Override
      public void onDrawerClosed(View view)
      {
        if(drawerIntent != null) {
          startActivity(drawerIntent);
          drawerIntent = null;
        }
      }
    };
    drawerLayout.addDrawerListener(drawerToggle);
  }

  @Override
  public void onClick(View view)
  {
    onDrawerClick(view);
  }

  /**
   * Handle click event for drawer child and return drawer opened state. Returned value should be used by superclass, if
   * implements its own click event handler, to process event only if drawer is closed, see code snippet.
   * 
   * <pre>
   * if(onDrawerClick(view)) {
   *   return;
   * }
   * . . .
   * </pre>
   * 
   * @param view drawer child view.
   * @return true if drawer is opened.
   */
  protected boolean onDrawerClick(View view)
  {
    if(!drawerLayout.isDrawerOpen(Gravity.START)) {
      return false;
    }
    boolean clickHandled = true;

    switch(view.getId()) {
    case R.id.drawer_kids_cademy:
      App.audit().openMarket();
      try {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + getString(R.string.app_publisher))));
      }
      catch(android.content.ActivityNotFoundException unused) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:" + getString(R.string.app_publisher))));
      }
      break;

    case R.id.drawer_settings:
      drawerIntent = new Intent(this, PreferencesActivity.class);
      break;

    case R.id.drawer_help:
      drawerIntent = new Intent(this, FeaturesActivity.class);
      break;

    case R.id.drawer_rate:
      App.audit().openRate();
      try {
        startActivity(rate("market://details"));
      }
      catch(ActivityNotFoundException e) {
        startActivity(rate("http://play.google.com/store/apps/details"));
      }
      break;

    case R.id.drawer_recommended:
      drawerIntent = new Intent(this, RecommendedAppsActivity.class);
      break;

    case R.id.drawer_share:
      drawerIntent = new Intent(this, AppShareActivity.class);
      break;

    case R.id.drawer_about:
      drawerIntent = new Intent(this, AboutActivity.class);
      break;

    case R.id.drawer_no_ads:
      drawerIntent = new Intent(this, NoAdsManifestActivity.class);
      break;

    default:
      clickHandled = false;
    }

    if(clickHandled) {
      drawerLayout.closeDrawer(drawerView);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if(drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState)
  {
    super.onPostCreate(savedInstanceState);
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  protected boolean closeDrawerIfOpened()
  {
    if(drawerLayout.isDrawerOpen(Gravity.START)) {
      drawerLayout.closeDrawer(Gravity.START);
      return true;
    }
    return false;
  }

  @SuppressWarnings("deprecation")
  private Intent rate(String url)
  {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
    int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
    if(Build.VERSION.SDK_INT >= 21) {
      flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
    }
    else {
      flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
    }
    intent.addFlags(flags);
    return intent;
  }
}
