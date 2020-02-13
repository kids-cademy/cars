package com.kidscademy.cars.model;

import java.util.List;

import js.log.Log;
import js.log.LogFactory;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.kidscademy.cars.CatalogFragment;

public class CatalogAdapter extends FragmentStatePagerAdapter
{
  private static final Log log = LogFactory.getLog(CatalogAdapter.class);

  private List<Brand> brands;
  private SparseArray<CatalogFragment> fragments;

  public CatalogAdapter(FragmentManager fragmentManager, List<Brand> brands)
  {
    super(fragmentManager);
    this.brands = brands;
    this.fragments = new SparseArray<CatalogFragment>();
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position)
  {
    log.debug("Instantiate fragment for position |%d|.", position);
    fragments.put(position, CatalogFragment.newInstance(brands.get(position), brands.size(), position));
    return super.instantiateItem(container, position);
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object)
  {
    log.debug("Destroy fragment for position |%d|.", position);
    super.destroyItem(container, position, object);
    fragments.remove(position);
  }

  @Override
  public CatalogFragment getItem(int position)
  {
    return fragments.get(position);
  }

  @Override
  public int getItemPosition(Object object)
  {
    for(int i = 0; i < brands.size(); ++i) {
      if(brands.get(i).equals(object)) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public int getCount()
  {
    return brands.size();
  }
}
