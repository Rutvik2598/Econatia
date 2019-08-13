package com.company.econatia.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.econatia.Model.Product;
import com.company.econatia.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {

    private Context mContext;
    private List<Product> mProduct;


    public RewardsAdapter(Context mContext, List<Product> mProduct) {
        this.mContext = mContext;
        this.mProduct = mProduct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.econs_redeem_item , viewGroup , false);
        return new RewardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Product product = mProduct.get(i);

        Glide.with(mContext).load(product.getProductImgUrl())
                .apply(new RequestOptions().override(1000 , 1000))
                .into(viewHolder.product_image);

        viewHolder.product_name.setText(product.getProductName());
        viewHolder.product_desc.setText(product.getGetProductDesc());
        viewHolder.product_cost.setText(product.getProductCost());

    }


    @Override
    public int getItemCount() {
        return mProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView product_image;
        public TextView product_name , product_desc , product_cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_desc = itemView.findViewById(R.id.product_description);
            product_cost = itemView.findViewById(R.id.product_econs);

        }
    }

}
