package com.example.johanmorales.loginapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecentLog implements Parcelable {

    public Integer arrivingControlLogId;
    public String name;
    public boolean isAbleToEnter;
    public String arrivingAgent;
    public String agentTurn;
    public String arrivingTime;
    public Integer controlAgent;
    public String detail;

    public RecentLog(Parcel in) {
        if (in.readByte() == 0) {
            arrivingControlLogId = null;
        } else {
            arrivingControlLogId = in.readInt();
        }
        name = in.readString();
        isAbleToEnter = in.readByte() != 0;
        arrivingAgent = in.readString();
        agentTurn = in.readString();
        arrivingTime = in.readString();
        if (in.readByte() == 0) {
            controlAgent = null;
        } else {
            controlAgent = in.readInt();
        }
        detail = in.readString();
    }

    public static final Creator<RecentLog> CREATOR = new Creator<RecentLog>() {
        @Override
        public RecentLog createFromParcel(Parcel in) {
            return new RecentLog(in);
        }

        @Override
        public RecentLog[] newArray(int size) {
            return new RecentLog[size];
        }
    };

    public RecentLog() {

    }

    public Integer getArrivingControlLogId() {
        return arrivingControlLogId;
    }

    public void setArrivingControlLogId(Integer arrivingControlLogId) {
        this.arrivingControlLogId = arrivingControlLogId;
    }

    public Integer getControlAgent() {
        return controlAgent;
    }

    public void setControlAgent(Integer controlAgent) {
        this.controlAgent = controlAgent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAbleToEnter() {
        return isAbleToEnter;
    }

    public void setAbleToEnter(boolean ableToEnter) {
        isAbleToEnter = ableToEnter;
    }

    public String getArrivingAgent() {
        return arrivingAgent;
    }

    public void setArrivingAgent(String arrivingAgent) {
        this.arrivingAgent = arrivingAgent;
    }

    public String getAgentTurn() {
        return agentTurn;
    }

    public void setAgentTurn(String agentTurn) {
        this.agentTurn = agentTurn;
    }

    public String getArrivingTime() {
        return arrivingTime;
    }

    public void setArrivingTime(String arrivingTime) {
        this.arrivingTime = arrivingTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (arrivingControlLogId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(arrivingControlLogId);
        }
        dest.writeString(name);
        dest.writeByte((byte) (isAbleToEnter ? 1 : 0));
        dest.writeString(arrivingAgent);
        dest.writeString(agentTurn);
        dest.writeString(arrivingTime);
        if (controlAgent == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(controlAgent);
        }
        dest.writeString(detail);
    }
}
