package com.kidscademy.cars.model;

import java.util.HashMap;
import java.util.Map;

public class Counters
{
  private Map<Brand, Value> values = new HashMap<Brand, Value>();

  public void plus(Brand brand)
  {
    value(brand).plus();
  }

  public void minus(Brand brand)
  {
    value(brand).minus();
  }

  public double getScore(Brand brand)
  {
    return value(brand).getScore();
  }

  public void reset()
  {
    values.clear();
  }

  private Value value(Brand brand)
  {
    Value value = values.get(brand);
    if(value == null) {
      value = new Value();
      values.put(brand, value);
    }
    return value;
  }

  public static class Value implements Comparable<Value>
  {
    private int positive;
    private int negative;

    public void plus()
    {
      ++positive;
    }

    public void minus()
    {
      ++negative;
    }

    public double getScore()
    {
      final double total = positive + negative;
      return total != 0 ? positive / total : 0;
    }

    @Override
    public int compareTo(Value other)
    {
      return ((Double)this.getScore()).compareTo(other.getScore());
    }
  }
}
