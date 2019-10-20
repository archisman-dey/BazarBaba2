package com.example.bazarbaba;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class Product_details implements Parcelable {
    private String Name;
    private String Shop;
    private Long Price;
//    private ImageView image;

    public Product_details(String Name,String Shop,Long Price)
    {
        this.Name=Name;
        this.Shop=Shop;
        this.Price=Price;
        //this.setImage();
    }

//    private void setImage()
//    {
//        if (this.Shop.equals("Lohit Pure Veg"))
//            this.image.setImageResource(R.drawable.lohit);
//        else if (this.Shop.equals("Lohit"))
//            this.image.setImageResource(R.drawable.lohit);
//        else if (this.Shop.equals("Umiam"))
//            this.image.setImageResource(R.drawable.umiam);
//        else if (this.Shop.equals("Brahmaputra"))
//            this.image.setImageResource(R.drawable.brahmaputra);
//        else if (this.Shop.equals("Siang"))
//            this.image.setImageResource(R.drawable.siang);
//        else if (this.Shop.equals("Barak"))
//            this.image.setImageResource(R.drawable.barak);
//        else if (this.Shop.equals("Kameng"))
//            this.image.setImageResource(R.drawable.kameng);
//        else if (this.Shop.equals("Manas"))
//            this.image.setImageResource(R.drawable.manas);
//        else if (this.Shop.equals("Dihing"))
//            this.image.setImageResource(R.drawable.dihing);
//        else if (this.Shop.equals("Khokha"))
//            this.image.setImageResource(R.drawable.khokha);
//        else if (this.Shop.equals("Kapili"))
//            this.image.setImageResource(R.drawable.kapili);
//    }

    protected Product_details(Parcel in) {
        Name = in.readString();
        Shop = in.readString();
        if (in.readByte() == 0) {
            Price = null;
        } else {
            Price = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Shop);
        if (Price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(Price);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product_details> CREATOR = new Creator<Product_details>() {
        @Override
        public Product_details createFromParcel(Parcel in) {
            return new Product_details(in);
        }

        @Override
        public Product_details[] newArray(int size) {
            return new Product_details[size];
        }
    };

    public String getName() {
        return Name;
    }

    public String getShop() {
        return Shop;
    }

    public Long getPrice() {
        return Price;
    }
}
