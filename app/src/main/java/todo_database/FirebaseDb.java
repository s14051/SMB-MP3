package todo_database;

import android.util.Log;

import com.example.todo.ListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDb {
    private FirebaseDatabase _db;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    public FirebaseDb() {
        _db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = _db.getReference("users").child(user.getUid()).child("products");
    }

    public void addProduct(Product newProduct) {
        DatabaseReference dr = databaseReference.push();
        newProduct.setProductId(dr.getKey());
        dr.setValue(newProduct);
    }

    public void updateProduct(Product updatedProduct) {
        DatabaseReference dr = databaseReference.child(updatedProduct.getProductId());
        dr.setValue(updatedProduct);
    }

    public void updateIsBought(String productId, boolean isBought) {
        DatabaseReference dr = databaseReference.child(productId);
        dr.child("bought").setValue(isBought);
    }

    public void deleteProduct(Product productToRemove) {
        databaseReference.child(productToRemove.getProductId()).removeValue();
    }

    public void initProducts(final ListActivity.DbChangeCallback callback) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Product p = dataSnapshot.getValue(Product.class);
                p.setProductId(dataSnapshot.getKey());

                callback.addProduct(p);
                Log.i("TODO_ADD", "Produkt: " + p.getName() + ", price: " + p.getPrice() + ", count:" + p.getCount() + ", bought:" + p.getBought());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Product p = dataSnapshot.getValue(Product.class);
                p.setProductId(dataSnapshot.getKey());

                callback.updateProduct(p);
                Log.i("TODO_UPDATE", "Produkt: " + p.getName() + ", price: " + p.getPrice() + ", count:" + p.getCount() + ", bought:" + p.getBought());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                callback.deleteProduct(dataSnapshot.getKey());
                Log.i("TODO_DELETE", "Produkt o id: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
