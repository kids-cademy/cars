package com.kidscademy.cars;

import com.kidscademy.app.FeaturesActivityBase;

public class FeaturesActivity extends FeaturesActivityBase
{
  private static final int[] SLIDES = new int[]
  {
      R.layout.slide_game, //
      R.layout.slide_quiz, //
      R.layout.slide_kids_game, //
      R.layout.slide_catalog, //
      R.layout.slide_settings, //
      R.layout.slide_support
  };

  @Override
  protected int[] slides()
  {
    return SLIDES;
  }
}
