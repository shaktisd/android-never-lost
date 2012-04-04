package com.rssaggregator.activity;

import java.util.List;
import java.util.Map;

import android.R.color;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class CustomExpandableListAdapter extends SimpleExpandableListAdapter {

	
	 private LayoutInflater mInflater;
	 private List<Map<String, String>> groupData;
	 private List<List<Map<String, String>>> childData;



	public CustomExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, int lastChildLayout, String[] childFrom,
			int[] childTo) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom,
				groupTo, childData, childLayout, lastChildLayout, childFrom, childTo);
		// TODO Auto-generated constructor stub
	}
	
	

	public CustomExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom,
				groupTo, childData, childLayout, childFrom, childTo);
		// TODO Auto-generated constructor stub
	}



	public CustomExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
		// Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.groupData = (List<Map<String, String>>) groupData;
        //this.childData = childData;
	}



	@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
		
		  // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        //ViewHolder holder;

     // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
/*
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.customrowtext1);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
*/
        if (convertView != null ){
        	convertView = mInflater.inflate(R.layout.custom_row, null);	
        }
        
        View itemRenderer = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        TextView tv = (TextView)itemRenderer.findViewById(R.id.customrowtext1);
        String childListItem = tv.getText().toString();
        if (isFeedRead(childListItem) ){
        	tv.setTextColor(R.color.readfeed);
        }else {
        	if(tv.getTextColors().equals(R.color.readfeed)){
        		convertView = mInflater.inflate(R.layout.custom_row, null);	
        		itemRenderer = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        		tv = (TextView)itemRenderer.findViewById(R.id.customrowtext1);
        		tv.setTextColor(R.color.unreadfeed);
        	}
        }
        
        if (childListItem.length() > 0 ){
        	tv.setText(childListItem.subSequence(0, childListItem.length()-1));	
        }

        // Creates a ViewHolder and store references to the two children views
        // we want to bind data to.
        //holder = new ViewHolder();
        //holder.text = (TextView) convertView.findViewById(R.id.customrowtext1);
        //convertView.setTag(holder);

		
        
        return convertView;
    }



	private boolean isFeedRead(CharSequence text) {
		if (text.length() > 0 &&  text.charAt(text.length()-1) == 'Y') {
			return true;
		}else {
			return false;
		}
	}

}


 class ViewHolder {
    TextView text;
}


