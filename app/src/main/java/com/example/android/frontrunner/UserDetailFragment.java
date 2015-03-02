package com.example.android.frontrunner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.frontrunner.Common.DBRelated;
import com.example.android.frontrunner.data.DataContract;

/**
 * Created by Melos on 9/24/2014.
 */
public class UserDetailFragment extends Fragment {
    private int userID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.user_detail_fragment, container, false);
        if(getArguments() == null) { return null; }
        Integer userID = getArguments().getInt(MainFragment.EXTRA_FIELD_ID);
        Cursor cursor = getActivity().getContentResolver().query(DataContract.Users.USERS_CONTENT_URI, null, DataContract.Users.COLUMN_USER_ID + " = ? ", new String[]{Integer.toString(userID)}, null);
        if (cursor.moveToFirst()) {

            TextView detailUserId = (TextView)rootView.findViewById(R.id.detail_user_id_TV);
            detailUserId.setText(cursor.getString(DBRelated.USER_ID_COL_INDEX));
            ((TextView) rootView.findViewById(R.id.detail_user_name_TV)).setText(cursor.getString(DBRelated.USER_NAME_COL_INDEX));
            ((TextView) rootView.findViewById(R.id.detail_user_nick_TV)).setText(cursor.getString(DBRelated.USER_NICKNAME_COL_INDEX));


            ImageView userImage = (ImageView) rootView.findViewById(R.id.detail_user_photo);
            Integer getUserImage = getActivity().getResources().getIdentifier(cursor.getString(DBRelated.USER_PHOTO_COL_INDEX), "drawable", getActivity().getPackageName());
            if (getUserImage != 0)
                userImage.setImageDrawable(getActivity().getResources().getDrawable(getUserImage));
        }


        return rootView;
    }


}
