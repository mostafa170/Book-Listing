package com.example.android.booklisting.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.booklisting.List.ListBook;
import com.example.android.booklisting.R;

/**
 * Created by mostafa on 06/07/2017.
 */

public class ListBookAdapter extends ArrayAdapter<ListBook> {

    public ListBookAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ListBook book = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView author = (TextView) view.findViewById(R.id.author);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());

        return view;
    }
}
