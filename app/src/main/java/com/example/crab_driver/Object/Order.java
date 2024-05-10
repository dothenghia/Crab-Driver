package com.example.crab_driver.Object;

import com.example.crab_driver.Manager.FirestoreConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

public class Order implements Serializable {
    private float fee;
    private String bookingMethod;
    private Address destination;
    private Address pickup;
    private Customer customer;
    private VehicleType vehicleType;
    private Driver driver;
    private String startTimestamp;
    private String endTimestamp;

    public Order() {}

    public void parseFromObject(Object documentData, OnParseCompleteListener listener) {
        if (documentData == null) {
            listener.onParseFailed(new Exception("Document data is null"));
            return;
        }

        Map<String, Object> data = (Map<String, Object>) documentData;
        fee = ((Number) data.get(FirestoreConstants.ORDER_FEE)).floatValue();
        bookingMethod = (String) data.get(FirestoreConstants.ORDER_METHOD);

        parseAddress(data, FirestoreConstants.ORDER_DESTINATION, new OnAddressParseCompleteListener() {
            @Override
            public void onAddressParseComplete(Address address) {
                destination = address;
                parseAddress(data, FirestoreConstants.ORDER_PICKUP, new OnAddressParseCompleteListener() {
                    @Override
                    public void onAddressParseComplete(Address address) {
                        pickup = address;
                        parseCustomer(data, FirestoreConstants.ORDER_CUSTOMER, new OnCustomerParseCompleteListener() {
                            @Override
                            public void onCustomerParseComplete(Customer parsedCustomer) {
                                customer = parsedCustomer;

                                String driverId = (String) data.get(FirestoreConstants.ORDER_DRIVER);
                                if (driverId.isEmpty()) {
                                    driver = null;
                                } else {
                                    driver = new Driver();
                                }

                                listener.onParseComplete(Order.this);
                            }

                            @Override
                            public void onCustomerParseFailed(Exception e) {
                                listener.onParseFailed(e);
                            }
                        });
                    }

                    @Override
                    public void onAddressParseFailed(Exception e) {
                        listener.onParseFailed(e);
                    }
                });
            }

            @Override
            public void onAddressParseFailed(Exception e) {
                listener.onParseFailed(e);
            }
        });
    }

    private void parseCustomer(Map<String, Object> data, String referenceKey, OnCustomerParseCompleteListener listener) {
        Object customerRefObj = data.get(referenceKey);
        if (customerRefObj instanceof DocumentReference) {
            DocumentReference customerRef = (DocumentReference) customerRefObj;
            customerRef.get().addOnCompleteListener(referenceTask -> {
                if (referenceTask.isSuccessful()) {
                    DocumentSnapshot customerSnapshot = referenceTask.getResult();
                    if (customerSnapshot.exists()) {
                        Customer customer = new Customer();
                        customer.setName((String) customerSnapshot.get(FirestoreConstants.CUSTOMER_NAME));
                        customer.setPhoneNumber((String) customerSnapshot.get(FirestoreConstants.CUSTOMER_PHONE));

                        listener.onCustomerParseComplete(customer);
                    } else {
                        listener.onCustomerParseFailed(new Exception("Referenced document does not exist"));
                    }
                } else {
                    listener.onCustomerParseFailed(referenceTask.getException());
                }
            });
        } else {
            listener.onCustomerParseFailed(new Exception("Reference data is not a DocumentReference"));
        }
    }

    private void parseAddress(Map<String, Object> data, String addressKey, OnAddressParseCompleteListener listener) {
        Object addressRefObj = data.get(addressKey);
        if (addressRefObj instanceof DocumentReference) {
            DocumentReference addressRef = (DocumentReference) addressRefObj;
            addressRef.get().addOnCompleteListener(addressTask -> {
                if (addressTask.isSuccessful()) {
                    DocumentSnapshot addressSnapshot = addressTask.getResult();
                    if (addressSnapshot.exists()) {
                        Address address = new Address();
                        address.setAddress((String) addressSnapshot.get(FirestoreConstants.ADDRESS_NAME));
                        address.setLongtitude(((Number) addressSnapshot.get(FirestoreConstants.ADDRESS_LONGTITUDE)).floatValue());
                        address.setLatitude(((Number) addressSnapshot.get(FirestoreConstants.ADDRESS_LATITUDE)).floatValue());
                        listener.onAddressParseComplete(address);
                    } else {
                        listener.onAddressParseFailed(new Exception("Address document does not exist"));
                    }
                } else {
                    listener.onAddressParseFailed(addressTask.getException());
                }
            });
        } else {
            listener.onAddressParseFailed(new Exception("Address data is not a DocumentReference"));
        }
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public String getBookingMethod() {
        return bookingMethod;
    }

    public void setBookingMethod(String bookingMethod) {
        this.bookingMethod = bookingMethod;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public Address getPickup() {
        return pickup;
    }

    public void setPickup(Address pickup) {
        this.pickup = pickup;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public interface OnParseCompleteListener {
        void onParseComplete(Order order);
        void onParseFailed(Exception e);
    }

    private interface OnAddressParseCompleteListener {
        void onAddressParseComplete(Address address);
        void onAddressParseFailed(Exception e);
    }

    private interface OnCustomerParseCompleteListener {
        void onCustomerParseComplete(Customer customer);
        void onCustomerParseFailed(Exception e);
    }
}
