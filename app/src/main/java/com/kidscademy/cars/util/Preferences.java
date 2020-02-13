package com.kidscademy.cars.util;

import com.kidscademy.cars.App;
import com.kidscademy.cars.R;
import com.kidscademy.util.PreferencesBase;

public final class Preferences extends PreferencesBase
{
  public boolean isKidsMode()
  {
    return getBoolean(App.context(), R.string.pref_kids_mode_key, false);
  }

  public boolean isOfficialCountryName()
  {
    return getBoolean(App.context(), R.string.pref_official_country_key, false);
  }

  public boolean isKeyVibrator()
  {
    return getBoolean(App.context(), R.string.pref_key_vibrator_key, false);
  }
}
