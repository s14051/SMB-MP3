package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import todo_database.FirebaseProductDb;
import todo_database.Product;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.rv1);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        productList = new ArrayList<>();
        final ProductAdapter productAdapter = new ProductAdapter(this, productList);
        initProductList(productAdapter);
        recyclerView.setAdapter(productAdapter);
    }

    public interface DbChangeCallback {
        void addProduct(Product newProduct);
        void deleteProduct(String productId);
        void updateProduct(Product updatedProduct);
    }

    public void initProductList(final ProductAdapter productAdapter){
        DbChangeCallback i = new DbChangeCallback() {
            @Override
            public void addProduct(Product newProduct) {
                productList.add(newProduct);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void deleteProduct(String productId) {
                Product productToRemove = null;
                for (Product p : productList) {
                    if (p.getProductId().equals(productId)){
                        productToRemove = p;
                        break;
                    }
                }

                productList.remove(productToRemove);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void updateProduct(Product updatedProduct) {
                for (Product p : productList) {
                    if (p.getProductId().equals(updatedProduct.getProductId())){
                        p.setName(updatedProduct.getName());
                        p.setCount(updatedProduct.getCount());
                        p.setPrice(updatedProduct.getPrice());
                        p.setBought(updatedProduct.getBought());
                        break;
                    }
                }

                productAdapter.notifyDataSetChanged();
            }
        };

        FirebaseProductDb db = new FirebaseProductDb();
        db.initProducts(i);
    }

    public void addButtonClick(View view) {
        ManageProduct manageProduct = new ManageProduct(this);
        manageProduct.showAddProductDialog();
    }
}
