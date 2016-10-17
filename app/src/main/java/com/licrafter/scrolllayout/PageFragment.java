package com.licrafter.scrolllayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * author: shell
 * date 16/10/9 下午2:40
 **/
public class PageFragment extends Fragment {

    private RecyclerView recyclerView;
    private int mPosition;

    public static PageFragment getInstance(int position){
        PageFragment fragment = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosition = getArguments().getInt("position");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new GoodsAdapter());
    }

    private class GoodsAdapter extends RecyclerView.Adapter<GoodsVH> {

        @Override
        public GoodsVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GoodsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false));
        }

        @Override
        public void onBindViewHolder(GoodsVH holder, int position) {
            holder.textView.setText("商品类别->"+mPosition+"  位置->"+position);
        }

        @Override
        public int getItemCount() {
            return 40;
        }
    }

    private class GoodsVH extends RecyclerView.ViewHolder {

        public TextView textView;
        public GoodsVH(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"点击商品",Toast.LENGTH_SHORT).show();
                    android.util.Log.d("ljx","click Goods");
                }
            });
        }
    }

}
