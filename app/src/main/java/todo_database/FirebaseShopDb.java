package todo_database;

import android.util.Log;

import com.example.todo.ui.shopsList.ShopsListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseShopDb {
    private FirebaseDatabase _db;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    public FirebaseShopDb() {
        _db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = _db.getReference("users").child(user.getUid()).child("shops");
    }

    public void addShop(Shop newShop) {
        DatabaseReference dr = databaseReference.push();
        newShop.setId(dr.getKey());
        dr.setValue(newShop);
    }

    public void updateShop(Shop updatedShop) {
        DatabaseReference dr = databaseReference.child(updatedShop.getId());
        dr.setValue(updatedShop);
    }

    public void deleteShop(Shop shopToRemove) {
        databaseReference.child(shopToRemove.getId()).removeValue();
    }

    public void initFirebaseListeners(final ShopsListFragment.DbChangeCallback callback) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                shop.setId(dataSnapshot.getKey());

                callback.addShop(shop);
                Log.i("SHOP_ADD", "Sklep: " + shop.getName() + ", opis: " + shop.getDescription() + ", koordynaty:" + shop.getCoordinates() + ", radius:" + shop.getRadius());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                shop.setId(dataSnapshot.getKey());

                callback.updateShop(shop);
                Log.i("SHOP_UPDATE", "Sklep: " + shop.getName() + ", opis: " + shop.getDescription() + ", koordynaty:" + shop.getCoordinates() + ", radius:" + shop.getRadius());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                callback.deleteShop(dataSnapshot.getKey());
                Log.i("SHOP_DELETE", "Usunieto sklep o id: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
