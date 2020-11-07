package com.example.aptech.greenfox;

import android.net.Uri;

public class itemDetailsModelClass {

    public String ItemPrice = "";
    public Uri ItemPic;
    public String ItemName = "";
    public String PostID = "";

    public String getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public Uri getItemPic() {
        return ItemPic;
    }

    public void setItemPic(String itemPic) {
        ItemPic = Uri.parse(itemPic);
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }
}
