package com.example.crab_driver.Manager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DocumentManager {

    private String collectionName;
    private FirebaseFirestore db;
    private DocumentReference documentRef;

    public DocumentManager() {
        db = FirebaseFirestore.getInstance();
    }

    public DocumentManager(String collectionName) {
        db = FirebaseFirestore.getInstance();
        this.collectionName = collectionName;
    }

    // Add a document to Firestore
    public void addDocument(String documentName, Object documentData) {
        documentRef = db.collection(collectionName).document(documentName);
        documentRef.set(documentData);
    }

    // Get data of a document from Firestore

    public void getDocumentDataByPath(String documentPath, final OnDocumentLoadedListener listener) {
        documentRef = db.document(documentPath);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        listener.onDocumentLoaded(document.getData());
                    } else {
                        listener.onDocumentNotFound();
                    }
                } else {
                    listener.onError(task.getException());
                }
            }
        });
    }

    public void getDocumentData(String documentName, final OnDocumentLoadedListener listener) {
        documentRef = db.collection(collectionName).document(documentName);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        listener.onDocumentLoaded(document.getData());
                    } else {
                        listener.onDocumentNotFound();
                    }
                } else {
                    listener.onError(task.getException());
                }
            }
        });
    }

    // Listener interface for document data callbacks
    public interface OnDocumentLoadedListener {
        void onDocumentLoaded(Object documentData);

        void onDocumentNotFound();

        void onError(Exception e);
    }
}
