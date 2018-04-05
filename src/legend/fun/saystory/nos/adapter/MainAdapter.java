package legend.fun.saystory.nos.adapter;

import java.util.ArrayList;

import legend.fun.saystory.nos.R;
import legend.fun.saystory.nos.activity.MainActivity;
import legend.fun.saystory.nos.data.Main_Data;
import legend.fun.saystory.nos.util.ImageLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainAdapter extends BaseAdapter{
	public Context context;
	public ImageLoader imgLoader;
	public int _id = -1;
	public String id = "empty";
	public Cursor cursor;
	public ImageButton bt_favorite;
	public ArrayList<Main_Data> list;
	public ListView listview_main;
	public MainAdapter(Context context, ArrayList<Main_Data> list, ListView listview_main) {
		this.imgLoader = new ImageLoader(context.getApplicationContext());
		this.context = context;
		this.list = list;
		this.listview_main = listview_main;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		try{
			if(view == null){	
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				view = layoutInflater.inflate(R.layout.main_activity_listrow, parent, false); 
			}
			ImageView img_imageurl = (ImageView)view.findViewById(R.id.img_imageurl);
			img_imageurl.setFocusable(false);
			String image_url = list.get(position).thumbnail_hq;
			imgLoader.DisplayImage(image_url, R.drawable.no_image, img_imageurl);

			TextView txt_music = (TextView)view.findViewById(R.id.txt_music);
			txt_music.setText(list.get(position).title);
			
			
			TextView txt_artist = (TextView)view.findViewById(R.id.txt_artist);
			txt_artist.setText(list.get(position).category);
			
			bt_favorite = (ImageButton)view.findViewById(R.id.bt_favorite);
			bt_favorite.setFocusable(false);
			bt_favorite.setSelected(false);
			cursor = MainActivity.favorite_mydb.getReadableDatabase().rawQuery(
					"select * from favorite_list where id = '"+list.get(position).id+"'", null);
			if(null != cursor && cursor.moveToFirst()){
				id = cursor.getString(cursor.getColumnIndex("id"));
				_id = cursor.getInt(cursor.getColumnIndex("_id"));
			}else{
				id = "empty";
				_id = -1;
			}
			if(id.equals("empty")){
				bt_favorite.setImageResource(R.drawable.bt_favorite_normal);
			}else{
				bt_favorite.setImageResource(R.drawable.bt_favorite_pressed);	
			}
			
			bt_favorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cursor = MainActivity.favorite_mydb.getReadableDatabase().rawQuery(
							"select * from favorite_list where id = '"+list.get(position).id+"'", null);
					if(null != cursor && cursor.moveToFirst()){
						id = cursor.getString(cursor.getColumnIndex("id"));
						_id = cursor.getInt(cursor.getColumnIndex("_id"));
					}else{
						id = "empty";
						_id = -1;
					}
					if(id.equals("empty")){
						bt_favorite.setImageResource(R.drawable.bt_favorite_pressed);
						ContentValues cv = new ContentValues();
						cv.put("id", list.get(position).id);
						cv.put("title", list.get(position).title);
						cv.put("portal", list.get(position).portal);
						cv.put("category", list.get(position).category);
						cv.put("thumbnail_hq", list.get(position).thumbnail_hq);
						cv.put("duration", list.get(position).duration);
						MainActivity.favorite_mydb.getWritableDatabase().insert("favorite_list", null, cv);
						MainActivity.main_adapter.notifyDataSetChanged();
						Toast.makeText(context, context.getString(R.string.txt_main_activity11), Toast.LENGTH_SHORT).show();
					}else{
						bt_favorite.setImageResource(R.drawable.bt_favorite_normal);
						MainActivity.favorite_mydb.getWritableDatabase().delete("favorite_list", "_id" + "=" +_id, null);
						MainActivity.main_adapter.notifyDataSetChanged();
						Toast.makeText(context, context.getString(R.string.txt_main_activity12), Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			if(listview_main.isItemChecked(position)){
				view.setBackgroundColor(Color.parseColor("#f4b757"));
				txt_music.setTextColor(Color.parseColor("#000000"));
				txt_artist.setTextColor(Color.parseColor("#000000"));
			}else{
				view.setBackgroundColor(Color.parseColor("#00000000"));
				txt_music.setTextColor(Color.parseColor("#ffffff"));
				txt_artist.setTextColor(Color.parseColor("#ffffff"));
			}
		}catch (Exception e) {
		}finally{
			MainActivity.favorite_mydb.close();
			if(cursor != null){
				cursor.close();	
			}
		}
		return view;
	}
}