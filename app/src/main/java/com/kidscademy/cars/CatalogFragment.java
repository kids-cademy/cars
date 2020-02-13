package com.kidscademy.cars;

import js.util.BitmapLoader;
import js.log.Log;
import js.log.LogFactory;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.cars.model.Brand;
import com.kidscademy.util.Countries;

public class CatalogFragment extends Fragment implements OnClickListener
{
  private static final Log log = LogFactory.getLog(CatalogFragment.class);

  private static final String ARG_SOUND = "sound";
  private static final String ARG_TOTAL = "total";
  private static final String ARG_POSITION = "position";

  public static CatalogFragment newInstance(Brand brand, int totalSounds, int position)
  {
    log.trace("newInstance(Sound, int, int)");

    final Bundle arguments = new Bundle();
    arguments.putParcelable(ARG_SOUND, brand);
    arguments.putInt(ARG_TOTAL, totalSounds);
    arguments.putInt(ARG_POSITION, position);

    final CatalogFragment fragment = new CatalogFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  private Brand brand;
  private int position;
  private OnClickListener onClickListener;

  public Brand getSound()
  {
    if(brand == null) {
      brand = (Brand)getArguments().getParcelable(ARG_SOUND);
    }
    return brand;
  }

  public void onAttach(Context context)
  {
    super.onAttach(context);
    onClickListener = (OnClickListener)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    final Bundle arguments = getArguments();
    brand = (Brand)arguments.getParcelable(ARG_SOUND);
    log.trace("onCreate(Bundle) - %s", car());

    position = arguments.getInt(ARG_POSITION);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    log.trace("onCreateView(LayoutInflater, ViewGroup, Bundle) - position=%d", position);

    View view = inflater.inflate(R.layout.item_catalog, container, false);

    ImageView image = (ImageView)view.findViewById(R.id.item_catalog_image);
    image.setOnClickListener(this);
    BitmapLoader imageLoader = new BitmapLoader(getContext(), brand.getLogoAssetPath(), image);
    imageLoader.start();

    TextView name = (TextView)view.findViewById(R.id.item_catalog_name);
    name.setText(brand.getDisplay());

    TextView country = (TextView)view.findViewById(R.id.item_catalog_country);
    TextView foundationYear = (TextView)view.findViewById(R.id.item_catalog_foundation_year);
    TextView defunctYear = (TextView)view.findViewById(R.id.item_catalog_defunct_year);
    TextView yearSeparator = (TextView)view.findViewById(R.id.item_catalog_year_separator);
    ImageView flag = (ImageView)view.findViewById(R.id.item_catalog_flag);

    if(App.prefs().isKidsMode()) {
      foundationYear.setVisibility(View.GONE);
      defunctYear.setVisibility(View.GONE);
      yearSeparator.setVisibility(View.GONE);
      flag.setVisibility(View.GONE);
      country.setText(null);

      return view;
    }

    country.setText(brand.getCountryName());

    foundationYear.setText(Integer.toString(brand.getFoundationYear()));
    if(brand.isDefunct()) {
      defunctYear.setText(Integer.toString(brand.getDefunctYear()));
    }
    else {
      defunctYear.setVisibility(View.GONE);
      yearSeparator.setVisibility(View.GONE);
    }

    int flagResId = Countries.flag(brand.getCountry());
    if(flagResId != 0) {
      BitmapLoader flagLoader = new BitmapLoader(getContext().getResources(), flagResId, flag);
      flagLoader.start();
    }
    else {
      flag.setVisibility(View.GONE);
    }
    
    return view;
  }

  @Override
  public void onStart()
  {
    log.trace("onStart() - position=%d", position);
    super.onStart();
  }

  @Override
  public void onClick(View view)
  {
    onClickListener.onClick(view);
  }

  private String car()
  {
    return brand != null ? brand.toString() : null;
  }
}
