package com.ollie.mc3d;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShaderUtils {
    public static String readTextFileFromRawResource(final Context context, final int resourceId) {
        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(resourceId)));
        final StringBuilder body = new StringBuilder();
        String nextLine;
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return body.toString();
    }
}
