package com.example.todo;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import todo_database.FirebaseDb;
import todo_database.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.productList = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_element, parent, false);
        ProductViewHolder pvh = new ProductViewHolder(view);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (productList != null) {
            Product product = productList.get(position);
            holder.setProductId(product.getProductId());
            holder.getNameTextView().setText(product.getName());
            holder.getPriceTextView().setText(product.getPrice() + "");
            holder.getCountTextView().setText(product.getCount() + "");
            holder.getIsBoughtCheckBox().setChecked(product.getBought());
        }
    }

    @Override
    public int getItemCount() {
        if (productList != null)
            return productList.size();
        else return 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private String productId;
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView countTextView;
        private CheckBox isBoughtCheckBox;
        private ManageProduct manageProduct;

        public ProductViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.nameTextView);
            this.priceTextView = itemView.findViewById(R.id.priceTextView);
            this.countTextView = itemView.findViewById(R.id.countTextView);
            this.isBoughtCheckBox = itemView.findViewById(R.id.isBoughtCheckBox);
            this.manageProduct = new ManageProduct(context);

            itemView.setOnClickListener(this);

            isBoughtCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    manageProduct.onIsBoughtCheckedChange(productId, isChecked);
                }
            });

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
                    final Product selectedProduct = new Product(
                            productId,
                            nameTextView.getText().toString(),
                            Double.parseDouble(priceTextView.getText().toString()),
                            Integer.parseInt(countTextView.getText().toString()),
                            isBoughtCheckBox.isSelected());

                    menu.add(0, 0, 0, context.getString(R.string.edit)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            manageProduct.showManageProductDialog(selectedProduct);
                            return false;
                        }
                    });

                    menu.add(0, 1, 1, context.getString(R.string.delete)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            manageProduct.showDeleteProductDialog(itemView.getContext(), selectedProduct);
                            return false;
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View v) {
        }


        public TextView getNameTextView() {
            return nameTextView;
        }
        public TextView getPriceTextView() {
            return priceTextView;
        }
        public TextView getCountTextView() {
            return countTextView;
        }
        public CheckBox getIsBoughtCheckBox() {
            return isBoughtCheckBox;
        }
        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}
