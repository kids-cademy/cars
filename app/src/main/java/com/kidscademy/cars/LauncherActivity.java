package com.kidscademy.cars;

import java.io.File;
import java.io.IOException;

import js.log.Log;
import js.log.LogFactory;
import android.os.Bundle;
import android.widget.TextView;

import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.app.LoadService;

public class LauncherActivity extends FullScreenActivity implements LoadService.Listener
{
  private static final Log log = LogFactory.getLog(LauncherActivity.class);

  public LauncherActivity()
  {
    log.trace("LauncherActivity()");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launcher);

    if(App.storage().isLoaded()) {
      MainActivity.start(this);
      finish();
      return;
    }

    LoadService.bind(this, this);
    LoadService.start(this);
  }

  @Override
  public void onLoadServiceConnected(LoadService service)
  {
    log.trace("onLoadServiceConnected(LoadService)");
  }

  @Override
  public void onLoadServiceProgress(File downloadedFile, int totalFiles, int processedFiles)
  {
    log.debug("Application loading progress: %d / %d", processedFiles, totalFiles);
  }

  @Override
  public void onLoadServiceDone()
  {
    log.trace("onLoadServiceDone()");
    LoadService.unbind(this, this);
    MainActivity.start(this);
    finish();
  }

  @Override
  public void onLoadServiceException(IOException exception)
  {
    log.trace("onLoadServiceException(IOException)");
    log.error(exception);
    LoadService.unbind(this, this);

    findViewById(R.id.launcher).setBackgroundResource(R.color.red_600);
    ((TextView)findViewById(R.id.launcher_text)).setText(getString(R.string.launcher_error));
  }

  @Override
  public void onBackPressed()
  {
    super.onBackPressed();
    this.finish();
  }
}
