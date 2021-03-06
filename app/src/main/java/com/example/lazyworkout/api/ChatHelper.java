package com.example.lazyworkout.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

// Return chat collection from firestore
public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static boolean checkString(String[] value) {
        if (value[0] == null) {
            return true;
        } else {
            return false;
        }
    }
}