package com.kidscademy.cars.model;

import js.lang.BugError;

import com.kidscademy.cars.App;

public class Balance
{
  private static final int SCORE_INCREMENT_BASE = 10;
  private static final int SCORE_PENALTY = 1;
  private static final int SCORE_LEVEL_UNLOCK_BONUS = 50;
  private static final int SCORE_LEVEL_COMPLETE_BONUS = 100;

  private static final int QUIZ_INCREMENT_BASE = 3;

  // keep in sync minimum credit with lowest deduction value
  private static final int MINIMUM_CREDIT = 10;
  private static final int REVEAL_LETTER_DEDUCTION = 10;
  private static final int VERIFY_INPUT_DEDUCTION = 20;
  private static final int HIDE_LETTERS_DEDUCTION = 30;
  private static final int SAY_NAME_DEDUCTION = 40;

  public static int getScoreIncrement(int levelIndex)
  {
    return SCORE_INCREMENT_BASE + levelIndex;
  }

  public static int getScoreLevelCompleteBonus(int levelIndex)
  {
    return SCORE_LEVEL_COMPLETE_BONUS;
  }

  public static int getScoreLevelUnlockBonus(int levelIndex)
  {
    return SCORE_LEVEL_UNLOCK_BONUS;
  }

  public static int getScorePenalty()
  {
    return SCORE_PENALTY;
  }

  public static int getQuizDifficultyFactor(PlayContext playContext)
  {
    switch(playContext) {
    case BRAND_QUIZ:
      return 1;

    case COUNTRY_QUIZ:
      return 2;

    case YEAR_QUIZ:
      return 8;

    default:
      throw new BugError("Illegal quiz mode.");
    }
  }

  public static int getQuizIncrement(int levelIndex)
  {
    return QUIZ_INCREMENT_BASE;
  }

  public static int getRevealLetterDeduction()
  {
    return REVEAL_LETTER_DEDUCTION;
  }

  public static int getVerifyInputDeduction()
  {
    return VERIFY_INPUT_DEDUCTION;
  }

  public static int getHideLettersDeduction()
  {
    return HIDE_LETTERS_DEDUCTION;
  }

  public static int getSayNameDeduction()
  {
    return SAY_NAME_DEDUCTION;
  }

  private int score;
  private int kidsScore;
  private int credit;

  public void minusScore(int points)
  {
    if(App.prefs().isKidsMode()) {
      kidsScore -= points;
      if(kidsScore < 0) {
        kidsScore = 0;
      }
    }
    else {
      score -= points;
      if(score < 0) {
        score = 0;
      }
    }
  }

  public void plusScore(int points)
  {
    if(App.prefs().isKidsMode()) {
      kidsScore += points;
    }
    else {
      score += points;
    }
  }

  public int getScore()
  {
    return App.prefs().isKidsMode() ? kidsScore : score;
  }

  public void minusCredit(int units)
  {
    credit -= units;
    if(credit < 0) {
      credit = 0;
    }
  }

  public void plusCredit(int units)
  {
    credit += units;
  }

  public int getCredit()
  {
    return credit;
  }

  public boolean hasCredit()
  {
    return credit >= MINIMUM_CREDIT;
  }

  public boolean deductRevealLetter()
  {
    if(credit < REVEAL_LETTER_DEDUCTION) {
      return false;
    }
    credit -= REVEAL_LETTER_DEDUCTION;
    return true;
  }

  public boolean deductVerifyInput()
  {
    if(credit < VERIFY_INPUT_DEDUCTION) {
      return false;
    }
    credit -= VERIFY_INPUT_DEDUCTION;
    return true;
  }

  public boolean deductHideLettersInput()
  {
    if(credit < HIDE_LETTERS_DEDUCTION) {
      return false;
    }
    credit -= HIDE_LETTERS_DEDUCTION;
    return true;
  }

  public boolean deductSayName()
  {
    if(credit < SAY_NAME_DEDUCTION) {
      return false;
    }
    credit -= SAY_NAME_DEDUCTION;
    return true;
  }

  public void reset()
  {
    score = 0;
    kidsScore = 0;
    credit = 0;
  }
}
