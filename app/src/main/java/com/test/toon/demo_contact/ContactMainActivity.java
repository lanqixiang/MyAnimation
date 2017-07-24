package com.test.toon.demo_contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.toon.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 1.基本功能：增删改查联系人数据 2.点击联系人拨打和发送短信
 * 
 * @author Administrator
 *
 */
public class ContactMainActivity extends Activity {
	private Button mAddButton;
	private PinnedSectionListView mListView;
	private LetterBar mLetterBar;
	private TextView mTextView;
	private ContactAdapter mContactAdapter;
	private List<ContactBean> contacts = new ArrayList<ContactBean>();
	private ArrayList<Object> datas;
	private Map<String, Integer> letterPositions;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactmain);
		initHandler();
		mAddButton = (Button) findViewById(R.id.btn_add);
		mListView = (PinnedSectionListView) findViewById(R.id.lv);
		mListView.setShadowVisible(false);
		mLetterBar = (LetterBar) findViewById(R.id.letter_bar);
		mTextView = (TextView) findViewById(R.id.tv_letter);
		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAddDialog();
				setContactData();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = parent.getAdapter().getItem(position);
				if (obj instanceof ContactBean) {
					ContactBean contact = (ContactBean) obj;
					showMoreDialog(contact);
				}
			}

		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = parent.getAdapter().getItem(position);
				if (obj instanceof ContactBean) {
					ContactBean contact = (ContactBean) obj;
					showDeleteDialog(contact);
					setContactData();
				}
				return true;
			}

		});
		
		mLetterBar.setOnLetterSelectedListener(new LetterBar.OnLetterSelectedListener() {
			
			@Override
			public void onLetterSelecterd(String str) {
				if(TextUtils.isEmpty(str)) {
					mTextView.setVisibility(View.GONE);
				} else {
					mTextView.setVisibility(View.VISIBLE);
					mTextView.setText(str);
					int position = mContactAdapter.getLetterPosition(str);
					if(position != -1) {
						mListView.setSelection(position);
					}
				}
			}
		});
		
		
		
		setContactData();
		
	}
	private void initHandler(){
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 0:
						mContactAdapter = new ContactAdapter(ContactMainActivity.this, contacts);

						mContactAdapter.updateAdapter(datas,letterPositions);
						mListView.setAdapter(mContactAdapter);
						break;
					default:
						break;
				}
			}
		};
	}


	private void setContactData() {
		List<ContactBean> contactData = ContactManager.getContacts(this);
		contacts.clear();
		contacts.addAll(contactData);
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		new Thread(new Runnable(){
			@Override
			public void run() {
				//耗时操作
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
					initList();
					mHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}).start();


	}

	private void initList() {
		datas = new ArrayList<Object>();
		letterPositions = new HashMap<String, Integer>();
		//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(contacts, new Comparator<ContactBean>() {

			@Override
			public int compare(ContactBean lhs, ContactBean rhs) {
				if(lhs!=null&&rhs!=null&& !TextUtils.isEmpty(lhs.getName())&& !TextUtils.isEmpty(rhs.getName())){
					System.out.println("qqqqqqqqqqq"+lhs.getName().toUpperCase());

					String lhsName = PinYinUtils.trans2PinYin(lhs.getName().trim()).substring(0, 1).toUpperCase();
					String rhsName = PinYinUtils.trans2PinYin(rhs.getName().trim()).substring(0, 1).toUpperCase();
					System.out.println("111"+lhsName);
					System.out.println("222"+rhsName);
					char lc = lhsName.charAt(0);
					char rc = rhsName.charAt(0);

					//return lc== rc ? 0 :
					//		(lc > rc ? 1 : -1);
					return lhsName.compareTo(rhsName);
				}else {
					return 0;
				}

			}
		});
		for (int i = 0; i < contacts.size(); i++) {
			ContactBean contact = contacts.get(i);
			if(!TextUtils.isEmpty(contact.getName())){
				String firstLetter = getFirstLetter(contact.getName());
				if (!letterPositions.containsKey(firstLetter)) {
					letterPositions.put(firstLetter, datas.size());
					datas.add(firstLetter);
				}
				datas.add(contact);
			}


		}
	}

	private String getFirstLetter(String name) {
		String firstLetter = "";
		char c = PinYinUtils.trans2PinYin(name).toUpperCase().charAt(0);
		if (c >= 'A' && c <= 'Z') {
			firstLetter = String.valueOf(c);
		}
		return firstLetter;
	}

	private void showDeleteDialog(final ContactBean contact) {
		new AlertDialog.Builder(this).setTitle("删除联系人")
				.setMessage("确定要删除" + contact.getName() + "吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContactManager.deleteContact(ContactMainActivity.this, contact);
					}

				})
				.setNegativeButton("取消", null)
				.show();
	}

	private void showMoreDialog(final ContactBean contact) {
		new AlertDialog.Builder(this)
				.setItems(new String[] { "拨打电话", "发送短信", "修改联系人" }, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							//拨打电话
							Intent intentCall = new Intent();
							intentCall.setAction(Intent.ACTION_CALL);
							intentCall.setData(Uri.parse("tel:" + contact.getPhone()));
							startActivity(intentCall);
							break;
						case 1:
							//发送短信
							Intent intentMessage = new Intent();
							intentMessage.setAction(Intent.ACTION_SENDTO);
							intentMessage.setData(Uri.parse("smsto://" + contact.getPhone()));
							startActivity(intentMessage);
							break;
						case 2:
							showUpdateDialog(contact);
							setContactData();
							break;
						default:
							break;
						}
					}
				}).show();
	}

	/*
	 * 显示添加联系人的对话框
	 */
	private void showAddDialog() {
		View view = View.inflate(this, R.layout.dialog_add_view, null);
		final EditText nameText = (EditText) view.findViewById(R.id.et_name);
		final EditText phoneText = (EditText) view.findViewById(R.id.et_phone);

		new AlertDialog.Builder(this).setTitle("添加联系人").setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContactBean contact = new ContactBean();
						contact.setName(nameText.getText().toString());
						contact.setPhone(phoneText.getText().toString());
						ContactManager.addContact(ContactMainActivity.this, contact);
					}
				}).setNegativeButton("取消", null).show();
	}

	/*
	 * 显示修改联系人的对话框
	 */
	private void showUpdateDialog(final ContactBean contact) {
		View view = View.inflate(this, R.layout.dialog_add_view, null);
		final EditText nameText = (EditText) view.findViewById(R.id.et_name);
		final EditText phoneText = (EditText) view.findViewById(R.id.et_phone);
		nameText.setText(contact.getName());
		phoneText.setText(contact.getPhone());
		new AlertDialog.Builder(this).setTitle("修改联系人").setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContactBean temp = new ContactBean();
						temp.setRawContactId(contact.getRawContactId());
						temp.setName(nameText.getText().toString());
						temp.setPhone(phoneText.getText().toString());
						ContactManager.updateContact(ContactMainActivity.this, temp);
					}

				}).setNegativeButton("取消", null).show();
	}

}
