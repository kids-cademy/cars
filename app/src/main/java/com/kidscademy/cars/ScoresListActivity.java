package com.kidscademy.cars;

import js.log.Log;
import js.log.LogFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.kidscademy.app.AppActivityBase;
import com.kidscademy.cars.model.OrderBy;
import com.kidscademy.cars.model.ScoreAdapter;
import com.kidscademy.cars.util.Flags;

public class ScoresListActivity extends AppActivityBase implements OnItemSelectedListener
{
  private static final Log log = LogFactory.getLog(ScoresListActivity.class);

  public static void start(Activity activity)
  {
    log.trace("start(Context)");
    Intent intent = new Intent(activity, ScoresListActivity.class);
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
  }

  private ListView listView;
  private ScoreAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scores_list);

    listView = (ListView)findViewById(R.id.score_list);

    adapter = new ScoreAdapter(this, App.storage().getBrands());
    listView.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.scores_list, menu);

    MenuItem item = menu.findItem(R.id.score_order_by);
    Spinner spinner = (Spinner)MenuItemCompat.getActionView(item);
    SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.scores_list_order_by, android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(this);
    spinner.setSelection(Flags.getScoreOrderBy());

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
  {
    Flags.setScoreOrderBy(position);
    OrderBy orderBy = OrderBy.values()[position];
    adapter.sort(orderBy);
    log.debug("Change sounds score order by to |%s|.", orderBy);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {
  }

  @Override
  public void onBackPressed()
  {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
  }
}
