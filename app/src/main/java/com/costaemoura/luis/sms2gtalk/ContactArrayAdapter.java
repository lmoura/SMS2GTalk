package com.costaemoura.luis.sms2gtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ContactArrayAdapter extends ArrayAdapter<Contact> {

    private int resource;

    public ContactArrayAdapter(Context context, int textViewResourceId, List<Contact> objects) {
        super(context, textViewResourceId, objects);
        this.resource = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout contactView;
        Contact contactItem = getItem(position);

        if (convertView == null) {
            contactView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, contactView, true);
        } else {
            contactView = (LinearLayout) convertView;
        }

        TextView txtName = (TextView) contactView.findViewById(R.id.txtName);
        TextView txtStatus = (TextView) contactView.findViewById(R.id.txtStatus);
        ImageView appIcon = (ImageView) contactView.findViewById(R.id.imgContact);

        txtName.setText(contactItem.getName());
        txtStatus.setText(contactItem.getStatus());
        appIcon.setImageDrawable(contactItem.getImage());

        return contactView;
    }

}

