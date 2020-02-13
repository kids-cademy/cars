package com.kidscademy.cars.model;

import java.util.ArrayList;
import java.util.List;

import com.kidscademy.cars.App;

public class LevelState
{
  private static final float UNLOCK_TRESHOLD = 0.5F;

  private int index;
  private int totalBrandsCount;
  private List<Brand> solvedBrands;
  private boolean unlocked;

  public LevelState()
  {
  }

  public LevelState(int index, int brandsCount)
  {
    this.index = index;
    this.totalBrandsCount = brandsCount;
    this.solvedBrands = new ArrayList<Brand>(brandsCount);
  }

  public LevelState(int index, int brandsCount, boolean unlocked)
  {
    this(index, brandsCount);
    this.unlocked = unlocked;
  }

  public int getIndex()
  {
    return index;
  }

  public void solveBrand(Brand brand)
  {
    solvedBrands.add(brand);
  }

  public void reset()
  {
    solvedBrands.clear();
    unlocked = false;
  }

  public List<Brand> getBrands()
  {
    return App.storage().getBrands(index);
  }

  public int getBrandsCount()
  {
    return getBrands().size();
  }

  public List<Brand> getUnsolvedBrands()
  {
    List<Brand> unsolvedBrands = new ArrayList<Brand>(App.storage().getBrands(index));
    unsolvedBrands.removeAll(solvedBrands);
    return unsolvedBrands;
  }

  public boolean isUnlockThreshold()
  {
    return (float)solvedBrands.size() / (float)totalBrandsCount > UNLOCK_TRESHOLD;
  }

  public void unlock()
  {
    unlocked = true;
  }

  public boolean isUnlocked()
  {
    return unlocked;
  }

  public boolean isComplete()
  {
    return solvedBrands.size() == totalBrandsCount;
  }

  public void forceComplete()
  {
    // TODO: hack currently used by quiz
    // a quiz can complete even with errors meaning that after quiz pass there are still not known brands
    // for now this condition is simply ignored; maybe to add logic to re-play the quiz
    // meanwhile consider quiz complete by initializing solved brands with all level brands list
    solvedBrands = getBrands();
  }

  public int getSolvedBrandsCount()
  {
    return solvedBrands.size();
  }

  public boolean isSolvedBrand(Brand brand)
  {
    return solvedBrands.contains(brand);
  }
}
