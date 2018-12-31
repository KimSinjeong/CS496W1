package com.example.q.cs496w1;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;


public class SingleContactView extends LinearLayout {
    TextView Name;
    TextView Phone;
    ImageView Thumbnail;

    public SingleContactView(Context context) {
        super(context);

        init(context);
    }

    public SingleContactView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_contact, this, true);

        Name = (TextView) findViewById(R.id.name);
        Phone = (TextView) findViewById(R.id.phone);
        Thumbnail = (ImageView) findViewById(R.id.thumbnail);
    }

    public void setName(String name) { Name.setText(name); }

    public void setPhone(String mobile) {
        Phone.setText(mobile);
    }

    public void setImage(int resId) {
        Thumbnail.setImageResource(resId);
    }

    public void setImage(long photoId, long Id) {
        Bitmap tempbitmap = loadContactPhoto(Id, photoId);
        if (tempbitmap != null)
            Thumbnail.setImageBitmap(tempbitmap);
    }

    public Bitmap loadContactPhoto(long id, long photo_id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(), uri);
        if (input != null)
            return resizingBitmap(BitmapFactory.decodeStream(input));
        else
            Log.d("PHOTO", "first try failed to load photo");

        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor c = getContext().getContentResolver().query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null);
        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }

        if (photoBytes != null)
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        else
            Log.d("PHOTO", "second try also failed");
        return null;
    }

    public Bitmap resizingBitmap(Bitmap oBitmap) {
        if (oBitmap == null)
            return null;
        float width = oBitmap.getWidth();
        float height = oBitmap.getHeight();
        float resizing_size = 70;
        Bitmap rBitmap = null;
        if (width > resizing_size) {
            float mWidth = (float) (width / 100);
            float fScale = (float) (resizing_size / mWidth);
            width *= (fScale / 100);
            height *= (fScale / 100);
        }
        else if (height > resizing_size) {
            float mHeight = (float) (height / 100);
            float fScale = (float) (resizing_size / mHeight);
            width *= (fScale / 100);
            height *= (fScale / 100);
        }
        Log.d("PHOTO","rBitmap : " + width + ", " + height);
        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int)width, (int) height, true);
        return rBitmap;
    }
}
