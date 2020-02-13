package com.kidscademy.cars;

import com.kidscademy.app.AppBase;
import com.kidscademy.cars.util.Audit;
import com.kidscademy.cars.util.Preferences;
import com.kidscademy.cars.util.Repository;
import com.kidscademy.cars.util.Storage;

/**
 * Application singleton holds global states, generates crash report and implements application active detection logic.
 * <p>
 * <h5>Android Process Creation</h5>
 * <p>
 * An Android process is created when an activity should be activated; a process is just a run-time container for an
 * activity instance. The ultimate goal is to start an activity but before that Android creates application singleton
 * and invoke {@link #onCreate()}. After on create callback returns Android continue with activity creation. This is
 * true even if callback starts another activity; Android will still create activity requested by platform then will
 * create that requested by callback.
 * <p>
 * Now, which activity is created when application starts depends on Android platform and external applications. For
 * example, if application is started from home launcher main activity will be created; if application is recreated from
 * recent applications, platform will restore last active activity. Also an activity could be explicitly requested by a
 * third party application. This behavior can be adjusted by <code>launchMode</code> attribute from manifest or intent
 * flags.
 * <p>
 * To sum-up, there is no way to route application start-up to different activity. Platform chosen activity will be
 * created anyway, even if on create callback requests another activity start.
 * 
 * @author Iulian Rotaru
 */
public class App extends AppBase
{
  public static final String PROJECT_NAME = "cars";

  /**
   * Application instance creation. Application is guaranteed by Android platform to be created in a single instance.
   * <p>
   * Initialization occurs in two steps: first is this callback invoked by Android. The second is
   * {@link #onPostCreate()}; if storage is loaded post-create is invoked immediately by this method. If storage is not
   * loaded, {@link MainActivity} will route application start-up logic to storage loading asynchronous task that, when
   * done, will invoke post-create. This way post-create is guaranteed to be called when storage is loaded.
   */
  @Override
  public void onAppCreate()
  {
    preferences = new Preferences();
    repository = new Repository();
    storage = new Storage(getApplicationContext());
    audit = new Audit();

    if(storage().isValid() && storage().isLoaded()) {
      onPostCreate();
    }
  }

  public static App instance()
  {
    return (App)instance;
  }

  public static Preferences prefs()
  {
    return (Preferences)AppBase.prefs();
  }

  /**
   * Get application storage singleton instance.
   * 
   * @return application storage.
   * @see #storage
   */
  public static Storage storage()
  {
    return (Storage)AppBase.storage();
  }

  /**
   * Get application storage singleton instance.
   * 
   * @return application storage.
   * @see #storage
   */
  public static Repository repository()
  {
    return (Repository)AppBase.repository();
  }

  /**
   * Get audit singleton instance.
   * 
   * @return audit instance.
   * @see #audit
   */
  public static Audit audit()
  {
    return (Audit)AppBase.audit();
  }
}
