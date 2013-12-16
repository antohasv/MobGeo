package com.igeo.igeolink;

import java.io.InputStream;
import java.io.OutputStream;

public class Util {
    public static void CopyStream(InputStream in, OutputStream out)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes = new byte[buffer_size];
            for(;;)
            {
              int count = in.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              out.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
