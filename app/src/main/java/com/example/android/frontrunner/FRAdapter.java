package com.example.android.frontrunner;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.frontrunner.BackgroundTasks.gameCreated;
import com.example.android.frontrunner.Common.Common;
import com.example.android.frontrunner.Common.DBRelated;

import java.util.HashMap;

/**
 * Created by dev on 24/09/2014.
 */
public class FRAdapter extends CursorAdapter {
    LayoutInflater mInflater;
    public static boolean checkForStatus = false;

    private int userListPosition = -1;

    public FRAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return mInflater.inflate(R.layout.friend_list_listview, viewGroup, false);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckBox cb;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_list_listview, null);

            cb = (CheckBox) convertView.findViewById(R.id.friend_list_checkbutton);
            if (cb != null) {
                cb.setTag(position);
            }
        } else {
            cb = (CheckBox) convertView.findViewById(R.id.friend_list_checkbutton);

            if (cb != null) {
                cb.setTag(position);

                int userID = -1;
                if (Common.positionToUserID.containsKey(position)) {
                    userID = Common.positionToUserID.get(position);
                }
                if (Common.selectedUsers.contains(userID)) cb.setChecked(true);
                else cb.setChecked(false);

            }

        }
        return super.getView(position, convertView, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //assign the photo


        if (cursor.getString(DBRelated.USER_PHOTO_COL_INDEX) != null) {
            ImageView image = (ImageView) view.findViewById(R.id.friend_list_listview_photo);
            Integer getImage = context.getResources().getIdentifier(cursor.getString(DBRelated.USER_PHOTO_COL_INDEX), "drawable", context.getPackageName());
            if (getImage != 0)
                image.setImageDrawable(context.getResources().getDrawable(getImage));
        }


        //assign the nickname
        int userID = cursor.getInt(DBRelated.USER_ID_COL_INDEX);

        TextView status = (TextView) view.findViewById(R.id.friend_list_listview_status);
        //update participant status HashMap;
        if (checkForStatus)
            if (Common.participantsReady.containsKey(userID)
                    ) status.setText(DBRelated.participantStatusCodeToText(Common.participantsReady.get(userID)));
            else status.setText("");

        //Set the nickname a
        String nickText = "";
        TextView nick = (TextView) view.findViewById(R.id.friend_list_listview_nickname);
        if (userID == MainActivity.appUser.getId()) nickText = "Myself";
        else if (cursor.getString(DBRelated.USER_NICKNAME_COL_INDEX) != null)
            nickText = cursor.getString(DBRelated.USER_NICKNAME_COL_INDEX);
        nick.setText(nickText);


        //Set the checkboxx
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.friend_list_checkbutton);
        if (userID == MainActivity.appUser.getId()) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else
        {
            checkBox.setEnabled(true);
            //checkBox.setTag(new Integer(userID));
            int checkBoxPosition = -1;
            if (checkBox.getTag() != null) {
                checkBoxPosition = (Integer) checkBox.getTag();
            }
            if (checkBoxPosition != -1) {
                if (!Common.positionToUserID.containsKey(checkBoxPosition))
                    Common.positionToUserID.put(checkBoxPosition, userID);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int checkBoxPosition = (Integer) buttonView.getTag();
                    int userID = -1;
                    if (Common.positionToUserID.containsKey(checkBoxPosition)) {
                        userID = Common.positionToUserID.get(checkBoxPosition);
                    } else {
                        return;
                    }
                    if (isChecked) {
                        if (!Common.selectedUsers.contains(userID)) {
                            Common.selectedUsers.add(userID);
                        }
                    } else {
                        if (Common.selectedUsers.contains(userID))
                            Common.selectedUsers.remove(Common.selectedUsers.indexOf(userID));
                    }


                }
            });

        }


    }
}
