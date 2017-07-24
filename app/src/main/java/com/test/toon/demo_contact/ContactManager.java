package com.test.toon.demo_contact;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

public class ContactManager {

	/**
	 * 获取联系人信息
	 * 
	 * @param context
	 * @return 联系人列表
	 */
	public static List<ContactBean> getContacts(Context context) {
		List<ContactBean> contacts = new ArrayList<ContactBean>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cRawContacts = resolver.query(RawContacts.CONTENT_URI, new String[] { RawContacts._ID }, null, null,
				null);
		while (cRawContacts.moveToNext()) {
			ContactBean contact = new ContactBean();
			long rawContactId = cRawContacts.getLong(cRawContacts.getColumnIndex(RawContacts._ID));
			contact.setRawContactId(rawContactId);
			Cursor dataCursor = resolver.query(Data.CONTENT_URI, null, // null
																		// 表示取出所有字段
					Data.RAW_CONTACT_ID + "=?", new String[] { String.valueOf(rawContactId) }, null);
			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor.getColumnIndex(Data.DATA1));
				String mimeType = dataCursor.getString(dataCursor.getColumnIndex(Data.MIMETYPE));
				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					contact.setName(data1);
				} else if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					contact.setPhone(data1);
				}
			}
			contacts.add(contact);
			dataCursor.close();// 使用完之后需要关闭
		}
		cRawContacts.close();
		return contacts;
	}

	/**
	 * 添加联系人信息
	 */
	public static void addContact(Context context, ContactBean contact) {
		/*
		 * 1.封装成ContentValues 2.插入
		 */
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		Uri uri = resolver.insert(RawContacts.CONTENT_URI, values);
		System.out.println(uri);
		long rawContactId = ContentUris.parseId(uri);

		ContentValues value1 = new ContentValues();
		value1.put(Data.RAW_CONTACT_ID, rawContactId);
		value1.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		value1.put(Phone.NUMBER, contact.getPhone());
		resolver.insert(Data.CONTENT_URI, value1);

		ContentValues value2 = new ContentValues();
		value2.put(Data.RAW_CONTACT_ID, rawContactId);
		value2.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		value2.put(StructuredName.DISPLAY_NAME, contact.getName());
		resolver.insert(Data.CONTENT_URI, value2);
	}

	/**
	 * 更新联系人信息
	 */
	public static void updateContact(Context context, ContactBean contact) {
		ContentResolver resolver = context.getContentResolver();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
				.withSelection(Data.RAW_CONTACT_ID + "=? AND " + Data.MIMETYPE + "=?",
						new String[] { String.valueOf(contact.getRawContactId()), StructuredName.CONTENT_ITEM_TYPE })
				.withValue(StructuredName.DISPLAY_NAME, contact.getName()).build());
		ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
				.withSelection(Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE + "= ?",
						new String[] { String.valueOf(contact.getRawContactId()), Phone.CONTENT_ITEM_TYPE })
				.withValue(Phone.NUMBER, contact.getPhone()).build());

		try {
			resolver.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}

	public static void deleteContact(Context context, ContactBean contact) {
		ContentResolver resolver = context.getContentResolver();
		//这里需要删除RawContacts表中的记录
		resolver.delete(RawContacts.CONTENT_URI, RawContacts._ID + "=?",
				new String[] { String.valueOf(contact.getRawContactId())});
	}
}
