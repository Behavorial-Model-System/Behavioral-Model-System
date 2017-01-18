package com.bms.mqp.behaviormodelsystem;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephen on 12/5/2016.
 */

public class PossiblePlace implements Parcelable {
    public String name;
    public float likelihood;

    protected PossiblePlace(Parcel in) {
        name = in.readString();
        likelihood = in.readFloat();
    }

    public PossiblePlace(String s, float likelihood) {
        this.name = s;
        this.likelihood = likelihood;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(likelihood);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PossiblePlace> CREATOR = new Parcelable.Creator<PossiblePlace>() {
        @Override
        public PossiblePlace createFromParcel(Parcel in) {
            return new PossiblePlace(in);
        }

        @Override
        public PossiblePlace[] newArray(int size) {
            return new PossiblePlace[size];
        }
    };
}