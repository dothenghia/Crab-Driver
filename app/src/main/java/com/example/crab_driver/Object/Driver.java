package com.example.crab_driver.Object;

import android.util.Log;

import com.example.crab_driver.Manager.DocumentManager;
import com.example.crab_driver.Manager.FirestoreConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class Driver {
    private String ID;
    private String name;
    private String gender;
    private String email;
    private String phoneNumber;
    private Vehicle vehicle;
    private float score;
    public Driver() {
    }

    public void parseFromObject(Object documentData, OnParseCompleteListener listener) {
        Driver driver = new Driver();
        if (documentData != null) {
            Map<String, Object> data = (Map<String, Object>) documentData;
            driver.setName((String) data.get(FirestoreConstants.DRIVER_NAME));
            driver.setGender((String) data.get(FirestoreConstants.DRIVER_GENDER));
            driver.setEmail((String) data.get(FirestoreConstants.DRIVER_EMAIL));
            driver.setPhoneNumber((String) data.get(FirestoreConstants.DRIVER_PHONE_NUMBER));
            driver.setScore(((Number) data.get(FirestoreConstants.DRIVER_SCORE)).floatValue());
            Log.d("HoTen", driver.getName());

            // Parse the vehicle reference
            Object vehicleRefObj = data.get(FirestoreConstants.VEHICLE);
            if (vehicleRefObj instanceof DocumentReference) {
                DocumentReference vehicleRef = (DocumentReference) vehicleRefObj;
                Task<DocumentSnapshot> vehicleTask = vehicleRef.get();
                vehicleTask.addOnCompleteListener(vehicleTaskResult -> {
                    if (vehicleTaskResult.isSuccessful()) {
                        DocumentSnapshot vehicleSnapshot = vehicleTaskResult.getResult();
                        if (vehicleSnapshot.exists()) {
                            Vehicle vehicle = new Vehicle();

                            vehicle.setName((String) vehicleSnapshot.get(FirestoreConstants.VEHICLE_NAME));
                            vehicle.setNumber((String) vehicleSnapshot.get(FirestoreConstants.VEHICLE_NUMBER));
                            Log.d("TenXe", vehicle.getName());

                            DocumentReference vehicleTypeRef = (DocumentReference) vehicleSnapshot.get(FirestoreConstants.VEHICLE_TYPE);
                            Task<DocumentSnapshot> vehicleTypeTask = vehicleTypeRef.get();
                            vehicleTypeTask.addOnCompleteListener(vehicleTypeTaskResult -> {
                                if (vehicleTypeTaskResult.isSuccessful()) {
                                    DocumentSnapshot vehicleTypeSnapshot = vehicleTypeTaskResult.getResult();
                                    if (vehicleTypeSnapshot.exists()) {
                                        VehicleType vehicleType = new VehicleType();

                                        vehicleType.setName((String) vehicleTypeSnapshot.get(FirestoreConstants.VEHICLE_TYPE_NAME));
                                        vehicleType.setCapacity(((Number) vehicleTypeSnapshot.get(FirestoreConstants.VEHICLE_TYPE_CAPACITY)).intValue());
                                        vehicleType.setFee_1(((Number) vehicleTypeSnapshot.get(FirestoreConstants.VEHICLE_TYPE_FEE_1)).floatValue());
                                        vehicleType.setFee_2(((Number) vehicleTypeSnapshot.get(FirestoreConstants.VEHICLE_TYPE_FEE_2)).floatValue());
                                        vehicleType.setFee_3(((Number) vehicleTypeSnapshot.get(FirestoreConstants.VEHICLE_TYPE_FEE_3)).floatValue());
                                        Log.d("TenLoaiXe", vehicleType.getName());

                                        vehicle.setVehicleType(vehicleType);

                                        // Set the vehicle to the driver
                                        driver.setVehicle(vehicle);

                                        // Notify listener that parsing is complete
                                        listener.onParseComplete(driver);
                                    } else {
                                        listener.onParseFailed(new Exception("LoaiXe document does not exist"));
                                    }
                                } else {
                                    listener.onParseFailed(vehicleTypeTaskResult.getException());
                                }
                            });
                        } else {
                            listener.onParseFailed(new Exception("Vehicle document does not exist"));
                        }
                    } else {
                        listener.onParseFailed(vehicleTaskResult.getException());
                    }
                });
            } else {
                listener.onParseFailed(new Exception("Vehicle data is not a DocumentReference"));
            }
        } else {
            listener.onParseFailed(new Exception("Document data is null"));
        }
    }

    public interface OnParseCompleteListener {
        void onParseComplete(Driver driver);
        void onParseFailed(Exception e);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
