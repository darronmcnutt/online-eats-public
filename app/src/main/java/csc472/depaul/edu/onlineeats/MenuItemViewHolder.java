package csc472.depaul.edu.onlineeats;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuItemViewHolder extends RecyclerView.ViewHolder {

    private LinearLayout layout;
    private TextView name;
    private TextView description;
    private TextView category;
    private TextView price;
    private ImageView image;

    public MenuItemViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.list_layout);
        name = itemView.findViewById(R.id.list_name);
        description = itemView.findViewById(R.id.list_description);
        category = itemView.findViewById(R.id.list_category);
        price = itemView.findViewById(R.id.list_price);
        image = itemView.findViewById(R.id.list_image);

    }

    public LinearLayout getLayout() {
        return layout;
    }

    public ImageView getImage() {
        return image;
    }

    public void setName(String string) {
        name.setText(string);
    }

    public void setDescription(String string) {
        description.setText(string);
    }

    public void setCategory(String string) {
        category.setText(string);
    }

    public void setPrice (String string) {
        price.setText(string);
    }
}
