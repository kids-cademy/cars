package com.kidscademy.cars.model;

public enum PlayContext
{
  CATALOG, GAME, KIDS_GAME, BRAND_QUIZ, COUNTRY_QUIZ, YEAR_QUIZ;

  public static int size()
  {
    return values().length;
  }

  public Object display()
  {
    switch(this) {
    case BRAND_QUIZ:
      return "BRAND";

    case COUNTRY_QUIZ:
      return "COUNTRY";

    case YEAR_QUIZ:
      return "YEAR";

    default:
      return null;
    }
  }
}
