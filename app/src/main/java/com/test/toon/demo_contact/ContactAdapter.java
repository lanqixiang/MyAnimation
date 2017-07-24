package com.test.toon.demo_contact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.toon.R;

public class ContactAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
	public static final int VIEW_TYPE_CONTACT = 0;
	public static final int VIEW_TYPE_LETTER = 1;
	private Context context;
	private List<ContactBean> contacts;

	private List<Object> datas;
	private Map<String, Integer> letterPositions;


	public ContactAdapter(Context context, List<ContactBean> contacts) {
		this.context = context;
		this.contacts = contacts;
	}

	public void updateAdapter(ArrayList<Object> data,Map<String, Integer>letterPosition ) {
		datas=data;
		letterPositions=letterPosition;
		//notifyDataSetChanged();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return datas.get(position) instanceof ContactBean ? VIEW_TYPE_CONTACT : VIEW_TYPE_LETTER;
	}

	public int getLetterPosition(String letter) {
		Integer positoin = letterPositions.get(letter);
		return positoin == null ? -1 : positoin;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemViewType = getItemViewType(position);
		return itemViewType == VIEW_TYPE_CONTACT ? getContactView(position, convertView)
				: getLetterView(position, convertView);
	}

	private View getLetterView(int position, View convertView) {
		ViewHolder viewHolder;
		if(convertView ==null) {
			convertView = View.inflate(context, R.layout.item_letter_view, null);
			viewHolder = new ViewHolder();
			viewHolder.letter = (TextView) convertView.findViewById(R.id.tv_letter);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String letter = (String) getItem(position);
		viewHolder.letter.setText(letter);
		return convertView;
	}

	private View getContactView(int position, View convertView) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_contact_view, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.tv_phone);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ContactBean contact = (ContactBean) datas.get(position);
		viewHolder.name.setText(contact.getName());
		viewHolder.phone.setText(contact.getPhone());
		return convertView;
	}

	class ViewHolder {
		TextView letter;
		TextView name;
		TextView phone;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {

		return viewType == VIEW_TYPE_LETTER;
	}

}
