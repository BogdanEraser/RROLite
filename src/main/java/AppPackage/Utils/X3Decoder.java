package AppPackage.Utils;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by Eraser on 28.03.2016.
 * <p>
 * Class for decoding X3 report
 */
public class X3Decoder {

    private static final Logger log = Logger.getLogger(X3Decoder.class);

    public X3Decoder() {
    }

    public static ArrayList<X3Result> decodeToArrayList(byte[] bFile) {
        ArrayList<X3Result> resultArrayList = new ArrayList<X3Result>();
        int i = 0;
        for (int j = 1; j < bFile.length; j += 124) {
            X3Result tmp = new X3Result();

            byte[] temp1 = new byte[4];
            System.arraycopy(bFile, j, temp1, 0, 4);
            tmp.setCode(ByteBuffer.wrap(temp1).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

            byte[] temp2 = new byte[4];
            System.arraycopy(bFile, j + 4, temp2, 0, 4);    //делим на 100 ибо в копейках
            tmp.setPrice(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            byte[] temp3 = new byte[48];
            System.arraycopy(bFile, j + 4 + 4, temp3, 0, 48);
            try {
                tmp.setName(new String(temp3, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
            } catch (UnsupportedEncodingException e) {
                log.debug("got unsupported encoding error: " + e.toString());
            }

            byte[] temp4 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4, temp4, 0, 4);       //делим на 1000 ибо в граммах
            tmp.setQtyIn(BigDecimal.valueOf(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            resultArrayList.add(new X3Result());
            resultArrayList.set(i, tmp);
            i++;
        }


        return resultArrayList;
    }

}
