package com.fallen.streamgetter;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.fallen.streamgetterfree.R;

public class ListFilesAdapter extends BaseAdapter {
	Context context;
	File [] fileList;
	CallBackListView cblv;
	float xDown = 0;
	float xUp = 0;
	public ListFilesAdapter(Context context, File [] fileList, CallBackListView cblv) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.fileList = fileList;
		this.cblv= cblv;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fileList.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("file://"+ fileList [position].getAbsolutePath());
		return uri;
		
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=convertView;
		if (convertView == null)
		{
			LayoutInflater li = LayoutInflater.from(context);
			v = li.inflate(R.layout.file_row, parent, false);

		}
		final TextView text = (TextView)v.findViewById(R.id.filename);
		text.setText(fileList [position].getName());
		v.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				return false;
				
			}
		});
		ImageView delButton = (ImageView)v.findViewById(R.id.deleteFile);
		delButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean success = fileList [position].delete();
				if (!success)
					//Toast.makeText(context, fileList [position].getName() +" deleted", 1).show();
				//else
					Toast.makeText(context, fileList [position].getName() +" unable to delete", 1).show();
				cblv.onDelete(position);
				//v.setVisibility(View.GONE);
				//text.setVisibility(View.GONE);
			}
		});
       //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
       //imageView.setBackgroundResource(mGalleryItemBackground);

       return v;
		
	}

}
