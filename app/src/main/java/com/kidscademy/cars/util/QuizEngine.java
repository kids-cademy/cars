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

public class QuizEngine
{
  private static final Log log = LogFactory.getLog(QuizEngine.class);

  private static final int MAX_TRIES = 3;

  private PlayContext playContext;
  private LevelState levelState;
  private Counters counters;
  private Balance balance;
  private int leftTries;
  private Brand challengedBrand;
  private int challengedBrandIndex;
  private OptionHandler optionHandler;
  private int collectedCredits;

  public QuizEngine(PlayContext playContext, int levelIndex)
  {
    log.trace("QuizEngine(QuizMode, int)");
    this.playContext = playContext;
    this.levelState = App.storage().getLevelState(playContext, levelIndex);
    this.counters = App.storage().getCounters();
    this.balance = App.storage().getBalance();

    switch(playContext) {
    case BRAND_QUIZ:
      optionHandler = new BrandOption();
      break;

    case COUNTRY_QUIZ:
      optionHandler = new CountryOption();
      break;

    case YEAR_QUIZ:
      optionHandler = new YearOption();
      break;

    default:
      break;
    }

    leftTries = MAX_TRIES;
  }

  public Brand nextChallenge()
  {
    final List<Brand> brands = levelState.getBrands();
    if(challengedBrandIndex == brands.size()) {
      balance.plusCredit(collectedCredits);
      // TODO: hack, see method comment
      levelState.forceComplete();
      return null;
    }
    challengedBrand = brands.get(challengedBrandIndex++);
    return challengedBrand;
  }

  /**
   * This method assume it is called after {@link #nextChallenge()} that proper initialize {@link #challengedBrand}
   * instance field.
   * 
   * @param optionsCount
   * @return
   */
  public List<String> getOptions(int optionsCount)
  {
    // options list contains both positive and negative options
    // first takes care to add expected, that is challenge car
    List<String> options = new ArrayList<String>();
    options.add(optionHandler.getOption(challengedBrand));

    // create negative options list with all car options less expected car
    // takes care to not include a similar option many time; e.g. many cars can have the same country
    List<String> negativeOptions = new ArrayList<String>();
    for(Brand brand : App.storage().getBrands()) {
      final String option = optionHandler.getOption(brand);
      if(options.contains(option)) {
        continue;
      }
      if(negativeOptions.contains(option)) {
        continue;
      }
      negativeOptions.add(option);
    }
    Collections.shuffle(negativeOptions);

    // add negative options till options list is full, i.e. reaches requested options counter argument
    options.addAll(negativeOptions.subList(0, optionsCount - 1));

    Collections.shuffle(options);
    return options;
  }

  public boolean checkAnswer(String selectedOption)
  {
    if(selectedOption.equals(optionHandler.getOption(challengedBrand))) {
      collectedCredits += (Balance.getQuizDifficultyFactor(playContext) * Balance.getQuizIncrement(levelState.getIndex()));
      counters.plus(challengedBrand);
      return true;
    }
    counters.minus(challengedBrand);
    --leftTries;
    return false;
  }

  public int getLeftTries()
  {
    return leftTries;
  }

  public boolean noMoreTires()
  {
    return leftTries <= 0;
  }

  public int getCollectedCredits()
  {
    return collectedCredits;
  }

  public int getBrandIndex()
  {
    return challengedBrandIndex;
  }

  public int getBrandsCount()
  {
    return levelState.getBrandsCount();
  }

  private static interface OptionHandler
  {
    String getOption(Brand brand);
  }

  private static class BrandOption implements OptionHandler
  {
    @Override
    public String getOption(Brand brand)
    {
      return brand.getDisplay();
    }
  }

  private static class CountryOption implements OptionHandler
  {
    @Override
    public String getOption(Brand brand)
    {
      return brand.getCountryName();
    }
  }

  private static class YearOption implements OptionHandler
  {
    @Override
    public String getOption(Brand brand)
    {
      return Integer.toString(brand.getFoundationYear());
    }
  }
}
