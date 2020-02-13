package com.kidscademy.cars.model;

import java.util.List;

import js.lang.BugError;

import com.kidscademy.cars.App;

public class Level
{
  private int index;
  private String name;
  private List<Brand> brands;

  public Level()
  {
  }

  public Level(int index, List<Brand> brands)
  {
    this();
    this.index = index;
    this.name = "Level " + (index + 1);
    this.brands = brands;
  }

  public int getIndex()
  {
    return index;
  }

  public String getName()
  {
    return name;
  }

  public String getImageAssetPath()
  {
    return brands.get(0).getLogoAssetPath();
  }

  public String getDimImageAssetPath()
  {
    return brands.get(0).getDimLogoAssetPath();
  }

  public List<Brand> getBrands()
  {
    return brands;
  }

  public int getBrandsCount()
  {
    return brands.size();
  }

  public LevelState getState(PlayContext playContext)
  {
    return App.storage().getLevelState(playContext, index);
  }

  public static int getTotalLevels()
  {
    return App.storage().getLevels().length;
  }

  public static int getUnlockedLevels(PlayContext playContext)
  {
    int unlockedLevels = 0;
    for(LevelState levelState : App.storage().getLevelStates(playContext)) {
      if(levelState.isUnlocked()) {
        ++unlockedLevels;
      }
    }
    return unlockedLevels;
  }

  public static int getCompletedLevels(PlayContext playContext)
  {
    int completedLevels = 0;
    for(LevelState levelState : App.storage().getLevelStates(playContext)) {
      if(levelState.isComplete()) {
        ++completedLevels;
      }
    }
    return completedLevels;
  }

  public static int getFirstUncompletedLevelIndex(PlayContext playContext)
  {
    for(LevelState levelState : App.storage().getLevelStates(playContext)) {
      if(!levelState.isComplete()) {
        return levelState.getIndex();
      }
    }
    throw new BugError("Attempt to start quiz for completed play context |%s|.", playContext);
  }

  public static boolean areAllLevelsComplete(PlayContext playContext)
  {
    for(LevelState levelState : App.storage().getLevelStates(playContext)) {
      if(!levelState.isComplete()) {
        return false;
      }
    }
    return true;
  }

  public static int getTotalBrands()
  {
    int totalBrands = 0;
    for(Level level : App.storage().getLevels()) {
      totalBrands += level.getBrandsCount();
    }
    return totalBrands;
  }

  public static int getSolvedBrands(PlayContext playContext)
  {
    int solvedBrands = 0;
    for(LevelState level : App.storage().getLevelStates(playContext)) {
      solvedBrands += level.getSolvedBrandsCount();
    }
    return solvedBrands;
  }
}
