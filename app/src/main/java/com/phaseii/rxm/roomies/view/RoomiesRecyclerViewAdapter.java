package com.phaseii.rxm.roomies.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phaseii.rxm.roomies.R;
import com.phaseii.rxm.roomies.exception.RoomXpnseMngrException;
import com.phaseii.rxm.roomies.helper.RoomiesHelper;

import static com.phaseii.rxm.roomies.helper.RoomiesConstants.APP_ERROR;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.ROOM_BUDGET_FILE_KEY;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.ROOM_EXPENDITURE_FILE_KEY;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.ROOM_INFO_FILE_KEY;

/**
 * Created by Snehankur on 4/4/2015.
 */
public class RoomiesRecyclerViewAdapter
		extends RecyclerView.Adapter<RoomiesRecyclerViewAdapter.ViewHolder> {

	public RoomiesRecyclerViewAdapter(String Titles[], int Icons[], String Name,
	                                  String Email, int Profile) {
		mNavTitles = Titles;
		mIcons = Icons;
		name = Name;
		email = Email;
		profile = Profile;
	}

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;

	private String mNavTitles[];
	private int mIcons[];
	private String name;
	private int profile;
	private String email;

	public static class ViewHolder extends RecyclerView.ViewHolder {

		int holderId;
		TextView textView;
		ImageView imageView;
		ImageView profile;
		TextView name;
		TextView email;

		public ViewHolder(View itemView, int ViewType, final Context mContext) {
			super(itemView);
			itemView.setClickable(true);
			itemView.setOnClickListener(new View.OnClickListener() {
				Toast mToast;

				@Override
				public void onClick(View v) {
					int pos = getPosition();
					if (pos == 5) {
						SharedPreferences mSharedPref = mContext.getSharedPreferences(
								ROOM_INFO_FILE_KEY, Context.MODE_PRIVATE);
						SharedPreferences.Editor mEditor = mSharedPref.edit();
						mEditor.clear();
						mEditor.apply();
						mSharedPref = mContext.getSharedPreferences(
								ROOM_BUDGET_FILE_KEY, Context.MODE_PRIVATE);
						mEditor = mSharedPref.edit();
						mEditor.clear();
						mEditor.apply();
						mSharedPref = mContext.getSharedPreferences(
								ROOM_EXPENDITURE_FILE_KEY, Context.MODE_PRIVATE);
						mEditor = mSharedPref.edit();
						mEditor.clear();
						mEditor.apply();
						try {
							RoomiesHelper.startActivityHelper(mContext,
									mContext.getResources()
											.getString(R.string.HomeScreen_Activity), null, true);
						} catch (RoomXpnseMngrException e) {
							RoomiesHelper.createToast(mContext, APP_ERROR, mToast);
							System.exit(0);
						}
					}
				}
			});
			if (ViewType == TYPE_ITEM) {
				textView = (TextView) itemView.findViewById(R.id.rowText);
				imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
				holderId = 1;
			} else {
				name = (TextView) itemView.findViewById(R.id.name);
				email = (TextView) itemView.findViewById(R.id.email);
				profile = (ImageView) itemView.findViewById(R.id.circleView);
				holderId = 0;
			}
		}


	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).
					inflate(R.layout.drawer_list_item, parent, false);
			ViewHolder vhItem = new ViewHolder(v,
					viewType,
					parent.getContext());
			return vhItem;
		} else if (viewType == TYPE_HEADER) {
			View v = LayoutInflater.from(parent.getContext()).
					inflate(R.layout.drawer_header, parent, false);
			ViewHolder vhHeader = new ViewHolder(v,
					viewType, parent.getContext());
			return vhHeader;
		}
		return null;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		if (viewHolder.holderId == 1) {
			viewHolder.textView.setText(
					mNavTitles[position - 1]);
			viewHolder.imageView.setImageResource(
					mIcons[position - 1]);
		} else {
			viewHolder.profile.setImageResource(profile);
			viewHolder.name.setText(name);
			viewHolder.email.setText(email);
		}
	}


	@Override
	public int getItemCount() {
		return mNavTitles.length + 1;
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position))
			return TYPE_HEADER;

		return TYPE_ITEM;
	}

	private boolean isPositionHeader(int position) {
		return position == 0;
	}

}
