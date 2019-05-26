package com.example.foundlerv2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArrayAdaptor extends ArrayAdapter <Cards>{

    Context context;

    public ArrayAdaptor(Context context, int resourceId, List<Cards> items){
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from((getContext())).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.itemText);
        ImageView image = (ImageView) convertView.findViewById(R.id.itemImage);

        assert card_item != null;
        name.setText(card_item.getName());
        Log.d("tryme", card_item.getProfilePictureUrl());
        switch (card_item.getProfilePictureUrl()){
            case "default":
                Log.d("hit me", "hit here");
                image.setImageResource(R.mipmap.ic_launcher);
                break;

             default:
                 Glide.clear(image);
                 Glide.with(convertView.getContext()).load(card_item.getProfilePictureUrl()).into(image);
                 break;
        }

        return convertView;
    }
}
