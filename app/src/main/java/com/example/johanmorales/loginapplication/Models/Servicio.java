package com.example.johanmorales.loginapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;

public class Servicio implements Parcelable {

    public Array agents;
    public String airline;
    public String billingType;
    public String destinyCity;
    public String endDate;
    public String endZone;
    public String flightConnectionId;
    public String flightDateTime;
    public String flightId;
    public String id;
    public String originCity;
    public String paxName;
    public String serviceTerminal;
    public String serviceType;
    public String startDate;
    public String status;
    public String statusCode;

    public Servicio(Parcel in) {
        airline = in.readString();
        billingType = in.readString();
        destinyCity = in.readString();
        endDate = in.readString();
        endZone = in.readString();
        flightConnectionId = in.readString();
        flightDateTime = in.readString();
        flightId = in.readString();
        id = in.readString();
        originCity = in.readString();
        paxName = in.readString();
        serviceTerminal = in.readString();
        serviceType = in.readString();
        startDate = in.readString();
        status = in.readString();
        statusCode = in.readString();
    }

    public static final Creator<Servicio> CREATOR = new Creator<Servicio>() {
        @Override
        public Servicio createFromParcel(Parcel in) {
            return new Servicio(in);
        }

        @Override
        public Servicio[] newArray(int size) {
            return new Servicio[size];
        }
    };

    public Servicio() {

    }

    public Array getAgents() {
        return agents;
    }

    public void setAgents(Array agents) {
        this.agents = agents;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getDestinyCity() {
        return destinyCity;
    }

    public void setDestinyCity(String destinyCity) {
        this.destinyCity = destinyCity;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndZone() {
        return endZone;
    }

    public void setEndZone(String endZone) {
        this.endZone = endZone;
    }

    public String getFlightConnectionId() {
        return flightConnectionId;
    }

    public void setFlightConnectionId(String flightConnectionId) {
        this.flightConnectionId = flightConnectionId;
    }

    public String getFlightDateTime() {
        return flightDateTime;
    }

    public void setFlightDateTime(String flightDateTime) {
        this.flightDateTime = flightDateTime;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getPaxName() {
        return paxName;
    }

    public void setPaxName(String paxName) {
        this.paxName = paxName;
    }

    public String getServiceTerminal() {
        return serviceTerminal;
    }

    public void setServiceTerminal(String serviceTerminal) {
        this.serviceTerminal = serviceTerminal;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(airline);
        dest.writeString(billingType);
        dest.writeString(destinyCity);
        dest.writeString(endDate);
        dest.writeString(endZone);
        dest.writeString(flightConnectionId);
        dest.writeString(flightDateTime);
        dest.writeString(flightId);
        dest.writeString(id);
        dest.writeString(originCity);
        dest.writeString(paxName);
        dest.writeString(serviceTerminal);
        dest.writeString(serviceType);
        dest.writeString(startDate);
        dest.writeString(status);
        dest.writeString(statusCode);
    }
}
