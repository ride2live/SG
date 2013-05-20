package com.fallen.streamgetter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fallen.streamgetterfree.R;

public class ListGenresAdapter extends BaseAdapter { 						//default stuff for list adapter
	Context context;
	ArrayList<String> genres;
	public ListGenresAdapter(Context context, ArrayList<String> genres) {
		this.context = context;
		this.genres = genres;
		genres.add(0, "Favorites");
	}

	@Override
	public int getCount() {
		return genres.size();
	}

	@Override
	public Object getItem(int position) {
		return genres.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v=convertView;

        if(convertView==null)
        {
        	LayoutInflater li = LayoutInflater.from(context);
        	v = li.inflate(R.layout.genre_list_row, parent, false);
        }
        ImageView image = (ImageView)v.findViewById(R.id.genreImage);
      //  if (position == 0)
       // 	image.setImageResource(android.R.drawable.star_big_on);
       // else
        	image.setImageResource(Utils.getGenreImage(position));
       // if (genres.get(position).equals(""));
                     	//When done with design, set bitmap by condition Altrenative = 1, Alternative icon = 1 and etc 
        TextView text = (TextView)v.findViewById(R.id.genreName);
        text.setText(genres.get(position));
        return v;
	}

}
