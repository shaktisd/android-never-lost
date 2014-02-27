package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rssaggregator.valueobjects.Category;

public class CategoryActivity extends Activity {

	Button saveCategory;
	Button deleteCategory;
	TextView categoryNameText;
	ListView categoryListView;
	RssAggregatorApplication rssAggregatorApplication;
	List<String> values;
	ArrayAdapter<String> adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);
		this.setTitle("Home > Category");
		registerViewItems();
		registerClickListenersToButtons();
		values = getAllCategories();

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the View to which the data is written
		// Forth - the Array of data
		adapter = new ArrayAdapter<String>(this, R.layout.category_list, R.id.categorytext1, values);
		// Assign adapter to ListView
		categoryListView.setAdapter(adapter);
	}

	private List<String> getAllCategories() {
		rssAggregatorApplication = getRssAggregatorApplication();
		List<Category> allCategory = rssAggregatorApplication.findAllCategory();
		List<String> localValues = new ArrayList<String>();
		for (Category category : allCategory) {
			localValues.add(category.getCategoryName());
		}
		return localValues;
	}

	private void registerClickListenersToButtons() {
		saveCategory.setOnClickListener(new View.OnClickListener() {
			StringBuffer message;

			public void onClick(View arg0) {
				message = new StringBuffer();
				Category category = new Category(categoryNameText.getText().toString());
				StringBuffer validate = validate(categoryNameText.getText().toString());
				if (validate.length() == 0) {
					rssAggregatorApplication = getRssAggregatorApplication();
					Category storedCategory = rssAggregatorApplication.findCategoryByName(category.getCategoryName());
					if (storedCategory != null) {
						message.append("Category : " + category.getCategoryName() + " already exists");
					} else {
						rssAggregatorApplication.save(category);
						values.add(category.getCategoryName());
						adapter.notifyDataSetChanged();
						categoryNameText.setText("");
						message.append("Saved Category : " + category.getCategoryName());
						rssAggregatorApplication.setRefreshMainDataSet(true);

					}
				} else {
					message.append(validate);
				}

				Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
			}

			private StringBuffer validate(String categoryName) {
				StringBuffer msg = new StringBuffer();
				if (categoryName == null) {
					msg.append("Provide Category name");
				}

				if (categoryName != null & categoryName.trim().length() == 0) {
					msg.append("Provide Category name");
				}
				return msg;
			}
		});

		deleteCategory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Category category = new Category(categoryNameText.getText().toString());
				rssAggregatorApplication = getRssAggregatorApplication();
				final Category storedCategory = rssAggregatorApplication.findCategoryByName(category.getCategoryName());
				StringBuffer message = new StringBuffer();
				if (storedCategory == null) {
					message.append("Category : " + category.getCategoryName() + " doen't exists");
					Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
					builder.setMessage("Are you sure you want to delete?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							rssAggregatorApplication.deleteCategoryFeedSourceAndFeeds(storedCategory);
							values.remove(category.getCategoryName());
							adapter.notifyDataSetChanged();
							Toast.makeText(getApplicationContext(), "Deleted Category : " + category.getCategoryName(), Toast.LENGTH_SHORT).show();
							rssAggregatorApplication.setRefreshMainDataSet(true);
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});

		categoryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				categoryNameText.setText(((TextView) view).getText());
			}
		});

	}

	private void registerViewItems() {
		saveCategory = (Button) findViewById(R.id.saveCategory);
		categoryNameText = (TextView) findViewById(R.id.rssSourceName);
		categoryListView = (ListView) findViewById(R.id.categorylist);
		deleteCategory = (Button) findViewById(R.id.deleteCategory);
	}

	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.homemenu, menu);
		return true;
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_home:
			intent = new Intent(this, MainRssAggregatorActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_back:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}