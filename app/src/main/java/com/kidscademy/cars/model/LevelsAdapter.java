package com.kidscademy.cars.model;

import js.util.BitmapLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kidscademy.cars.App;
import com.kidscademy.cars.R;

public class LevelsAdapter extends ArrayAdapter<Level>
{
  public LevelsAdapter(Context context, Level[] list)
  {
    super(context, 0, list);
  }

  public View getView(int position, View convertView, ViewGroup parent)
  {
    Holder holder = null;

    if(convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_level, parent, false);

      holder = new Holder();
      convertView.setTag(holder);

      holder.imageView = (ImageView)convertView.findViewById(R.id.level_image);
      holder.nameView = (TextView)convertView.findViewById(R.id.level_name);
      holder.brandsCountView = (TextView)convertView.findViewById(R.id.level_total);
      holder.resolvedBrandsView = (TextView)convertView.findViewById(R.id.level_solved);
      holder.progressView = (ProgressBar)convertView.findViewById(R.id.level_progress);
      holder.maskView = convertView.findViewById(R.id.level_lock_mask);
    }
    else {
      holder = (Holder)convertView.getTag();
    }

    final Level level = getItem(position);
    final PlayContext playContext = App.prefs().isKidsMode() ? PlayContext.KIDS_GAME : PlayContext.GAME;
    final LevelState levelState = App.storage().getLevelState(playContext, level.getIndex());

    final int brandsCount = level.getBrandsCount();
    final int solvedBrands = levelState.getSolvedBrandsCount();
    final int progress = 100 * solvedBrands / brandsCount;

    final String imageAssetPath = levelState.isUnlocked() ? level.getImageAssetPath() : level.getDimImageAssetPath();
    BitmapLoader loader = new BitmapLoader(getContext(), imageAssetPath, holder.imageView, 2);
    loader.start();

    holder.nameView.setText(level.getName());
    holder.brandsCountView.setText(Integer.toString(brandsCount));
    holder.resolvedBrandsView.setText(Integer.toString(solvedBrands));
    holder.progressView.setProgress(progress);
    holder.maskView.setVisibility(levelState.isUnlocked() ? View.INVISIBLE : View.VISIBLE);

    return convertView;
  }

  private static class Holder
  {
    ImageView imageView;
    TextView nameView;
    TextView brandsCountView;
    TextView resolvedBrandsView;
    ProgressBar progressView;
    View maskView;
  }
}
