package com.shortshop.shortcutapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFrag extends Fragment implements View.OnClickListener{
    private static final String TAB_PAGE = "";
    private static final String TAB_POS = "";
    int position=0;
    private View view;
    private RecyclerView recycleview;
    private RecyclerView.Adapter mAdapter;

    public static HomeFrag newInstance(String tab,int from) {
        Bundle args = new Bundle();
        args.putString(TAB_PAGE, tab);
        args.putInt(TAB_POS, from);
        HomeFrag fragment = new HomeFrag();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(TAB_POS);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homefrag, container,
                false);
        recycleview = (RecyclerView)view.findViewById(R.id.recycleview);
        recycleview.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        recycleview.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(getDataArray(position,false), getDataArray(position,true));
        recycleview.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onClick(View v) {
    }

    public JSONArray getDataArray(int array_index, boolean isUrlArray){
        JSONArray result_array = new JSONArray();
        try{
            JSONObject jsonobj = new JSONObject(MainActivity.resp);
            JSONArray urls_array = jsonobj.getJSONArray("urls");
            JSONArray images_array = jsonobj.getJSONArray("images");
            if(isUrlArray){
                result_array = urls_array.getJSONArray(array_index);
            }else{
                result_array = images_array.getJSONArray(array_index);
            }
            Log.e("urls_array",""+result_array);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result_array;
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private JSONArray imagesArray;
        private JSONArray urlsArray;

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView url_tv;

            ViewHolder(View v) {
                super(v);
                icon = (ImageView) v.findViewById(R.id.image);
                url_tv = (TextView) v.findViewById(R.id.url_tv);
            }
        }

        MyAdapter(JSONArray imagesArray, JSONArray urlsArray) {
            this.imagesArray = imagesArray;
            this.urlsArray = urlsArray;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
//                Log.e("imv_url",""+MainActivity.BASE_URL +imagesArray.getString(position));
                Glide.with(getContext())
                        .load(MainActivity.BASE_URL +imagesArray.getString(position))
                        .into(holder.icon);
                holder.url_tv.setText(urlsArray.getString(position));
                holder.icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent i = new Intent(getActivity(),BrowseFrag.class);
                            i.putExtra("url",""+urlsArray.getString(position));
                            i.putExtra("name",""+imagesArray.getString(position));
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return imagesArray.length();
        }

    }


}
