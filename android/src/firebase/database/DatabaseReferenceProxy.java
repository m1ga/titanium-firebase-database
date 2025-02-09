/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package firebase.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import java.util.HashMap;

@Kroll.proxy(creatableInModule = FirebaseDatabaseModule.class)
public class DatabaseReferenceProxy extends KrollProxy {
    private DatabaseReference databaseReference;
    private final FirebaseDatabase database;

    // Constructor
    public DatabaseReferenceProxy(DatabaseReference dbr, FirebaseDatabase db) {
        super();
        databaseReference = dbr;
        database = db;

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KrollDict kd = new KrollDict();
                kd.put("key", dataSnapshot.getKey());
                kd.put("value", dataSnapshot.getValue());
                fireEvent("value", kd);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                KrollDict kd = new KrollDict();
                kd.put("type", "error");
                kd.put("message", databaseError.getMessage());
                fireEvent("error", kd);
            }
        };
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                KrollDict kd = new KrollDict();
                kd.put("data", snapshot.toString());
                fireEvent("add", kd);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                KrollDict kd = new KrollDict();
                kd.put("data", snapshot.toString());
                fireEvent("change", kd);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                KrollDict kd = new KrollDict();
                kd.put("data", snapshot.toString());
                fireEvent("remove", kd);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                KrollDict kd = new KrollDict();
                kd.put("data", snapshot.toString());
                fireEvent("move", kd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                KrollDict kd = new KrollDict();
                kd.put("type", "cancelled");
                kd.put("message", error.getMessage());
                fireEvent("error", kd);
            }
        };
        databaseReference.addValueEventListener(postListener);
        databaseReference.addChildEventListener(childListener);
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    @Kroll.method
    public DatabaseReferenceProxy child(KrollDict kd) {
        String path = kd.getString("path");
        String url = kd.getString("url");
        String identifier = kd.getString("identifier");
        if (path != "" && identifier != "") {
            databaseReference = database.getReference(path).child(identifier);
        } else if (url != "" && identifier != "") {
            databaseReference = database.getReferenceFromUrl(url).child(identifier);
        }
        return this;
    }

    @Kroll.method
    public DatabaseReferenceProxy childByAutoId(KrollDict kd) {
        String path = kd.getString("path");
        if (path != "") {
            databaseReference = database.getReference(path).push();
        }
        return this;
    }

    @Kroll.method
    public void setValue(HashMap data) {
        databaseReference.setValue(data);
    }

    @Kroll.method
    public void setValue(HashMap data, KrollFunction callback) {
        databaseReference.setValue(data);
        if (callback != null) {
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    KrollDict kd = new KrollDict();
                    kd.put("data", task.getResult().toString());
                    callback.call(getKrollObject(), kd);
                }
            });
        }
    }

    @Kroll.method
    public void removeValue() {
        databaseReference.removeValue();
        KrollDict kd = new KrollDict();
        kd.put("data", databaseReference.toString());
        fireEvent("remove", kd);
    }

    @Kroll.method
    public void updateChildValues(HashMap data) {
        databaseReference.updateChildren(data);
    }

    @Kroll.method
    public void goOnline() {
        database.goOnline();
    }

    @Kroll.method
    public void keepSynced(Boolean value) {
        databaseReference.keepSynced(value);
    }

    @Kroll.method
    public void setPriority(Object priority, KrollFunction callback) {
        databaseReference.setPriority(priority, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (callback != null) {
                    callback.call(getKrollObject(), new KrollDict());
                }
            }
        });
    }

    @Kroll.method
    public void goOffline() {
        database.goOffline();
    }

}
