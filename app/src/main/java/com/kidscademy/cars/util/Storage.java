package com.kidscademy.cars.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;
import android.content.Context;

import com.kidscademy.cars.model.Balance;
import com.kidscademy.cars.model.Brand;
import com.kidscademy.cars.model.Config;
import com.kidscademy.cars.model.Counters;
import com.kidscademy.cars.model.Level;
import com.kidscademy.cars.model.LevelState;
import com.kidscademy.cars.model.PlayContext;
import com.kidscademy.commons.Country;
import com.kidscademy.util.StorageBase;

public class Storage extends StorageBase
{
  /** Class logger. */
  private static final Log log = LogFactory.getLog(Storage.class);

  private static final String COUNTERS_PATH = "counters.json";
  private static final String BALANCE_PATH = "balance.json";
  private static final String LEVELS_PATH = "levels.json";
  private static final String LEVEL_STATES_PATH = "level-states.json";

  private Counters counters;
  private Balance balance;
  private Level[] levels;
  private LevelState[][] levelStates;

  public Storage(Context context)
  {
    super(context);
  }

  @Override
  public void onAppCreate()
  {
    log.trace("onAppCreate()");
    config = loadObject(getConfigFile(), Config.class);
    counters = loadObject(getCountersFile(), Counters.class);
    balance = loadObject(getBalanceFile(), Balance.class);

    if(getLevelsFile().exists()) {
      levels = loadObject(getLevelsFile(), Level[].class);
      levelStates = loadObject(getLevelStatesFile(), LevelState[][].class);
    }
    else {
      initLevels();
    }
  }

  @Override
  public void onAppClose() throws Exception
  {
    super.onAppClose();
    saveObject(counters, getCountersFile());
    saveObject(balance, getBalanceFile());
    // are levels instances changed? really need to save
    saveObject(levels, getLevelsFile());
    saveObject(levelStates, getLevelStatesFile());
  }

  public Counters getCounters()
  {
    return counters;
  }

  public Balance getBalance()
  {
    return balance;
  }

  public Brand[] getBrands()
  {
    return BRANDS;
  }

  public List<Brand> getBrands(int levelIndex)
  {
    return levels[levelIndex].getBrands();
  }

  public int getBrandPosition(String brandName)
  {
    Brand[] brands = getBrands();
    for(int i = 0; i < brands.length; ++i) {
      if(brands[i].getName().equals(brandName)) {
        return i;
      }
    }
    return 0;
  }

  public Level[] getLevels()
  {
    return levels;
  }

  public Level getLevel(int index)
  {
    return levels[index];
  }

  public void resetLevels()
  {
    initLevels();
    try {
      saveObject(counters, getCountersFile());
      saveObject(levels, getLevelsFile());
      saveObject(balance, getBalanceFile());
      saveObject(levelStates, getLevelStatesFile());
    }
    catch(Exception e) {
      log.error(e);
    }
  }

  /**
   * Returns the state of the level after current level or null if current level is last.
   * 
   * @param currentIndex current level index.
   * @return next level or null.
   */
  public LevelState getNextLevel(PlayContext playContext, int currentIndex)
  {
    LevelState[] playContextLevelStates = levelStates[playContext.ordinal()];
    return currentIndex < (playContextLevelStates.length - 1) ? playContextLevelStates[currentIndex + 1] : null;
  }

  public LevelState[] getLevelStates(PlayContext playContext)
  {
    return levelStates[playContext.ordinal()];
  }

  public LevelState getLevelState(PlayContext playContext, int levelIndex)
  {
    return levelStates[playContext.ordinal()][levelIndex];
  }

  private File getCountersFile()
  {
    return new File(directory, COUNTERS_PATH);
  }

  private File getBalanceFile()
  {
    return new File(directory, BALANCE_PATH);
  }

  private File getLevelsFile()
  {
    return new File(directory, LEVELS_PATH);
  }

  private File getLevelStatesFile()
  {
    return new File(directory, LEVEL_STATES_PATH);
  }

  // ------------------------------------------------------

  // last argument is rank obtained via google search in K hits; search string is brand name + space + 'car'
  private static final Brand[] BRANDS = new Brand[]
  {
      new Brand("abarth", Country.IT, 1949, 25900), //
      new Brand("ac_cars", Country.GB, 1901, 67000), //
      new Brand("acura", Country.JP, 1986, 60900), //
      new Brand("agrale", Country.BR, 1962, 639), //
      new Brand("aixam", Country.FR, 1983, 477), //
      new Brand("alfa_romeo", Country.IT, 1910, 31200), //
      new Brand("alpina", Country.DE, 1965, 4440), //
      new Brand("apollo", Country.DE, 2004, 31300), //
      new Brand("aptera", Country.US, 2005, 2011, 257), //
      new Brand("ariel", Country.GB, 2001, 27100), //
      new Brand("aro", Country.RO, 1957, 2006, 6270), //
      new Brand("artega", Country.DE, 2006, 2012, 526), //
      new Brand("ascari", Country.GB, 1995, 478), //
      new Brand("ashok_leyland", Country.IN, 1948, 3530), //
      new Brand("aston_martin", Country.GB, 1913, 30400), //
      new Brand("audi", Country.DE, 1910, 95300), //
      new Brand("autobianchi", Country.IT, 1955, 1995, 510), //
      new Brand("baic", Country.CN, 1958, 404), //
      new Brand("bentley", Country.GB, 1919, 44300), //
      new Brand("bertone", Country.IT, 1912, 2014, 445), //
      new Brand("bmw", Country.DE, 1916, 128000), //
      new Brand("borgward", Country.DE, 1929, 405), //
      new Brand("brilliance", Country.CN, 1991, 1500), //
      new Brand("bristol", Country.GB, 1945, 56600), //
      new Brand("british_leyland", Country.GB, 1968, 1986, 1230), //
      new Brand("brp", Country.CA, 1942, 470), //
      new Brand("bugatti", Country.FR, 1909, 1963, 1120),//
      new Brand("buick", Country.US, 1903, 104500), //
      new Brand("cadillac", Country.US, 1902, 94800), //
      new Brand("carbon", Country.US, 2003, 2013, 16100), //
      new Brand("changan", Country.CN, 1862, 528), //
      new Brand("chery", Country.CN, 1997, 552), //
      new Brand("chevrolet", Country.US, 1911, 152000), //
      new Brand("chrysler", Country.US, 1925, 83000), //
      new Brand("citroen", Country.FR, 1919, 32100), //
      new Brand("cizeta", Country.IT, 1990, 317), //
      new Brand("coda", Country.US, 2009, 10600), //
      new Brand("corvette", Country.US, 1953, 39000), //
      new Brand("dacia", Country.RO, 1966, 26600), //
      new Brand("dadi", Country.CN, 1988, 555), //
      new Brand("daewoo", Country.KR, 1937, 2002, 24400), //
      new Brand("daihatsu", Country.JP, 1907, 17100), //
      new Brand("datsun", Country.JP, 1931, 19200), //
      new Brand("de_tomaso", Country.IT, 1959, 2004, 13800), //
      new Brand("dodge", Country.US, 1900, 119000), //
      new Brand("dongfeng", Country.CN, 1969, 385), //
      new Brand("donkervoort", Country.NL, 1978, 593), //
      new Brand("ds", Country.FR, 2009, 83300), //
      new Brand("eagle", Country.GB, 1981, 1998, 6300), //
      new Brand("elfin", Country.AU, 1957, 499), //
      new Brand("elva", Country.GB, 1955, 1968, 495), //
      new Brand("fap", Country.RS, 1952, 495), //
      new Brand("faw", Country.CN, 1953, 628), //
      new Brand("ferrari", Country.IT, 1939, 54100), //
      new Brand("fiat", Country.IT, 1899, 59500), //
      new Brand("fisker", Country.US, 2007, 2013, 16400), //
      new Brand("ford_mustang", Country.US, 1965, 52700), //
      new Brand("ford", Country.US, 1903, 163000), //
      new Brand("foton", Country.CN, 1996, 461), //
      new Brand("fso", Country.PL, 1951, 2011, 441), //
      new Brand("gaz", Country.RU, 1932, 44000), //
      new Brand("geely", Country.CN, 1986, 578), //
      new Brand("gem", Country.US, 1998, 25600), //
      new Brand("gillet", Country.BE, 1992, 455), //
      new Brand("ginetta", Country.GB, 1958, 436), //
      new Brand("great_wall", Country.CN, 1984, 10300), //
      new Brand("hafei", Country.CN, 1950, 433), //
      new Brand("haima", Country.CN, 1992, 493), //
      new Brand("hillman", Country.GB, 1907, 1931, 730), //
      new Brand("hino", Country.JP, 1942, 481), //
      new Brand("holden", Country.AU, 1856, 29400), //
      new Brand("hommell", Country.FR, 1990, 2003, 168), //
      new Brand("honda", Country.JP, 1948, 141000), //
      new Brand("hongqi", Country.CN, 1958, 395), //
      new Brand("hyundai", Country.KR, 1967, 107000), //
      new Brand("ikco", Country.IR, 1962, 539), //
      new Brand("infiniti", Country.JP, 1989, 57200), //
      new Brand("innocenti", Country.IT, 1947, 1997, 461), //
      new Brand("isdera", Country.DE, 1969, 1790), //
      new Brand("isuzu", Country.JP, 1934, 30500), //
      new Brand("iveco", Country.IT, 1975, 5530), //
      new Brand("jac", Country.CN, 1964, 9630), //
      new Brand("jaguar", Country.GB, 1922, 82100), //
      new Brand("jmc", Country.CN, 1952, 426), //
      new Brand("kamaz", Country.RU, 1969, 393), //
      new Brand("kenworth", Country.US, 1923, 1220), //
      new Brand("kia", Country.KR, 1944, 104000), //
      new Brand("koenigsegg", Country.SE, 1994, 1790), //
      new Brand("lada", Country.RU, 1966, 15700), //
      new Brand("lagonda", Country.GB, 1906, 399), //
      new Brand("lamborghini", Country.IT, 1963, 32100), //
      new Brand("lancia", Country.IT, 1906, 26800), //
      new Brand("land_rover", Country.GB, 1948, 48600), //
      new Brand("landwind", Country.CN, 2004, 610), //
      new Brand("laraki", Country.MA, 1999, 255), //
      new Brand("ldv", Country.GB, 1993, 2009, 458), //
      new Brand("lexus", Country.JP, 1989, 70600), //
      new Brand("ligier", Country.FR, 1968, 424), //
      new Brand("lincoln", Country.US, 1917, 108000), //
      new Brand("lotus", Country.GB, 1952, 1830), //
      new Brand("luxgen", Country.TW, 2009, 631), //
      new Brand("mack", Country.US, 1900, 17400), //
      new Brand("mahindra", Country.IN, 1945, 6810), //
      new Brand("man", Country.DE, 1915, 62000), //
      new Brand("marcos", Country.GB, 1959, 2007, 30900), //
      new Brand("marussia", Country.RU, 2007, 2014, 656), //
      new Brand("maserati", Country.IT, 1914, 38700), //
      new Brand("mastretta", Country.MX, 1987, 367), //
      new Brand("maybach", Country.DE, 1909, 22000), //
      new Brand("mazda", Country.JP, 1920, 84600), //
      new Brand("mclaren", Country.GB, 1963, 30300), //
      new Brand("melkus", Country.DE, 1959, 2012, 386), //
      new Brand("mercedes_benz", Country.DE, 1926, 83000), //
      new Brand("mercury", Country.US, 1938, 2011, 32600), //
      new Brand("mg_motor", Country.GB, 2006, 7910), //
      new Brand("microcar", Country.FR, 1984, 857), //
      new Brand("mini", Country.GB, 1959, 2000, 2200), //
      new Brand("mitsubishi", Country.JP, 1970, 61100), //
      new Brand("morgan", Country.GB, 1910, 68600), //
      new Brand("morris", Country.GB, 1919, 1984, 8600), //
      new Brand("mosler", Country.US, 1985, 2013, 507), //
      new Brand("nash", Country.US, 1916, 1954, 29400), //
      new Brand("naza", Country.MY, 1975, 397), //
      new Brand("nissan", Country.JP, 1933, 136000), //
      new Brand("oldsmobile", Country.US, 1897, 2004, 37300), //
      new Brand("oltcit", Country.RO, 1976, 1991, 733), //
      new Brand("opel", Country.DE, 1863, 31600), //
      new Brand("pagani", Country.IT, 1992, 15600), //
      new Brand("panoz", Country.US, 1989, 13500), //
      new Brand("pars_khodro", Country.IR, 1967, 82), //
      new Brand("perodua", Country.MY, 1993, 1140), //
      new Brand("peugeot", Country.FR, 1882, 37500), //
      new Brand("pgo", Country.FR, 1985, 441), //
      new Brand("piaggio", Country.IT, 1884, 584), //
      new Brand("pininfarina", Country.IT, 1930, 542), //
      new Brand("plymouth", Country.US, 1928, 2001, 37600), //
      new Brand("polaris", Country.US, 1954, 9010), //
      new Brand("pontiac", Country.US, 1926, 2010, 53800), //
      new Brand("porsche", Country.DE, 1931, 58800), //
      new Brand("premier", Country.IN, 1941, 2520), //
      new Brand("proton", Country.MY, 1983, 15600), //
      new Brand("puch", Country.AT, 1899, 425), //
      new Brand("renault_samsung", Country.KR, 1994, 1430), //
      new Brand("rimac", Country.HR, 2009, 416), //
      new Brand("renault", Country.FR, 1899, 46600), //
      new Brand("rolls_royce", Country.GB, 1906, 28900), //
      new Brand("rossion", Country.US, 2008, 210), //
      new Brand("rover", Country.GB, 1986, 2005, 58200), //
      new Brand("saab", Country.SE, 1945, 2012, 35300), //
      new Brand("saic", Country.CN, 2011, 391), //
      new Brand("saipa", Country.IR, 1966, 451), //
      new Brand("saleen", Country.US, 1983, 49600), //
      new Brand("saturn", Country.US, 1985, 2010, 9700), //
      new Brand("scania", Country.SE, 1911, 531), //
      new Brand("scion", Country.US, 2003, 2016, 49000), //
      new Brand("seat", Country.ES, 1950, 289000), //
      new Brand("shelby", Country.US, 1965, 32400), //
      new Brand("sisu", Country.FI, 1931, 411), //
      new Brand("skoda", Country.CZ, 1895, 30600), //
      new Brand("smart", Country.DE, 1994, 20200), //
      new Brand("spyker", Country.NL, 1999, 179), //
      new Brand("ssangyong", Country.KR, 1954, 25700), //
      new Brand("ssc", Country.US, 1999, 8520), //
      new Brand("studebaker", Country.US, 1852, 1967, 14900), //
      new Brand("subaru", Country.JP, 1953, 84100), //
      new Brand("suzuki", Country.JP, 1909, 53400), //
      new Brand("talbot", Country.GB, 1903, 1994, 17), //
      new Brand("tata", Country.IN, 1945, 1670), //
      new Brand("tatra", Country.CZ, 1897, 426), //
      new Brand("tauro", Country.ES, 2010, 515), //
      new Brand("temsa", Country.TR, 1968, 475), //
      new Brand("tesla", Country.US, 2003, 4400), //
      new Brand("thai_rung", Country.TH, 1967, 133), //
      new Brand("toyota", Country.JP, 1937, 175000), //
      new Brand("trabant", Country.DE, 1957, 1991, 9180), //
      new Brand("tramontana", Country.ES, 2007, 507), //
      new Brand("triumph", Country.GB, 1885, 1984, 7200), //
      new Brand("troller", Country.BR, 1995, 434), //
      new Brand("tvs", Country.IN, 1978, 16600), //
      new Brand("uaz", Country.RU, 1941, 529), //
      new Brand("ud", Country.JP, 1950, 23700), //
      new Brand("unimog", Country.DE, 1946, 419), //
      new Brand("vauxhall", Country.GB, 1857, 30300), //
      new Brand("vector", Country.US, 1971, 693), //
      new Brand("venturi", Country.MC, 1984, 460), //
      new Brand("veritas", Country.DE, 1947, 1952, 636), //
      new Brand("volkswagen", Country.DE, 1937, 95500), //
      new Brand("volvo", Country.SE, 1927, 7390), //
      new Brand("w_motors", Country.AE, 2012, 3130), //
      new Brand("wartburg", Country.DE, 1898, 1991, 581), //
      new Brand("western_star", Country.US, 1967, 8450), //
      new Brand("westfield", Country.GB, 1982, 17100), //
      new Brand("wiesmann", Country.DE, 1988, 2014, 439), //
      new Brand("yamaha", Country.JP, 1887, 20400), //
      new Brand("yulon", Country.TW, 1953, 703), //
      new Brand("zagato", Country.IT, 1919, 447), //
      new Brand("zastava", Country.RS, 1953, 2008, 395), //
      new Brand("zaz", Country.UA, 1923, 409), //
      new Brand("zenvo", Country.DK, 2004, 1150), //
      new Brand("zil", Country.RU, 1916, 404)
  };

  private void initLevels()
  {
    // TODO: hard coded levels count; keep in sync with BRANDS collection
    final int LEVELS_COUNT = 20;

    levels = new Level[LEVELS_COUNT];
    final int BRANDS_PER_LEVEL = BRANDS.length / levels.length;

    List<Brand> brands = new ArrayList<Brand>(Arrays.asList(BRANDS));
    Collections.sort(brands, new Comparator<Brand>()
    {
      @Override
      public int compare(Brand lhs, Brand rhs)
      {
        // put first right hand element for descendant sort
        return ((Integer)rhs.getRank()).compareTo(lhs.getRank());
      }
    });

    Iterator<Brand> iterator = brands.iterator();
    for(int levelIndex = 0; levelIndex < levels.length; ++levelIndex) {

      final List<Brand> levelBrands = new ArrayList<Brand>(BRANDS_PER_LEVEL);
      for(int brandIndex = 0; brandIndex < BRANDS_PER_LEVEL; ++brandIndex) {
        assert iterator.hasNext();
        levelBrands.add(iterator.next());
      }

      levels[levelIndex] = new Level(levelIndex, levelBrands);
    }

    Flags.setCurrentLevel(0);

    levelStates = new LevelState[PlayContext.size()][];
    for(int playContextIndex = 0; playContextIndex < levelStates.length; ++playContextIndex) {
      LevelState[] playContextLevelStates = new LevelState[levels.length];
      levelStates[playContextIndex] = playContextLevelStates;

      playContextLevelStates[0] = new LevelState(0, BRANDS_PER_LEVEL, true);
      for(int levelIndex = 1; levelIndex < playContextLevelStates.length; ++levelIndex) {
        playContextLevelStates[levelIndex] = new LevelState(levelIndex, BRANDS_PER_LEVEL);
      }
    }
  }
}
