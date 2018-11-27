package csc472.depaul.edu.onlineeats;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CartItemViewHolder extends RecyclerView.ViewHolder {

    private RelativeLayout layout;
    private TextView name;
    private TextView price;
    private TextView qty;

    private ImageView up;
    private ImageView down;

    public CartItemViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.cart_layout);
        name = itemView.findViewById(R.id.cart_name);
        price = itemView.findViewById(R.id.cart_price);
        qty = itemView.findViewById(R.id.cart_qty);
        up = itemView.findViewById(R.id.cart_up_arrow);
        down = itemView.findViewById(R.id.cart_down_arrow);

    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public ImageView getUp() { return up; }

    public ImageView getDown() { return down; }

    public void setName(String string) {
        name.setText(string);
    }

    public void setPrice(String string) {
        price.setText(string);
    }

    public void setQty(String string) { qty.setText(string); }
}
