package com.csja.smlocked;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by qylk on 15/9/11.
 */
public class SerializeUtils {

    public static boolean serializeObject(Context context, Object object, String name) {
        if (object == null || TextUtils.isEmpty(name)) {
            throw new NullPointerException("object or name is null/empty");
        }
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(context.getFilesDir(), name)));
            objectOutputStream.writeObject(object);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    public static <T> T deSerializeObject(Context context, String name) {
        File objFile = new File(context.getFilesDir(), name);
        T obj = null;
        if (objFile.exists()) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(objFile));
                obj = (T) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return obj;
    }

}
