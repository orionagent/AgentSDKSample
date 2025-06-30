package com.example.agentrolejava;

import android.os.Parcel;
import android.os.Parcelable;

public class Role implements Parcelable {
    private String name;
    private String persona;
    private String objective;
    private int avatarRes;

    public Role(String name, String persona, String objective, int avatarRes) {
        this.name = name;
        this.persona = persona;
        this.objective = objective;
        this.avatarRes = avatarRes;
    }

    protected Role(Parcel in) {
        name = in.readString();
        persona = in.readString();
        objective = in.readString();
        avatarRes = in.readInt();
    }

    public static final Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        @Override
        public Role[] newArray(int size) {
            return new Role[size];
        }
    };

    // Getters
    public String getName() {
        return name;
    }

    public String getPersona() {
        return persona;
    }

    public String getObjective() {
        return objective;
    }

    public int getAvatarRes() {
        return avatarRes;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setAvatarRes(int avatarRes) {
        this.avatarRes = avatarRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(persona);
        dest.writeString(objective);
        dest.writeInt(avatarRes);
    }
} 