package com.jiang.android.lib.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public abstract class BaseAdapter<T,VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<T> items = new ArrayList<>();

    public BaseAdapter() {
        setHasStableIds(true);
    }

    public void add(T object) {
        if (!items.contains(object)) {
            items.add(object);
            notifyDataAddChanged(-1);
        }
    }

    public void add(int index, T object) {
        if (!items.contains(object)) {
            items.add(index, object);
            notifyDataAddChanged(index);
        }
    }

    public void addAll(Collection<T> collection) {
        if (collection != null) {
            items = new ArrayList<>();
            items.addAll(collection);
            notifyDataAddChanged(0);
        }
    }

    public void notifyDataAddChanged(int position){
        notifyDataSetChanged();
    }

    public void addAll(T... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(T object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        T t = getItem(position);
        if (t != null) {
            return t.hashCode();
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
