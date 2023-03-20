package edu.northeastern.cs5500.starterbot.service;

public class FedexReference {
    private String carrierCode;
    private String type;
    private String value;
    private String accountNumber;
    private String shipDateBegin;
    private String shipDateEnd;
    private String destinationCountryCode;
    private String destinationPostalCode;

    public FedexReference(String carrierCode, String type, String value, String accountNumber, String shipDateBegin, String shipDateEnd, String destinationCountryCode, String destinationPostalCode) {
        this.carrierCode = carrierCode;
        this.type = type;
        this.value = value;
        this.accountNumber = accountNumber;
        this.shipDateBegin = shipDateBegin;
        this.shipDateEnd = shipDateEnd;
        this.destinationCountryCode = destinationCountryCode;
        this.destinationPostalCode = destinationPostalCode;
    }

    public FedexReference(String value, String shipDateBegin, String shipDateEnd) {
        this.value = value;
        this.shipDateBegin = shipDateBegin;
        this.shipDateEnd = shipDateEnd;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getShipDateBegin() {
        return shipDateBegin;
    }

    public String getShipDateEnd() {
        return shipDateEnd;
    }

    public String getDestinationCountryCode() {
        return destinationCountryCode;
    }

    public String getDestinationPostalCode() {
        return destinationPostalCode;
    }
}