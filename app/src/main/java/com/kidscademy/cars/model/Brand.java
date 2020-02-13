package com.kidscademy.cars.model;

import js.util.Assets;
import js.util.Strings;
import android.os.Parcel;
import android.os.Parcelable;

import com.kidscademy.cars.App;
import com.kidscademy.commons.Country;
import com.kidscademy.util.Countries;

public class Brand implements Parcelable
{
  private String name;
  private String logoAssetPath;
  private String dimLogoAssetPath;
  private String voiceAssetPath;
  private Country country;
  private int foundationYear;
  private int defunctYear;
  private int rank;

  public Brand()
  {
  }

  public Brand(String name, Country country, int foundationYear, int rank)
  {
    this(name, country, foundationYear, 0, rank);
  }

  public Brand(String name, Country country, int foundationYear, int defunctYear, int rank)
  {
    this.name = name;
    this.logoAssetPath = Strings.concat("image/", name, ".png");
    this.dimLogoAssetPath = Strings.concat("image/", name, "_dim.png");
    if(!Assets.exists(App.context(), this.dimLogoAssetPath)) {
      this.dimLogoAssetPath = this.logoAssetPath;
    }
    this.voiceAssetPath = Strings.concat("voice/", name, ".mp3");
    this.country = country;
    this.foundationYear = foundationYear;
    this.defunctYear = defunctYear;
    this.rank = rank;
  }

  public Brand(Parcel parcel)
  {
    this.name = parcel.readString();
    this.logoAssetPath = parcel.readString();
    this.dimLogoAssetPath = parcel.readString();
    this.voiceAssetPath = parcel.readString();
    this.country = Country.valueOf(parcel.readString());
    this.foundationYear = parcel.readInt();
    this.defunctYear = parcel.readInt();
    this.rank = parcel.readInt();
  }

  public String getName()
  {
    return name;
  }

  public String getLogoAssetPath()
  {
    return logoAssetPath;
  }

  public String getDimLogoAssetPath()
  {
    return dimLogoAssetPath;
  }

  public String getVoiceAssetPath()
  {
    return voiceAssetPath;
  }

  public Country getCountry()
  {
    return country;
  }

  public String getCountryName()
  {
    return App.prefs().isOfficialCountryName() ? Countries.officialName(country) : Countries.name(country);
  }

  public int getFoundationYear()
  {
    return foundationYear;
  }

  public int getDefunctYear()
  {
    return defunctYear;
  }

  public boolean isDefunct()
  {
    return defunctYear != 0;
  }

  public String getDisplay()
  {
    return name.replace('_', ' ');
  }

  public int getRank()
  {
    return rank;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    Brand other = (Brand)obj;
    if(name == null) {
      if(other.name != null) return false;
    }
    else if(!name.equals(other.name)) return false;
    return true;
  }

  @Override
  public String toString()
  {
    return name;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags)
  {
    parcel.writeString(name);
    parcel.writeString(logoAssetPath);
    parcel.writeString(dimLogoAssetPath);
    parcel.writeString(voiceAssetPath);
    parcel.writeString(country.name());
    parcel.writeInt(foundationYear);
    parcel.writeInt(defunctYear);
    parcel.writeInt(rank);
  }

  public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>()
  {
    public Brand createFromParcel(Parcel parcel)
    {
      return new Brand(parcel);
    }

    public Brand[] newArray(int size)
    {
      return new Brand[size];
    }
  };
}
