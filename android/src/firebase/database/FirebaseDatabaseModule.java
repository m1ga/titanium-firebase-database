/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2018 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package firebase.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


@Kroll.module(name = "FirebaseDatabase", id = "firebase.database")
public class FirebaseDatabaseModule extends KrollModule {

    @Kroll.constant
    public static final int DATA_EVENT_TYPE_CHILD_ADDED = 0;
    @Kroll.constant
    public static final int DATA_EVENT_TYPE_VALUE = 1;
    FirebaseDatabase database;
    FirebaseFirestore databaseFirestore;
    String TAG = "FirebaseDatabase";

    // You can define constants with @Kroll.constant, for example:
    // @Kroll.constant public static final String EXTERNAL_NAME = value;

    public FirebaseDatabaseModule() {
        super();
    }

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app) {
    }

    @Kroll.method
    public void queryFirestore(KrollDict kd) {
        databaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference cr = databaseFirestore.collection(kd.getString("collection"));

        if (kd.containsKeyAndNotNull("document")) {
            DocumentReference dr = cr.document(kd.getString("document"));
            dr.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    KrollDict kdOutputData = new KrollDict();
                    Object[] documentData = new Object[1];
                    DocumentSnapshot document = task.getResult();
                    KrollDict docData = new KrollDict();
                    docData.put("id", document.getId());
                    try {
                        docData.put("data", mapToJSON(document.getData()).toString());
                    } catch (JSONException e) {
                    }
                    documentData[0] = docData;
                    kdOutputData.put("data", documentData);
                    fireEvent("query", kdOutputData);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        } else {
            cr.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    KrollDict kdOutputData = new KrollDict();
                    Object[] documentData = new Object[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        KrollDict docData = new KrollDict();
                        docData.put("id", document.getId());

                        try {
                            docData.put("data", mapToJSON(document.getData()).toString());
                        } catch (JSONException e) {
                        }
                        documentData[i] = docData;
                        i++;
                    }
                    kdOutputData.put("data", documentData);
                    fireEvent("query", kdOutputData);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        }
    }

    private JSONObject mapToJSON(Map<String, Object> map) throws JSONException {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) value;
                obj.put(key, mapToJSON(subMap));
            } else if (value instanceof List) {
                obj.put(key, listToJSONArray((List) value));
            } else {
                obj.put(key, value);
            }
        }
        return obj;
    }

    private JSONArray listToJSONArray(List<Object> list) throws JSONException {
        JSONArray arr = new JSONArray();
        for (Object obj : list) {
            if (obj instanceof Map) {
                arr.put(mapToJSON((Map) obj));
            } else if (obj instanceof List) {
                arr.put(listToJSONArray((List) obj));
            } else {
                arr.put(obj);
            }
        }
        return arr;
    }

    @Kroll.method
    public DatabaseReferenceProxy getReference(@Kroll.argument(optional = true) KrollDict kd) {
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbr;
        if (kd != null) {
            if (kd.containsKeyAndNotNull("path")) {
                String path = kd.getString("path");
                dbr = database.getReference(path);
            } else if (kd.containsKeyAndNotNull("url")) {
                String url = kd.getString("url");
                dbr = database.getReferenceFromUrl(url);
            } else {
                dbr = database.getReference();
            }

            if (kd.containsKeyAndNotNull("observableEvents")) {
                int[] intArray = kd.getIntArray("observableEvents");
                if (ArrayUtils.contains(intArray, DATA_EVENT_TYPE_CHILD_ADDED)) {
                    dbr.addChildEventListener(new ChildEventListener() {

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

                        }
                    });
                } else if (ArrayUtils.contains(intArray, DATA_EVENT_TYPE_VALUE)) {
                    dbr.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            KrollDict kd = new KrollDict();
                            kd.put("data", snapshot.toString());
                            fireEvent("change", kd);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        } else {
            dbr = database.getReference();
        }
        DatabaseReferenceProxy drp = new DatabaseReferenceProxy(dbr, database);
        return drp;
    }

    @Kroll.getProperty
    public Long getFirebaseServerTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

}