package com.kidscademy.cars.model;

import js.util.BitmapLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.cars.App;
import com.kidscademy.cars.R;

public final class LevelBrandsAdapter extends ArrayAdapter<Brand>
{
  private PlayContext playContext;
  private Level level;

  public LevelBrandsAdapter(Context context, PlayContext playContext, Level level)
  {
    super(context, 0, level.getBrands());
    this.playContext = playContext;
    this.level = level;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    if(convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_level_brands, parent, false);
    }
    Brand brand = getItem(position);
    boolean catalogMode = playContext == PlayContext.CATALOG;
    boolean isSolvedBrand = level.getState(playContext).isSolvedBrand(brand);

    String logoAssetPath = App.prefs().isKidsMode() || catalogMode || isSolvedBrand ? brand.getLogoAssetPath() : brand.getDimLogoAssetPath();
    String display = catalogMode || isSolvedBrand ? brand.getDisplay() : null;

    ImageView iconView = (ImageView)convertView.findViewById(R.id.item_level_brands_icon);
    BitmapLoader loader = new BitmapLoader(getContext(), logoAssetPath, iconView);
    loader.start();

    TextView nameView = (TextView)convertView.findViewById(R.id.item_level_brands_name);
    nameView.setText(display);

    boolean showCheck = isSolvedBrand && !catalogMode;
    convertView.findViewById(R.id.item_level_brands_check).setVisibility(showCheck ? View.VISIBLE : View.INVISIBLE);

    return convertView;
  }
}