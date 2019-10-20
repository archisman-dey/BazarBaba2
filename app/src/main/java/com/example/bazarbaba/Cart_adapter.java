package com.example.bazarbaba;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.AvailabilityException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ViewHolder> {
    private List<Product_details> itemList;
    private LayoutInflater layoutInflater;
    private Context context;
    private MyClickListener Myclicklistner;
//    private OnNoteListener mOnNoteListener;
//    private Button addcart;

    public Cart_adapter(List<Product_details> itemList, Context context, MyClickListener myClickListener) {
        this.itemList = itemList;
        this.context = context;
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Myclicklistner=myClickListener;

    }

    @NonNull
    @Override
    public Cart_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        ViewHolder holder=  new ViewHolder(v, Myclicklistner);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Cart_adapter.ViewHolder holder, int position) {
        Product_details ne=itemList.get(position);
        holder.bind(ne);
//        holder.addcart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                click();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        MyClickListener listener;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewShop;
        private ImageView image;
        private Product_details curentitem;
        //        OnNoteListener onNoteListener;
        Button addcart;

        public ViewHolder(@NonNull View itemView,MyClickListener listener) {
            super(itemView);
            textViewName=itemView.findViewById(R.id.item_name);
            textViewPrice=itemView.findViewById(R.id.item_price);
            textViewShop=itemView.findViewById(R.id.item_shop);
            addcart=(Button) itemView.findViewById(R.id.item_del_to_cart);
            image = itemView.findViewById(R.id.itemImage);
            this.listener=listener;
            addcart.setOnClickListener(this);
//            itemView.setOnClickListener(this);
//            addcart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    click();
//                    //            onNoteListener.onNoteClick(curentitem);
//
//                }
//            });

        }

        void bind (Product_details item) {
            textViewName.setText(item.getName());
            textViewPrice.setText(item.getPrice().toString());
            textViewShop.setText(item.getShop());
            curentitem=item;

            String sp=item.getShop().toString();

            if (sp.equals("Lohit Pure Veg"))
                image.setImageResource(R.drawable.lohit);
            else if (sp.equals("Lohit"))
                image.setImageResource(R.drawable.lohit);
            else if (sp.equals("Umiam"))
                image.setImageResource(R.drawable.umiam);
            else if (sp.equals("Brahmaputra"))
                image.setImageResource(R.drawable.brahmaputra);
            else if (sp.equals("Siang"))
                image.setImageResource(R.drawable.siang);
            else if (sp.equals("Barak"))
                image.setImageResource(R.drawable.barak);
            else if (sp.equals("Kameng"))
                image.setImageResource(R.drawable.kameng);
            else if (sp.equals("Manas"))
                image.setImageResource(R.drawable.manas);
            else if (sp.equals("Dihing"))
                image.setImageResource(R.drawable.dihing);
            else if (sp.equals("Khokha"))
                image.setImageResource(R.drawable.khokha);
            else if (sp.equals("Kapili"))
                image.setImageResource(R.drawable.kapili);
//            addcart.setOnClickListener(this);
        }
        //        public void click()
//        {
//            onNoteListener.onNoteClick(curentitem);
//        }
        @Override
        public void onClick(View v) {
            listener.ondd(curentitem);
        }

    }
    public interface MyClickListener{
        void ondd(Product_details it);
    }


    public void filterList(List<Product_details> filteredList) {
        itemList = filteredList;
        notifyDataSetChanged();
    }

}