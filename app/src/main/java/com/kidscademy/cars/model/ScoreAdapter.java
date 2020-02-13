package com.kidscademy.cars.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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

public class ScoreAdapter extends ArrayAdapter<Brand>
{
  private Counters counters;

  public ScoreAdapter(Context context, Brand[] cars)
  {
    // make a copy of the palette argument in order to avoid changing it by sort method
    super(context, 0, new ArrayList<Brand>(Arrays.asList(cars)));
    counters = App.storage().getCounters();
  }

  public void sort(final OrderBy orderBy)
  {
    sort(new Comparator<Brand>()
    {
      private Double score(Brand Brand)
      {
        return counters.getScore(Brand);
      }

      @Override
      public int compare(Brand leftSound, Brand rightSound)
      {
        switch(orderBy) {
        case SCORE_ASC:
          return score(leftSound).compareTo(score(rightSound));

        case SCORE_DESC:
          return score(rightSound).compareTo(score(leftSound));

        case NAME_ASC:
          return leftSound.getName().compareTo(rightSound.getName());

        case NAME_DESC:
          return rightSound.getName().compareTo(leftSound.getName());
        }

        return 0;
      }
    });
  }

  public View getView(int position, View convertView, ViewGroup parent)
  {
    if(convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_score, parent, false);
    }

    Brand brand = getItem(position);

    TextView scoreView = (TextView)convertView.findViewById(R.id.item_score_value);
    scoreView.setText(String.format("%05.1f", 100 * counters.getScore(brand)));

    ImageView imageView = (ImageView)convertView.findViewById(R.id.item_score_image);
    BitmapLoader loader = new BitmapLoader(getContext(), brand.getLogoAssetPath(), imageView, 2);
    loader.start();

    TextView nameView = (TextView)convertView.findViewById(R.id.item_score_name);
    nameView.setText(brand.getDisplay());

    return convertView;
  }
}
