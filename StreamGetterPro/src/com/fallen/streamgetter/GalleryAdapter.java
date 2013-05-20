package com.fallen.streamgetter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fallen.streamgetterfree.R;

public class GalleryAdapter extends BaseAdapter {
	Context context;
	 ArrayList<String> stations;
	 boolean showEdit;
	 CallBackStationListView cbslv;
	public GalleryAdapter(Context context, ArrayList<String> stations, boolean showEdit, CallBackStationListView cbslv) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.cbslv = cbslv;
		this.stations = stations;
		this.showEdit = showEdit;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stations.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=convertView;
		if (convertView == null)
		{
			LayoutInflater li = LayoutInflater.from(context);
        	v = li.inflate(R.layout.station_item, parent, false);
		}
		final ImageView delImage = (ImageView)v.findViewById(R.id.deleteStation);
		final TextView text = (TextView)v.findViewById(R.id.stName);
		final String station = stations.get(position);
		text.setText(station);
		if (showEdit)
		{
			
			delImage.setVisibility(View.VISIBLE);
			delImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					v.setVisibility(View.GONE);
					text.setVisibility(View.GONE);
					delImage.setVisibility(View.GONE);
					cbslv.onDeleteStation(station);
				}
			});
		}
		else
		{
			delImage.setVisibility(View.INVISIBLE);
		}
		//imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setBackgroundResource(mGalleryItemBackground);

        return v;
		
	}

}
