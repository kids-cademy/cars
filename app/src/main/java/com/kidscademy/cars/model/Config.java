package com.kidscademy.cars.model;

import com.kidscademy.commons.ConfigBase;

/**
 * Master configuration object is updated by repository server and synchronized on all devices.
 * 
 * @author Iulian Rotaru
 */
public class Config implements ConfigBase
{
  /**
   * Master audit switch force audit disable on all devices. If this is enabled audit is controlled by user preferences.
   */
  private boolean auditEnabled;

  /** Sync period in milliseconds. */
  private int syncPeriod;

  /**
   * Returns true if master audit switch is ON.
   * 
   * @return true if master audit is enabled.
   * @see #auditEnabled
   */
  public boolean isAuditEnabled()
  {
    return auditEnabled;
  }

  /**
   * Get storage synchronization polling period.
   * 
   * @return synchronization polling period.
   * @see #syncPeriod
   */
  public int getSyncPeriod()
  {
    return syncPeriod;
  }
}
