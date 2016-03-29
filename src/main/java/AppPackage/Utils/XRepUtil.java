package AppPackage.Utils;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Eraser on 28.03.2016.
 * <p>
 * Class for decoding X3 report
 */
public class XRepUtil {

    private static final Logger log = Logger.getLogger(XRepUtil.class);

    public XRepUtil() {
    }


    /**
     * Обработка массива байт из файла x1.bin
     * <p>
     * <TNUM> 4 двоичный Номер записи о налогах
     * <DATA> 4 двоичный дата записи ставок НДС
     * байт 1 Дата, Байт 2 месяц, Байты 3,4 год
     * <TAX_F> 1 бинарный Бит 3-7 Не используются
     * Бит 2 0 Сумма дополнительного сбора вычисляется без учета НДС
     * 1 Сумма дополнительного сбора вычисляется с учетом НДС
     * Бит 1 0 Дополнительные сборы запрещены
     * 1 Дополнительные сборы разрешены
     * Бит 0 0 НДС не включен в цену
     * 1 НДС включен в цену
     * <TAX_VALn> 2 двоичный Значение n-ой ставки НДС
     * <ADD_TAX_VALn> 2 двоичный Значение n-ой ставки дополнительного сбора
     * <ADD_TAX_NAMEn> 20 текст Название n-го дополнительного сбора
     * <Z1_NUM> 2 двоичный номер текущего Z отчета
     * <TURNOVERn_IN> 8 двоичный Оборот по ставке n, продажи
     * <TAXn_IN> 8 двоичный налог по ставке n, продажи
     * <ADD_TAXn_IN> 8 двоичный сбор по ставке n, продажи
     * <TURNOVERn_OUT> 8 двоичный Оборот по ставке n, возвраты
     * <TAXn_OUT> 8 двоичный налог по ставке n, возвраты
     * <ADD_TAXn_OUT> 8 двоичный сбор по ставке n, возвраты
     * <CHECKS_IN> 4 двоичный Количество чеков, продажи
     * <CHECKS_OUT> 4 двоичный Количество чеков, возвраты
     */
    public static X1FullResult decodeX1Full(byte[] bFile) {
        X1FullResult result = new X1FullResult();
        //номера записи о налогах
        byte[] temp4 = new byte[4];
        System.arraycopy(bFile, 0, temp4, 0, 4);
        result.setTaxNum(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        //дата, сформируем строку из разных байтов
        byte[] temp1 = new byte[1];
        System.arraycopy(bFile, 4, temp1, 0, 1);
        byte day = ByteBuffer.wrap(temp1).get(); //getting wrapped int from 1 byte in reverse byte order
        System.arraycopy(bFile, 5, temp1, 0, 1);
        byte month = ByteBuffer.wrap(temp1).get(); //getting wrapped int from 1 byte in reverse byte order
        byte[] temp2 = new byte[2];
        System.arraycopy(bFile, 6, temp2, 0, 2);
        short year = ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort(); //getting wrapped int from 2 byte in reverse byte order
        result.setData(String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year));

        //параметры НДС
        System.arraycopy(bFile, 8, temp1, 0, 1);
        //TODO добавить расшифровку бита параметров НДС
        byte taxF = ByteBuffer.wrap(temp1).order(ByteOrder.LITTLE_ENDIAN).get(); //getting wrapped 1 byte from 1 byte in reverse byte order
        result.setTaxParam(taxF);

        //значение 1-й ставки НДС (А)
        System.arraycopy(bFile, 9, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setTaxVal1(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //значение 1-й ставки дополнительного сбора
        System.arraycopy(bFile, 11, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setAddTaxVal1(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //наименование 1-й ставки дополнительного сбора
        byte[] temp20 = new byte[20];
        System.arraycopy(bFile, 13, temp20, 0, 20);
        try {
            result.setAddTaxValName1(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName1("Н/Д");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //значение 2-й ставки НДС (Б)
        System.arraycopy(bFile, 33, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setTaxVal2(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //значение 2-й ставки дополнительного сбора
        System.arraycopy(bFile, 35, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setAddTaxVal2(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //наименование 2-й ставки дополнительного сбора
        System.arraycopy(bFile, 37, temp20, 0, 20);
        try {
            result.setAddTaxValName2(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName2("Н/Д");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //значение 3-й ставки НДС (В)
        System.arraycopy(bFile, 57, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setTaxVal3(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //значение 3-й ставки дополнительного сбора
        System.arraycopy(bFile, 59, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setAddTaxVal3(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //наименование 3-й ставки дополнительного сбора
        System.arraycopy(bFile, 61, temp20, 0, 20);
        try {
            result.setAddTaxValName3(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName3("Н/Д");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //значение 4-й ставки НДС (Г)
        System.arraycopy(bFile, 81, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setTaxVal4(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //значение 4-й ставки дополнительного сбора
        System.arraycopy(bFile, 83, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setAddTaxVal4(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //наименование 4-й ставки дополнительного сбора
        System.arraycopy(bFile, 85, temp20, 0, 20);
        try {
            result.setAddTaxValName4(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName4("Н/Д");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //значение 5-й ставки НДС (Д)
        System.arraycopy(bFile, 105, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setTaxVal5(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //значение 5-й ставки дополнительного сбора
        System.arraycopy(bFile, 107, temp2, 0, 2);  //делим на 100 ибо в копейках
        result.setAddTaxVal5(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //наименование 5-й ставки дополнительного сбора
        System.arraycopy(bFile, 109, temp20, 0, 20);
        try {
            result.setAddTaxValName5(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName5("Н/Д");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //номер текущего Z-отчета
        System.arraycopy(bFile, 129, temp2, 0, 2);
        result.setZ1Num(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()); //getting wrapped int from 2 byte in reverse byte order

        //Оборот по 1-й ставке (А), продажи
        byte[] temp8 = new byte[8];
        System.arraycopy(bFile, 131, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Оборот по 2-й ставке (Б), продажи
        System.arraycopy(bFile, 139, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverIn2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Оборот по 3-й ставке (В), продажи
        System.arraycopy(bFile, 147, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Оборот по 4-й ставке (Г), продажи
        System.arraycopy(bFile, 155, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Оборот по 5-й ставке (Д), продажи
        System.arraycopy(bFile, 163, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Оборот по 6-й ставке (Е), продажи
        System.arraycopy(bFile, 171, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverIn6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Налог по 1-й ставке (А), продажи
        System.arraycopy(bFile, 179, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Налог по 2-й ставке (Б), продажи
        System.arraycopy(bFile, 187, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxIn2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Налог по 3-й ставке (В), продажи
        System.arraycopy(bFile, 195, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Налог по 4-й ставке (Г), продажи
        System.arraycopy(bFile, 203, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Налог по 5-й ставке (Д), продажи
        System.arraycopy(bFile, 211, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Налог по 6-й ставке (Е), продажи
        System.arraycopy(bFile, 219, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxIn6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сбор по 1-й ставке (А), продажи
        System.arraycopy(bFile, 227, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сбор по 2-й ставке (Б), продажи
        System.arraycopy(bFile, 235, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxIn2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сбор по 3-й ставке (В), продажи
        System.arraycopy(bFile, 243, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сбор по 4-й ставке (Г), продажи
        System.arraycopy(bFile, 251, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сбор по 5-й ставке (Д), продажи
        System.arraycopy(bFile, 259, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сбор по 6-й ставке (Е), продажи
        System.arraycopy(bFile, 267, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxIn6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Оборот по 1-й ставке (А), возврат
        System.arraycopy(bFile, 275, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Оборот по 2-й ставке (Б), возврат
        System.arraycopy(bFile, 283, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverOut2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Оборот по 3-й ставке (В), возврат
        System.arraycopy(bFile, 291, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Оборот по 4-й ставке (Г), возврат
        System.arraycopy(bFile, 299, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Оборот по 5-й ставке (Д), возврат
        System.arraycopy(bFile, 307, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Оборот по 6-й ставке (Е), возврат
        System.arraycopy(bFile, 315, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTurnoverOut6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Налог по 1-й ставке (А), возврат
        System.arraycopy(bFile, 323, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Налог по 2-й ставке (Б), возврат
        System.arraycopy(bFile, 331, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxOut2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Налог по 3-й ставке (В), возврат
        System.arraycopy(bFile, 339, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Налог по 4-й ставке (Г), возврат
        System.arraycopy(bFile, 347, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Налог по 5-й ставке (Д), возврат
        System.arraycopy(bFile, 355, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Налог по 6-й ставке (Е), возврат
        System.arraycopy(bFile, 363, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setTaxOut6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Сбор по 1-й ставке (А), возврат
        System.arraycopy(bFile, 371, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Сбор по 2-й ставке (Б), возврат
        System.arraycopy(bFile, 379, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxOut2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Сбор по 3-й ставке (В), возврат
        System.arraycopy(bFile, 387, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Сбор по 4-й ставке (Г), возврат
        System.arraycopy(bFile, 395, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Сбор по 5-й ставке (Д), возврат
        System.arraycopy(bFile, 403, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //Сбор по 6-й ставке (Е), возврат
        System.arraycopy(bFile, 411, temp8, 0, 8);  //делим на 100 ибо в копейках
        result.setAddTaxOut6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //кол-во чеков продажи
        System.arraycopy(bFile, 419, temp4, 0, 4);
        result.setChecksIn(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        //кол-во чеков возврата
        System.arraycopy(bFile, 423, temp4, 0, 4);
        result.setChecksOut(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        return result;
    }


    /**
     * Обработка массива байт из файла x3.bin
     * <p>
     * PAR3     1     битовый     Биты 3-7     Не используются
     * Бит 2     Передавать/не передавать остаток товара (1/0)
     * Бит 1     Передавать/не передавать штрих-код товара (1/0)
     * Бит 0     Передавать/не передавать наименование товара (1/0)
     * CODEx     4     двоичный     Код товара
     * PRICEx     4     двоичный     Цена в копейках товара
     * NAMEx     48     текст     Наименование товара
     * BARCODEx     8     двоичный     Штрих-код товара
     * QTYx     4     двоичный     Остаток товара в граммах
     * QTYx_IN     4     двоичный     Количество товара (продажи)
     * MRKPx_IN     8     двоичный     Наценка в копейках (продажи)
     * RDCTx_IN     8     двоичный     Скидка в копейках (продажи)
     * TRNOVRx_IN     8     двоичный     Оборот в копейках (продажи)
     * QTYx_OUT     4     двоичный     Количество товара (возвраты)
     * MRKPx_OUT     8     двоичный     Наценка в копейках (возвраты)
     * RDCTx_OUT     8     двоичный     Скидка в копейках (возвраты)
     * TRNOVRx_OUT     8     двоичный     Оборот в копейках (возвраты)
     */
    public static ArrayList<X3Result> decodeX3ToArrayList(byte[] bFile) {
        ArrayList<X3Result> resultArrayList = new ArrayList<X3Result>();
        int i = 0;
        for (int j = 1; j < bFile.length; j += 124) {
            X3Result tmp = new X3Result();
            //код
            byte[] temp1 = new byte[4];
            System.arraycopy(bFile, j, temp1, 0, 4);
            tmp.setCode(ByteBuffer.wrap(temp1).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

            //цена
            byte[] temp2 = new byte[4];
            System.arraycopy(bFile, j + 4, temp2, 0, 4);    //делим на 100 ибо в копейках
            tmp.setPrice(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //наименование
            byte[] temp3 = new byte[48];
            System.arraycopy(bFile, j + 4 + 4, temp3, 0, 48);
            try {
                tmp.setName(new String(temp3, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
            } catch (UnsupportedEncodingException e) {
                log.debug("got unsupported encoding error: " + e.toString());
            }

            //штрихкод
            byte[] temp4 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48, temp4, 0, 8);
            tmp.setBarcode(Integer.toString(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt())); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //остатки
            byte[] temp5 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8, temp5, 0, 4);    //делим на 1000 ибо в граммах
            tmp.setQty(BigDecimal.valueOf(ByteBuffer.wrap(temp5).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //количество (продажа)
            byte[] temp6 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4, temp6, 0, 4);       //делим на 1000 ибо в граммах
            tmp.setQtyIn(BigDecimal.valueOf(ByteBuffer.wrap(temp6).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //наценка (продажа)
            byte[] temp7 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4, temp7, 0, 8);    //делим на 100 ибо в копейках
            tmp.setMRKPIn(BigDecimal.valueOf(ByteBuffer.wrap(temp7).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //скидка (продажа)
            byte[] temp8 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8, temp8, 0, 8);    //делим на 100 ибо в копейках
            tmp.setRDCTIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //оборот (продажа)
            byte[] temp9 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8, temp9, 0, 8);    //делим на 100 ибо в копейках
            tmp.setTRNOVRIn(BigDecimal.valueOf(ByteBuffer.wrap(temp9).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order


            //количество (возврат)
            byte[] temp10 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8, temp10, 0, 4);       //делим на 1000 ибо в граммах
            tmp.setQtyOut(BigDecimal.valueOf(ByteBuffer.wrap(temp10).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //наценка (возврат)
            byte[] temp11 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8 + 4, temp11, 0, 8);    //делим на 100 ибо в копейках
            tmp.setMRKPOut(BigDecimal.valueOf(ByteBuffer.wrap(temp11).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //скидка (возврат)
            byte[] temp12 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8 + 4 + 8, temp12, 0, 8);    //делим на 100 ибо в копейках
            tmp.setRDCTOut(BigDecimal.valueOf(ByteBuffer.wrap(temp12).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //оборот (возврат)
            byte[] temp13 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8 + 4 + 8 + 8, temp13, 0, 8);    //делим на 100 ибо в копейках
            tmp.setTRNOVROut(BigDecimal.valueOf(ByteBuffer.wrap(temp13).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            resultArrayList.add(new X3Result());
            resultArrayList.set(i, tmp);
            i++;
        }
        return resultArrayList;
    }


    /**
     * Экспорт данных, полученных из отчета Х3 в файл Excel
     *
     * @param x1FullResult      объект с данными из декодированного файла x1.bin
     * @param x3ResultArrayList массив объектов с данными из декодированного файла x3.bin
     * @param excelFilePath     путь и имя книги xlsx
     * @param X1sheetName       имя листа для полного дневного отчета
     * @param X3sheetName       имя листа для отчета по товарам в книге
     * @return successfulness of operation
     */
    public static boolean writeToXlsx(X1FullResult x1FullResult, ArrayList<X3Result> x3ResultArrayList, String excelFilePath, String X1sheetName, String X3sheetName) {

        try {
            //создание файла
            if (!Files.exists(Paths.get(excelFilePath))) {
                Files.createFile(Paths.get(excelFilePath));
            }
            if ((Files.isDirectory(Paths.get(excelFilePath))) | (!Files.isWritable(Paths.get(excelFilePath)))) {
                log.debug("Error accessing file for writing X3 report. File '" + excelFilePath + "' is directory or is not writable");
                return false;
            }
            Workbook workbook = null;
            String fileExtension = excelFilePath.substring(excelFilePath.indexOf("."));
            if (fileExtension.equals(".xls")) {
                workbook = new HSSFWorkbook();

            } else if (fileExtension.equals(".xlsx")) {
                workbook = new XSSFWorkbook();

            } else {
                log.debug("Wrong File Type for exporting X3 report");
                return false;
            }


            //заполнение листа с отчетом о товарах (Х3)
            Sheet sheet = workbook.createSheet(X3sheetName); //если лист не существует - создать его
            ListIterator<X3Result> x3ResultListIterator = x3ResultArrayList.listIterator();

            Row row;
            row = sheet.createRow(1);    //создаем строку с названиями "продажа" и "возврат"
            Font font = workbook.createFont();
            //font.setFontHeightInPoints((short) 12);
            //font.setFontName("Calibri");
            font.setBold(true);
            //Set font into style
            CellStyle styleBoldHCenter = workbook.createCellStyle();
            styleBoldHCenter.setFont(font);
            styleBoldHCenter.setAlignment(CellStyle.ALIGN_CENTER);
            // Create a cell with a value and set style to it.
            Cell cell4 = row.createCell(4, Cell.CELL_TYPE_STRING);
            cell4.setCellValue("Продажа");
            Cell cell8 = row.createCell(8, Cell.CELL_TYPE_STRING);
            cell8.setCellValue("Возврат");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 7));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 11));
            cell4.setCellStyle(styleBoldHCenter);
            cell8.setCellStyle(styleBoldHCenter);
            //style.setAlignment(CellStyle.ALIGN_GENERAL);

            row = sheet.createRow(2);    //создаем строку с названиями стоблцов
            String[] heading = {"Код", "Наименование товара", "Цена", "Количество", "Наценка", "Скидка", "Оборот", "Количество", "Наценка", "Скидка", "Оборот", "Штрихкод", "Остатки"};
            int s = 1;
            for (String str : heading) {
                Cell cell = row.createCell(s, Cell.CELL_TYPE_STRING);
                cell.setCellValue(str);
                cell.setCellStyle(styleBoldHCenter);
                s++;
            }

            int rowIndex = 3;   //начнем импорт с 4-й!!! строки
            //int colIndex = 0;   //и 1-го!!! столбца

            while (x3ResultListIterator.hasNext()) {
                row = sheet.createRow(rowIndex);    //создаем строку и набрасываем в нее ячейки с данными
                row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getCode());
                row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getName());
                row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getPrice().doubleValue());
                row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getQtyIn().doubleValue());
                row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getMRKPIn().doubleValue());
                row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getRDCTIn().doubleValue());
                row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getTRNOVRIn().doubleValue());
                row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getQtyOut().doubleValue());
                row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getMRKPOut().doubleValue());
                row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getRDCTOut().doubleValue());
                row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getTRNOVROut().doubleValue());
                row.createCell(12, Cell.CELL_TYPE_STRING).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getBarcode());
                row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getQty().doubleValue());
                x3ResultListIterator.next();
                rowIndex++;
            }
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);


            OutputStream outFile = Files.newOutputStream(Paths.get(excelFilePath));
            //FileOutputStream outFile = new FileOutputStream(new File(excelFilePath));
            workbook.write(outFile);
            outFile.close();
            return true;
        } catch (IOException e) {
            log.debug("error while writing X3 report: " + e.toString());
            return false;
        }

    }
}
