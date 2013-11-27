package com.frozendevs.cache.cleaner.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frozendevs.cache.cleaner.model.AppsListItem;
import com.frozendevs.cache.cleaner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsListAdapter extends BaseAdapter {

    public static final int SORT_BY_APP_NAME = 0;
    public static final int SORT_BY_CACHE_SIZE = 1;

    private List<AppsListItem> items = new ArrayList<AppsListItem>();
    private Context context;
    private SharedPreferences sharedPreferences;

    public AppsListAdapter(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final AppsListItem item = items.get(i);

        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_item, null);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + item.getPackageName())));
            }
        });

        ImageView imageView = (ImageView)layout.findViewById(R.id.app_icon);
        imageView.setImageDrawable(item.getApplicationIcon());

        TextView nameView = (TextView)layout.findViewById(R.id.app_name);
        nameView.setText(item.getApplicationName());

        TextView sizeView = (TextView)layout.findViewById(R.id.app_size);
        sizeView.setText(Formatter.formatShortFileSize(context, item.getCacheSize()));

        return layout;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.size() == 0;
    }

    public void setItems(List<AppsListItem> items) {
        this.items = items;

        Collections.sort(this.items, new Comparator<AppsListItem>() {
            @Override
            public int compare(AppsListItem lhs, AppsListItem rhs) {
                switch (sharedPreferences.getInt(context.getString(R.string.sort_by_key), SORT_BY_CACHE_SIZE)) {
                    case SORT_BY_APP_NAME:
                        return lhs.getApplicationName().compareToIgnoreCase(rhs.getApplicationName());

                    case SORT_BY_CACHE_SIZE:
                        return (int)(rhs.getCacheSize() - lhs.getCacheSize());
                }

                return 0;
            }
        });
    }

    public void filterAppsByName(String filter) {
        List<AppsListItem> filteredItems = new ArrayList<AppsListItem>();

        for(AppsListItem item : items)
            if(item.getApplicationName().toLowerCase().contains(filter.toLowerCase()))
                filteredItems.add(item);

        items = filteredItems;
    }
}
