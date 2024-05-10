package com.example.crab_driver.Manager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DocumentManager {

    private String collectionName;
    private FirebaseFirestore db;
    private DocumentReference documentRef;

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

    public void getDocumentsWhereEqualTo(String fieldName1, Object value1, String fieldName2, Object value2, final OnDocumentsLoadedListener listener) {
        Query query = db.collection(collectionName)
                .whereEqualTo(fieldName1, value1)
                .whereEqualTo(fieldName2, value2);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    listener.onDocumentsLoaded(querySnapshot.getDocuments());
                } else {
                    listener.onDocumentsNotFound();
                }
            } else {
                listener.onError(task.getException());
            }
        });
    }

    // Listener interface for document data callbacks
    public interface OnDocumentLoadedListener {
        void onDocumentLoaded(Object documentData);

        void onDocumentNotFound();

        void onError(Exception e);
    }

    // Listener interface for multiple documents data callbacks
    public interface OnDocumentsLoadedListener {
        void onDocumentsLoaded(List<DocumentSnapshot> documents);

        void onDocumentsNotFound();

        void onError(Exception e);
    }
}
