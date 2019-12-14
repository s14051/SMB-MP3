package com.example.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import todo_database.FirebaseProductDb;
import todo_database.Product;

public final class ManageProduct {
    private Context context;
    private FirebaseProductDb db;

    public ManageProduct(Context context) {
        this.context = context;
        this.db = new FirebaseProductDb();
    }

    public void showAddProductDialog() {
        showManageProductDialog(null);
    }

    public void showManageProductDialog(final Product productToEdit) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        final View productManageView = ((Activity)context).getLayoutInflater().inflate(R.layout.manage_product, null);

        Button saveButton = productManageView.findViewById(R.id.saveButton);
        Button cancelButton = productManageView.findViewById(R.id.cancelButton);
        TextView headerTextView = productManageView.findViewById(R.id.headerTextView);

        if (productToEdit == null) {
            headerTextView.setText("Dodawanie nowego produktu");
        }
        else {
            headerTextView.setText("Edycja produktu");
            final EditText nameEditText = productManageView.findViewById(R.id.nameEditText);
            final EditText priceEditText = productManageView.findViewById(R.id.priceEditText);
            final EditText amountEditText = productManageView.findViewById(R.id.amountEditText);

            nameEditText.setText(productToEdit.getName());
            priceEditText.setText(String.valueOf(productToEdit.getPrice()));
            amountEditText.setText(String.valueOf(productToEdit.getCount()));
        }

        alertBuilder.setView(productManageView);
        final AlertDialog dialog = alertBuilder.create();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View btn) {
                onSaveButtonClick(productManageView, dialog, productToEdit);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void onSaveButtonClick(View view, AlertDialog dialog, Product productToEdit) {
        final EditText nameEditText = view.findViewById(R.id.nameEditText);
        final EditText priceEditText = view.findViewById(R.id.priceEditText);
        final EditText amountEditText = view.findViewById(R.id.amountEditText);

        String name = nameEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String amountStr = amountEditText.getText().toString();

        String error = ValidateFields(name, priceStr, amountStr);

        if (error != null) {
            Toast.makeText(view.getContext(), error, Toast.LENGTH_LONG).show();
        }
        else {
            double price = Double.parseDouble(priceStr);
            int amount = Integer.parseInt(amountStr);

            if (productToEdit == null){
                db.addProduct(new Product("", name, price, amount, false));
            }
            else {
                productToEdit.setName(name);
                productToEdit.setPrice(price);
                productToEdit.setCount(amount);
                db.updateProduct(productToEdit);
            }

            dialog.dismiss();
        }
    }


    public void showDeleteProductDialog(final Context context, final Product productToDelete) {
        new AlertDialog.Builder(context)
            .setTitle("Usuwanie produktu")
            .setMessage("Czy na pewno chcech usunąć ten produkt?")
            .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.deleteProduct(productToDelete);
                }
            })
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show();
    }


    private String ValidateFields(String name, String priceStr, String amountStr) {
        if (name == null || name.length() == 0 || priceStr == null || priceStr.length() == 0 || amountStr == null || amountStr.length() == 0)
            return "Wszystkie pola są wymagane";

        double price = Double.parseDouble(priceStr);
        int amount = Integer.parseInt(amountStr);

        if (name.length() > 50) return "Nazwa nie może przekraczać 50 znaków";
        if (price < 0) return "Cena nie może być ujemna";
        if (amount < 0) return "Ilość nie może być ujemna";

        return null;
    }

    public void onIsBoughtCheckedChange(String idProduct, boolean isBought){
        db.updateIsBought(idProduct, isBought);
    }
}
