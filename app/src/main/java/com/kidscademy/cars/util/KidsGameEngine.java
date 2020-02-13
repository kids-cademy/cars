package com.kidscademy.cars.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

import com.kidscademy.cars.App;
import com.kidscademy.cars.model.Balance;
import com.kidscademy.cars.model.Brand;
import com.kidscademy.cars.model.Counters;
import com.kidscademy.cars.model.LevelState;
import com.kidscademy.cars.model.PlayContext;

public class KidsGameEngine
{
  private static final Log log = LogFactory.getLog(KidsGameEngine.class);

  private static final int NO_UNLOCK_LEVEL = -1;

  private LevelState levelState;

  /** Car brand currently displayed as challenge. */
  private Brand challengedBrand;

  /** Current challenged brand index, relative to level not solved brands collection. */
  private int challengedBrandIndex;

  /** If current level unlock threshold was reached this field contains the index of the next level. */
  private int unlockedLevelIndex = NO_UNLOCK_LEVEL;

  private Balance balance;

  private Counters counters;

  public KidsGameEngine(LevelState levelState)
  {
    log.trace("KidsGameEngine(LevelState)");
    this.levelState = levelState;
    this.balance = App.storage().getBalance();
    this.counters = App.storage().getCounters();
  }

  public Brand initChallenge(String brandName)
  {
    log.trace("initChallenge(String)");
    List<Brand> unsolvedBrands = levelState.getUnsolvedBrands();
    if(unsolvedBrands.isEmpty()) {
      return null;
    }
    if(brandName == null) {
      challengedBrand = unsolvedBrands.get(0);
      challengedBrandIndex = 1;
    }
    else {
      for(challengedBrandIndex = 0; challengedBrandIndex < unsolvedBrands.size();) {
        challengedBrand = unsolvedBrands.get(challengedBrandIndex++);
        if(challengedBrand.getName().equals(brandName)) {
          break;
        }
      }
    }
    return challengedBrand;
  }

  public Brand nextChallenge()
  {
    List<Brand> unsolvedBrands = levelState.getUnsolvedBrands();
    if(unsolvedBrands.isEmpty()) {
      balance.plusScore(Balance.getScoreLevelCompleteBonus(levelState.getIndex()));
      return null;
    }
    if(challengedBrandIndex >= unsolvedBrands.size() || challengedBrandIndex < 0) {
      challengedBrandIndex = 0;
    }
    challengedBrand = unsolvedBrands.get(challengedBrandIndex++);
    return challengedBrand;
  }

  public List<Brand> getBrandOptions(int optionsCount)
  {
    // brand options list contains both positive and negative options
    List<Brand> brandOptions = new ArrayList<Brand>();
    brandOptions.add(challengedBrand);

    // create negative options list with all brands less expected brand
    List<Brand> negativeOptions = new ArrayList<Brand>();
    for(Brand brand : App.storage().getBrands()) {
      if(!brand.equals(challengedBrand)) {
        negativeOptions.add(brand);
      }
    }
    Collections.shuffle(negativeOptions);

    // add negative options till options list is full, i.e. reaches requested options counter argument
    brandOptions.addAll(negativeOptions.subList(0, optionsCount - 1));

    Collections.shuffle(brandOptions);
    return brandOptions;
  }

  public boolean checkAnswer(String answer)
  {
    assert challengedBrand != null;

    if(!challengedBrand.getName().equals(answer)) {
      counters.minus(challengedBrand);
      balance.minusScore(Balance.getScorePenalty());
      return false;
    }

    counters.plus(challengedBrand);
    balance.plusScore(Balance.getScoreIncrement(levelState.getIndex()));
    levelState.solveBrand(challengedBrand);
    // decrement brand index to compensate for solved brand that will not be part of unsolved brands on next challenge
    --challengedBrandIndex;

    // logic to unlock next level
    // store next level index for unlocking only if next level is not already enabled

    LevelState nextLevel = App.storage().getNextLevel(PlayContext.KIDS_GAME, levelState.getIndex());
    if(nextLevel != null && !nextLevel.isUnlocked() && levelState.isUnlockThreshold()) {
      nextLevel.unlock();
      unlockedLevelIndex = nextLevel.getIndex();
      balance.plusScore(Balance.getScoreLevelUnlockBonus(levelState.getIndex()));
    }
    return true;
  }

  public boolean wasNextLevelUnlocked()
  {
    return unlockedLevelIndex != NO_UNLOCK_LEVEL;
  }

  /**
   * Get unlocked level index. This getter returns meaningful value only if {@link #wasNextLevelUnlocked()} return true;
   * otherwise behavior is not specified. Returned value is usable only once, that is, this method reset internal value
   * before returning.
   * 
   * @return unlocked level index.
   */
  public int getUnlockedLevelIndex()
  {
    assert unlockedLevelIndex != NO_UNLOCK_LEVEL;
    int i = unlockedLevelIndex;
    unlockedLevelIndex = NO_UNLOCK_LEVEL;
    return i;
  }

  public Balance getBalance()
  {
    return balance;
  }
}
