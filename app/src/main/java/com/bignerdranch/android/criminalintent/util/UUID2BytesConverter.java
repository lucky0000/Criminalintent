package com.bignerdranch.android.criminalintent.util;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

//public class UUID2BytesConverter implements PropertyConverter<UUID, byte[]> {
//    @Override
//    public UUID convertToEntityProperty(byte[] databaseValue) {
//        if (databaseValue.length != 16) {
//            throw new IllegalArgumentException("Invalid UUID byte[]");
//        }
//
//        long msb = 0;
//        long lsb = 0;
//        for (int i = 0; i < 8; i++)
//            msb = (msb << 8) | (databaseValue[i] & 0xff);
//        for (int i = 8; i < 16; i++)
//            lsb = (lsb << 8) | (databaseValue[i] & 0xff);
//
//        return new UUID(msb, lsb);
//    }
//
//    @Override
//    public byte[] convertToDatabaseValue(UUID entityProperty) {
//        ByteArrayOutputStream ba = new ByteArrayOutputStream(16);
//        DataOutputStream da = new DataOutputStream(ba);
//        try {
//            da.writeLong(entityProperty.getMostSignificantBits());
//            da.writeLong(entityProperty.getLeastSignificantBits());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ba.toByteArray();
//
////        return new byte[0];
//    }
//}

public class UUID2BytesConverter implements PropertyConverter<UUID, String> {
    @Override
    public UUID convertToEntityProperty(String databaseValue) {
        return UUID.fromString(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(UUID entityProperty) {


        return entityProperty.toString();
    }
}

