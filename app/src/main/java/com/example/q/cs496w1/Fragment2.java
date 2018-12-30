package com.example.q.cs496w1;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    GridView gridView;
    PhotoAdapter adapter;

    public static Fragment2 newInstance() {
        Bundle args = new Bundle();
        Fragment2 fragment = new Fragment2();
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);

        adapter = new PhotoAdapter();

        adapter.addItem(new SinglePhoto("소녀시대", R.drawable.singer));
        adapter.addItem(new SinglePhoto("걸스데이", R.drawable.singer2));
        adapter.addItem(new SinglePhoto("여자친구", R.drawable.singer3));
        adapter.addItem(new SinglePhoto("티아라", R.drawable.singer4));
        adapter.addItem(new SinglePhoto("AOA", R.drawable.singer5));


        gridView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    class PhotoAdapter extends BaseAdapter {
        ArrayList<SinglePhoto> items = new ArrayList<SinglePhoto>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SinglePhoto item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SinglePhotoView view = new SinglePhotoView(getActivity().getApplicationContext());

            Boolean hasPermission = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasPermission = Permissioncheck();
            }

            if(hasPermission){

            }

            SinglePhoto item = items.get(position);
            view.setName(item.getName());
            view.setImage(item.getResId());

            int numColumns = gridView.getNumColumns();
            int rowIndex = position / numColumns;
            int columnIndex = position % numColumns;
            Log.d("PhotoAdapter", "index : " + rowIndex + ", " + columnIndex);

            return view;
        }
    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    public boolean Permissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1052);
            if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

}
