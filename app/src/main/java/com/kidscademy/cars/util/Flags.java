package com.kidscademy.cars.util;

import com.kidscademy.app.FlagsBase;

/**
 * Application global state and flags.
 * 
 * @author Iulian Rotaru
 */
public final class Flags extends FlagsBase
{
  private static final String FLAGS_SCORE_ORDR_BY = "flags.order.by";
  private static final String FLAGS_SPEAKER_NOTES = "flags.speaker.notes";
  private static final String FLAGS_CURRENT_LEVEL = "flags.current.level";

  public static void setScoreOrderBy(int position)
  {
    putInt(FLAGS_SCORE_ORDR_BY, position);
  }

  public static int getScoreOrderBy()
  {
    return getInt(FLAGS_SCORE_ORDR_BY);
  }

  public static void toggleSpeakerNotes()
  {
    putBoolean(FLAGS_SPEAKER_NOTES, !isSpeakerNotes());
  }

  public static boolean isSpeakerNotes()
  {
    return getBoolean(FLAGS_SPEAKER_NOTES);
  }

  public static void setCurrentLevel(int levelIndex)
  {
    putInt(FLAGS_CURRENT_LEVEL, levelIndex);
  }

  public static int getCurrentLevel()
  {
    return getInt(FLAGS_CURRENT_LEVEL);
  }
}
