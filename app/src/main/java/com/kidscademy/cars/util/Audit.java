package com.kidscademy.cars.util;

import com.kidscademy.cars.App;
import com.kidscademy.cars.model.PlayContext;
import com.kidscademy.util.AuditBase;

public final class Audit extends AuditBase
{
  private static enum Event
  {
    OPEN_CATALOG, PLAY_GAME, QUIZ_BRAND, QUIZ_COUNTRY, QUIZ_YEAR, VIEW_BALANCE, VIEW_SCORES_LIST, RESET_SCORE
  }

  public void openCatalog()
  {
    if(enabled) {
      send(Event.OPEN_CATALOG);
    }
  }

  public void playGame(String levelName)
  {
    if(enabled) {
      send(Event.PLAY_GAME, levelName);
    }
  }

  public void playQuiz(PlayContext playContext, int levelIndex)
  {
    if(enabled) {
      String levelName = App.storage().getLevel(levelIndex).getName();
      
      switch(playContext) {
      case BRAND_QUIZ:
        send(Event.QUIZ_BRAND, levelName);
        break;

      case COUNTRY_QUIZ:
        send(Event.QUIZ_COUNTRY, levelName);
        break;

      case YEAR_QUIZ:
        send(Event.QUIZ_YEAR, levelName);
        break;
        
      default:
        break;
      }
    }
  }

  public void viewBalance()
  {
    if(enabled) {
      send(Event.VIEW_BALANCE);
    }
  }

  public void viewScoresList()
  {
    if(enabled) {
      send(Event.VIEW_SCORES_LIST);
    }
  }

  public void resetScore()
  {
    if(enabled) {
      send(Event.RESET_SCORE);
    }
  }
}
