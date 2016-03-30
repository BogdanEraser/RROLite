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
     * TNUM 4 двоичный Номер записи о налогах
     * DATA 4 двоичный дата записи ставок НДС
     * байт 1 Дата, Байт 2 месяц, Байты 3,4 год
     * TAX_F 1 бинарный Бит 3-7 Не используются
     * Бит 2 0 Сумма дополнительного сбора вычисляется без учета НДС
     * 1 Сумма дополнительного сбора вычисляется с учетом НДС
     * Бит 1 0 Дополнительные сборы запрещены
     * 1 Дополнительные сборы разрешены
     * Бит 0 0 НДС не включен в цену
     * 1 НДС включен в цену
     * TAX_VALn 2 двоичный Значение n-ой ставки НДС
     * ADD_TAX_VALn 2 двоичный Значение n-ой ставки дополнительного сбора
     * ADD_TAX_NAMEn 20 текст Название n-го дополнительного сбора
     * Z1_NUM 2 двоичный номер текущего Z отчета
     * TURNOVERn_IN 8 двоичный Оборот по ставке n, продажи
     * TAXn_IN 8 двоичный налог по ставке n, продажи
     * ADD_TAXn_IN 8 двоичный сбор по ставке n, продажи
     * TURNOVERn_OUT 8 двоичный Оборот по ставке n, возвраты
     * TAXn_OUT 8 двоичный налог по ставке n, возвраты
     * ADD_TAXn_OUT 8 двоичный сбор по ставке n, возвраты
     * CHECKS_IN 4 двоичный Количество чеков, продажи
     * CHECKS_OUT 4 двоичный Количество чеков, возвраты
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
     * Обработка массива байт из файла x5.bin
     * <p>
     * PAY_CASH_INx 8 двоичный Сумма продаж, наличные
     * PAY_CHECK_INx 8 двоичный Сумма продаж, чек
     * PAY_CREDIT_CARD_INx 8 двоичный Сумма продаж, кредитная карта
     * PAY_USERx_INx 8 двоичный Сумма продаж, пользовательский тип х (х = 1,2,3,4,5)
     * PAY_CASH_OUTx 8 двоичный Сумма возвратов, наличные
     * PAY_CHECK_OUTx 8 двоичный Сумма возвратов, чек
     * PAY_CREDIT_CARD_OUTx 8 двоичный Сумма возвратов, кредитная карта
     * PAY_USERx_OUTx 8 двоичный Сумма возвратов, пользовательский тип х (х = 1,2,3,4,5)
     * CASH_INx 8 двоичный Служебный внос, наличные
     * CHECK_INx 8 двоичный Служебный внос, чек
     * CREDIT_CARD_INx 8 двоичный Служебный внос, кредитная карта
     * USERx_INx 8 двоичный Служебный внос, пользовательский тип х (х = 1,2,3,4,5)
     * CASH_OUTx 8 двоичный Служебный вынос, наличные
     * CHECK_OUTx 8 двоичный Служебный вынос, чек
     * CREDIT_CARD_OUTx 8 двоичный Служебный вынос, кредитная карта
     * USERx_OUTx 8 двоичный Служебный вынос, пользовательский тип х (х = 1,2,3,4,5)
     * ABORT_CHECKS_x 4 двоичный Отмененные чеки
     * ABORT_POS_x 4 двоичный Отмененные позиции
     */
    public static X5Result decodeX5(byte[] bFile) {
        X5Result result = new X5Result();
        //Сумма продаж, наличные
        byte[] temp8 = new byte[8];
        System.arraycopy(bFile, 0, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayCashIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, чек
        System.arraycopy(bFile, 8, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayCheckIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, кредитная карта
        System.arraycopy(bFile, 16, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayCCIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, пользовательский тип 1
        System.arraycopy(bFile, 24, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, пользовательский тип 2
        System.arraycopy(bFile, 32, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, пользовательский тип 3
        System.arraycopy(bFile, 40, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, пользовательский тип 4
        System.arraycopy(bFile, 48, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма продаж, пользовательский тип 5
        System.arraycopy(bFile, 56, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, наличные
        System.arraycopy(bFile, 64, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayCashOut(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, чек
        System.arraycopy(bFile, 72, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayCheckOut(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, кредитная карта
        System.arraycopy(bFile, 80, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayCCOut(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, пользовательский тип 1
        System.arraycopy(bFile, 88, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, пользовательский тип 2
        System.arraycopy(bFile, 96, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, пользовательский тип 3
        System.arraycopy(bFile, 104, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, пользовательский тип 4
        System.arraycopy(bFile, 112, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Сумма возвратов, пользовательский тип 5
        System.arraycopy(bFile, 120, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setPayUserOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, наличные
        System.arraycopy(bFile, 128, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setCashIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, чек
        System.arraycopy(bFile, 136, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setCheckIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, кредитная карта
        System.arraycopy(bFile, 144, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setCCIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, пользовательский тип 1
        System.arraycopy(bFile, 152, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, пользовательский тип 2
        System.arraycopy(bFile, 160, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, пользовательский тип 3
        System.arraycopy(bFile, 168, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, пользовательский тип 4
        System.arraycopy(bFile, 176, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный внос, пользовательский тип 5
        System.arraycopy(bFile, 184, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, наличные
        System.arraycopy(bFile, 192, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setCashOut(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, чек
        System.arraycopy(bFile, 200, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setCheckOut(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, кредитная карта
        System.arraycopy(bFile, 208, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setCCOut(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, пользовательский тип 1
        System.arraycopy(bFile, 216, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, пользовательский тип 2
        System.arraycopy(bFile, 224, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, пользовательский тип 3
        System.arraycopy(bFile, 232, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, пользовательский тип 4
        System.arraycopy(bFile, 240, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //Служебный вынос, пользовательский тип 5
        System.arraycopy(bFile, 248, temp8, 0, 8);     //делим на 100 ибо в копейках
        result.setUserOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order
        
        //Отмененные чеки
        byte[] temp4 = new byte[4];
        System.arraycopy(bFile, 256, temp4, 0, 4);
        result.setAbortChecks(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        //Отмененные позиции
        System.arraycopy(bFile, 264, temp4, 0, 4);
        result.setAbortPos(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        return result;
    }





    /**
     * Экспорт данных, полученных из Х-отчетов в файл Excel
     *
     * @param x1FullResult      объект с данными из декодированного файла x1.bin
     * @param x5Result          объект с данными из декодированного файла x5.bin
     * @param x3ResultArrayList массив объектов с данными из декодированного файла x3.bin
     * @param folderPath        имя папки для книги xlsx
     * @param excelFileName     имя книги xlsx
     * @param X1sheetName       имя листа для полного дневного отчета
     * @param X5sheetName       имя листа для отчета по кассирам
     * @param X3sheetName       имя листа для отчета по товарам в книге
     * @return successfulness of operation
     */
    public static boolean writeToXlsx(X1FullResult x1FullResult, X5Result x5Result, ArrayList<X3Result> x3ResultArrayList, String folderPath, String excelFileName, String X1sheetName, String X5sheetName, String X3sheetName) {

        try {
            //создание файла
            if (!Files.exists(Paths.get(excelFileName))) {
                if (!Files.isDirectory(Paths.get(folderPath))) {
                    Files.createDirectory(Paths.get(folderPath));
                }
                Files.createFile(Paths.get(folderPath + "\\" + excelFileName));
            }
            if ((!Files.isWritable(Paths.get(folderPath + "\\" +excelFileName))) || (Files.isDirectory(Paths.get(folderPath + "\\" +excelFileName)))) {
                log.debug("Error accessing file for writing X3 report. File '" + excelFileName + "' is directory or is not writable");
                return false;
            }
            Workbook workbook = null;
            String fileExtension = excelFileName.substring(excelFileName.indexOf("."));
            if (fileExtension.equals(".xls")) {
                workbook = new HSSFWorkbook();

            } else if (fileExtension.equals(".xlsx")) {
                workbook = new XSSFWorkbook();

            } else {
                log.debug("Wrong File Type for exporting X3 report");
                return false;
            }

            Font font = workbook.createFont();
            //font.setFontHeightInPoints((short) 12);
            //font.setFontName("Calibri");
            font.setBold(true);
            //Set font into style
            CellStyle styleBoldHCenter = workbook.createCellStyle();
            styleBoldHCenter.setFont(font);
            styleBoldHCenter.setAlignment(CellStyle.ALIGN_CENTER);

            //заполнение листа с полным дневным отчетом (Х1)
            Sheet sheetX1 = workbook.createSheet(X1sheetName); //если лист не существует - создать его

            // Строка заголовков
            Row rowX1_1 = sheetX1.createRow(1);
            Cell cellX1a = rowX1_1.createCell(1, Cell.CELL_TYPE_STRING);
            cellX1a.setCellValue("Название параметра");
            cellX1a.setCellStyle(styleBoldHCenter);
            Cell cellX1b = rowX1_1.createCell(2, Cell.CELL_TYPE_STRING);
            cellX1b.setCellValue("Значение");
            cellX1b.setCellStyle(styleBoldHCenter);

            Row rowX1_2 = sheetX1.createRow(2);
            rowX1_2.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Номер записи о налогах");
            rowX1_2.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxNum());

            Row rowX1_3 = sheetX1.createRow(3);
            rowX1_3.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Дата записи ставок НДС");
            rowX1_3.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x1FullResult.getData());

            Row rowX1_4 = sheetX1.createRow(4);
            rowX1_4.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Параметры НДС");
            rowX1_4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxParam());

            Row rowX1_5 = sheetX1.createRow(5);
            rowX1_5.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 1-й ставки НДС (А)");
            rowX1_5.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxVal1().doubleValue());

            Row rowX1_6 = sheetX1.createRow(6);
            rowX1_6.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 1-й ставки дополнительного сбора");
            rowX1_6.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxVal1().doubleValue());

            Row rowX1_7 = sheetX1.createRow(7);
            rowX1_7.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Название 1-го дополнительного сбора");
            rowX1_7.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x1FullResult.getAddTaxValName1());

            Row rowX1_8 = sheetX1.createRow(8);
            rowX1_8.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 2-й ставки НДС (Б)");
            rowX1_8.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxVal2().doubleValue());

            Row rowX1_9 = sheetX1.createRow(9);
            rowX1_9.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 2-й ставки дополнительного сбора");
            rowX1_9.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxVal2().doubleValue());

            Row rowX1_10 = sheetX1.createRow(10);
            rowX1_10.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Название 2-го дополнительного сбора");
            rowX1_10.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x1FullResult.getAddTaxValName2());

            Row rowX1_11 = sheetX1.createRow(11);
            rowX1_11.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 3-й ставки НДС (В)");
            rowX1_11.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxVal3().doubleValue());

            Row rowX1_12 = sheetX1.createRow(12);
            rowX1_12.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 3-й ставки дополнительного сбора");
            rowX1_12.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxVal3().doubleValue());

            Row rowX1_13 = sheetX1.createRow(13);
            rowX1_13.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Название 3-го дополнительного сбора");
            rowX1_13.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x1FullResult.getAddTaxValName3());

            Row rowX1_14 = sheetX1.createRow(14);
            rowX1_14.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 4-й ставки НДС (Г)");
            rowX1_14.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxVal4().doubleValue());

            Row rowX1_15 = sheetX1.createRow(15);
            rowX1_15.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 4-й ставки дополнительного сбора");
            rowX1_15.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxVal4().doubleValue());

            Row rowX1_16 = sheetX1.createRow(16);
            rowX1_16.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Название 4-го дополнительного сбора");
            rowX1_16.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x1FullResult.getAddTaxValName4());

            Row rowX1_17 = sheetX1.createRow(17);
            rowX1_17.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 5-й ставки НДС (Д)");
            rowX1_17.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxVal5().doubleValue());

            Row rowX1_18 = sheetX1.createRow(18);
            rowX1_18.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Значение 5-й ставки дополнительного сбора");
            rowX1_18.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxVal5().doubleValue());

            Row rowX1_19 = sheetX1.createRow(19);
            rowX1_19.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Название 5-го дополнительного сбора");
            rowX1_19.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x1FullResult.getAddTaxValName5());

            Row rowX1_20 = sheetX1.createRow(20);
            rowX1_20.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Номер текущего Z отчета");
            rowX1_20.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getZ1Num());

            Row rowX1_21 = sheetX1.createRow(21);
            rowX1_21.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, продажа, ставка №1");
            rowX1_21.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverIn1().doubleValue());

            Row rowX1_22 = sheetX1.createRow(22);
            rowX1_22.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, продажа, ставка №2");
            rowX1_22.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverIn2().doubleValue());

            Row rowX1_23 = sheetX1.createRow(23);
            rowX1_23.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, продажа, ставка №3");
            rowX1_23.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverIn3().doubleValue());

            Row rowX1_24 = sheetX1.createRow(24);
            rowX1_24.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, продажа, ставка №4");
            rowX1_24.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverIn4().doubleValue());

            Row rowX1_25 = sheetX1.createRow(25);
            rowX1_25.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, продажа, ставка №5");
            rowX1_25.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverIn5().doubleValue());

            Row rowX1_26 = sheetX1.createRow(26);
            rowX1_26.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, продажа, ставка №6");
            rowX1_26.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverIn6().doubleValue());

            Row rowX1_27 = sheetX1.createRow(27);
            rowX1_27.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, продажа, ставка №1");
            rowX1_27.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxIn1().doubleValue());

            Row rowX1_28 = sheetX1.createRow(28);
            rowX1_28.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, продажа, ставка №2");
            rowX1_28.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxIn2().doubleValue());

            Row rowX1_29 = sheetX1.createRow(29);
            rowX1_29.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, продажа, ставка №3");
            rowX1_29.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxIn3().doubleValue());

            Row rowX1_30 = sheetX1.createRow(30);
            rowX1_30.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, продажа, ставка №4");
            rowX1_30.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxIn4().doubleValue());

            Row rowX1_31 = sheetX1.createRow(31);
            rowX1_31.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, продажа, ставка №5");
            rowX1_31.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxIn5().doubleValue());

            Row rowX1_32 = sheetX1.createRow(32);
            rowX1_32.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, продажа, ставка №6");
            rowX1_32.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxIn6().doubleValue());

            Row rowX1_33 = sheetX1.createRow(33);
            rowX1_33.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, продажа, ставка №1");
            rowX1_33.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxIn1().doubleValue());

            Row rowX1_34 = sheetX1.createRow(34);
            rowX1_34.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, продажа, ставка №2");
            rowX1_34.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxIn2().doubleValue());

            Row rowX1_35 = sheetX1.createRow(35);
            rowX1_35.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, продажа, ставка №3");
            rowX1_35.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxIn3().doubleValue());

            Row rowX1_36 = sheetX1.createRow(36);
            rowX1_36.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, продажа, ставка №4");
            rowX1_36.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxIn4().doubleValue());

            Row rowX1_37 = sheetX1.createRow(37);
            rowX1_37.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, продажа, ставка №5");
            rowX1_37.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxIn5().doubleValue());

            Row rowX1_38 = sheetX1.createRow(38);
            rowX1_38.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, продажа, ставка №6");
            rowX1_38.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxIn6().doubleValue());

            Row rowX1_39 = sheetX1.createRow(39);
            rowX1_39.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, возврат, ставка №1");
            rowX1_39.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverOut1().doubleValue());

            Row rowX1_40 = sheetX1.createRow(40);
            rowX1_40.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, возврат, ставка №2");
            rowX1_40.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverOut2().doubleValue());

            Row rowX1_41 = sheetX1.createRow(41);
            rowX1_41.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, возврат, ставка №3");
            rowX1_41.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverOut3().doubleValue());

            Row rowX1_42 = sheetX1.createRow(42);
            rowX1_42.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, возврат, ставка №4");
            rowX1_42.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverOut4().doubleValue());

            Row rowX1_43 = sheetX1.createRow(43);
            rowX1_43.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, возврат, ставка №5");
            rowX1_43.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverOut5().doubleValue());

            Row rowX1_44 = sheetX1.createRow(44);
            rowX1_44.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Оборот, возврат, ставка №6");
            rowX1_44.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTurnoverOut6().doubleValue());

            Row rowX1_45 = sheetX1.createRow(45);
            rowX1_45.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, возврат, ставка №1");
            rowX1_45.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxOut1().doubleValue());

            Row rowX1_46 = sheetX1.createRow(46);
            rowX1_46.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, возврат, ставка №2");
            rowX1_46.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxOut2().doubleValue());

            Row rowX1_47 = sheetX1.createRow(47);
            rowX1_47.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, возврат, ставка №3");
            rowX1_47.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxOut3().doubleValue());

            Row rowX1_48 = sheetX1.createRow(48);
            rowX1_48.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, возврат, ставка №4");
            rowX1_48.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxOut4().doubleValue());

            Row rowX1_49 = sheetX1.createRow(49);
            rowX1_49.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, возврат, ставка №5");
            rowX1_49.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxOut5().doubleValue());

            Row rowX1_50 = sheetX1.createRow(50);
            rowX1_50.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Налог, возврат, ставка №6");
            rowX1_50.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getTaxOut6().doubleValue());

            Row rowX1_51 = sheetX1.createRow(51);
            rowX1_51.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, возврат, ставка №1");
            rowX1_51.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxOut1().doubleValue());

            Row rowX1_52 = sheetX1.createRow(52);
            rowX1_52.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, возврат, ставка №2");
            rowX1_52.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxOut2().doubleValue());

            Row rowX1_53 = sheetX1.createRow(53);
            rowX1_53.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, возврат, ставка №3");
            rowX1_53.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxOut3().doubleValue());

            Row rowX1_54 = sheetX1.createRow(54);
            rowX1_54.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, возврат, ставка №4");
            rowX1_54.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxOut4().doubleValue());

            Row rowX1_55 = sheetX1.createRow(55);
            rowX1_55.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, возврат, ставка №5");
            rowX1_55.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxOut5().doubleValue());

            Row rowX1_56 = sheetX1.createRow(56);
            rowX1_56.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сбор, возврат, ставка №6");
            rowX1_56.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getAddTaxOut6().doubleValue());

            Row rowX1_57 = sheetX1.createRow(57);
            rowX1_57.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Количество чеков, продажа");
            rowX1_57.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getChecksIn());

            Row rowX1_58 = sheetX1.createRow(58);
            rowX1_58.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Количество чеков, возврат");
            rowX1_58.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x1FullResult.getChecksOut());

            sheetX1.autoSizeColumn(1);
            sheetX1.autoSizeColumn(2);


            //заполнение листа с отчетом кассиров (Х5)
            Sheet sheetX5 = workbook.createSheet(X5sheetName); //если лист не существует - создать его

            // Строка заголовков
            Row rowX5_1 = sheetX5.createRow(1);
            Cell cellX5a = rowX5_1.createCell(1, Cell.CELL_TYPE_STRING);
            cellX5a.setCellValue("Название параметра");
            cellX5a.setCellStyle(styleBoldHCenter);
            Cell cellX5b = rowX5_1.createCell(2, Cell.CELL_TYPE_STRING);
            cellX5b.setCellValue("Значение");
            cellX5b.setCellStyle(styleBoldHCenter);

            Row rowX5_2 = sheetX5.createRow(2);
            rowX5_2.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, наличные");
            rowX5_2.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayCashIn().doubleValue());

            Row rowX5_3 = sheetX5.createRow(3);
            rowX5_3.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, чек");
            rowX5_3.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayCheckIn().doubleValue());

            Row rowX5_4 = sheetX5.createRow(4);
            rowX5_4.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, кредитная карта");
            rowX5_4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayCCIn().doubleValue());

            Row rowX5_5 = sheetX5.createRow(5);
            rowX5_5.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, пользовательский тип 1");
            rowX5_5.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserIn1().doubleValue());

            Row rowX5_6 = sheetX5.createRow(6);
            rowX5_6.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, пользовательский тип 2");
            rowX5_6.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserIn1().doubleValue());

            Row rowX5_7 = sheetX5.createRow(7);
            rowX5_7.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, пользовательский тип 3");
            rowX5_7.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserIn1().doubleValue());

            Row rowX5_8 = sheetX5.createRow(8);
            rowX5_8.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, пользовательский тип 4");
            rowX5_8.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserIn1().doubleValue());

            Row rowX5_9 = sheetX5.createRow(9);
            rowX5_9.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма продаж, пользовательский тип 5");
            rowX5_9.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserIn1().doubleValue());

            Row rowX5_10 = sheetX5.createRow(10);
            rowX5_10.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, наличные");
            rowX5_10.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayCashOut().doubleValue());

            Row rowX5_11 = sheetX5.createRow(11);
            rowX5_11.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, чек");
            rowX5_11.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayCheckOut().doubleValue());

            Row rowX5_12 = sheetX5.createRow(12);
            rowX5_12.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, кредитная карта");
            rowX5_12.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayCCOut().doubleValue());

            Row rowX5_13 = sheetX5.createRow(13);
            rowX5_13.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, пользовательский тип 1");
            rowX5_13.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserOut1().doubleValue());

            Row rowX5_14 = sheetX5.createRow(14);
            rowX5_14.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, пользовательский тип 2");
            rowX5_14.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserOut1().doubleValue());

            Row rowX5_15 = sheetX5.createRow(15);
            rowX5_15.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, пользовательский тип 3");
            rowX5_15.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserOut1().doubleValue());

            Row rowX5_16 = sheetX5.createRow(16);
            rowX5_16.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, пользовательский тип 4");
            rowX5_16.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserOut1().doubleValue());

            Row rowX5_17 = sheetX5.createRow(17);
            rowX5_17.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Сумма возвратов, пользовательский тип 5");
            rowX5_17.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getPayUserOut1().doubleValue());

            Row rowX5_18 = sheetX5.createRow(18);
            rowX5_18.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, наличные");
            rowX5_18.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getCashIn().doubleValue());

            Row rowX5_19 = sheetX5.createRow(19);
            rowX5_19.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, чек");
            rowX5_19.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getCheckIn().doubleValue());

            Row rowX5_20 = sheetX5.createRow(20);
            rowX5_20.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, кредитная карта");
            rowX5_20.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getCCIn().doubleValue());

            Row rowX5_21 = sheetX5.createRow(21);
            rowX5_21.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, пользовательский тип 1");
            rowX5_21.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserIn1().doubleValue());

            Row rowX5_22 = sheetX5.createRow(22);
            rowX5_22.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, пользовательский тип 2");
            rowX5_22.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserIn1().doubleValue());

            Row rowX5_23 = sheetX5.createRow(23);
            rowX5_23.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, пользовательский тип 3");
            rowX5_23.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserIn1().doubleValue());

            Row rowX5_24 = sheetX5.createRow(24);
            rowX5_24.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, пользовательский тип 4");
            rowX5_24.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserIn1().doubleValue());

            Row rowX5_25 = sheetX5.createRow(25);
            rowX5_25.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебный внос, пользовательский тип 5");
            rowX5_25.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserIn1().doubleValue());

            Row rowX5_26 = sheetX5.createRow(26);
            rowX5_26.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, наличные");
            rowX5_26.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getCashOut().doubleValue());

            Row rowX5_27 = sheetX5.createRow(27);
            rowX5_27.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, чек");
            rowX5_27.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getCheckOut().doubleValue());

            Row rowX5_28 = sheetX5.createRow(28);
            rowX5_28.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, кредитная карта");
            rowX5_28.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getCCOut().doubleValue());

            Row rowX5_29 = sheetX5.createRow(29);
            rowX5_29.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, пользовательский тип 1");
            rowX5_29.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserOut1().doubleValue());

            Row rowX5_30 = sheetX5.createRow(30);
            rowX5_30.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, пользовательский тип 2");
            rowX5_30.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserOut1().doubleValue());

            Row rowX5_31 = sheetX5.createRow(31);
            rowX5_31.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, пользовательский тип 3");
            rowX5_31.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserOut1().doubleValue());

            Row rowX5_32 = sheetX5.createRow(32);
            rowX5_32.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, пользовательский тип 4");
            rowX5_32.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserOut1().doubleValue());

            Row rowX5_33 = sheetX5.createRow(33);
            rowX5_33.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Служебная выдача, пользовательский тип 5");
            rowX5_33.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getUserOut1().doubleValue());

            Row rowX5_34 = sheetX5.createRow(34);
            rowX5_34.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Аннулированые чеки");
            rowX5_34.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getAbortChecks());

            Row rowX5_35 = sheetX1.createRow(35);
            rowX5_35.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Аннулированные позиции");
            rowX5_35.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(x5Result.getAbortPos());

            sheetX5.autoSizeColumn(1);
            sheetX5.autoSizeColumn(2);
            

            //заполнение листа с отчетом о товарах (Х3)
            Sheet sheetX3 = workbook.createSheet(X3sheetName); //если лист не существует - создать его
            ListIterator<X3Result> x3ResultListIterator = x3ResultArrayList.listIterator();

            Row rowX3;
            rowX3 = sheetX3.createRow(1);    //создаем строку с названиями "продажа" и "возврат"
            // Create a cell with a value and set style to it.
            Cell cell4 = rowX3.createCell(4, Cell.CELL_TYPE_STRING);
            cell4.setCellValue("Продажа");
            Cell cell8 = rowX3.createCell(8, Cell.CELL_TYPE_STRING);
            cell8.setCellValue("Возврат");
            sheetX3.addMergedRegion(new CellRangeAddress(1, 1, 4, 7));
            sheetX3.addMergedRegion(new CellRangeAddress(1, 1, 8, 11));
            cell4.setCellStyle(styleBoldHCenter);
            cell8.setCellStyle(styleBoldHCenter);
            //style.setAlignment(CellStyle.ALIGN_GENERAL);

            rowX3 = sheetX3.createRow(2);    //создаем строку с названиями стоблцов
            String[] headingX3 = {"Код", "Наименование товара", "Цена", "Количество", "Наценка", "Скидка", "Оборот", "Количество", "Наценка", "Скидка", "Оборот", "Штрихкод", "Остатки"};
            int s = 1;
            for (String str : headingX3) {
                Cell cell = rowX3.createCell(s, Cell.CELL_TYPE_STRING);
                cell.setCellValue(str);
                cell.setCellStyle(styleBoldHCenter);
                s++;
            }

            int rowIndex = 3;   //начнем импорт с 4-й!!! строки
            //int colIndex = 0;   //и 1-го!!! столбца

            while (x3ResultListIterator.hasNext()) {
                rowX3 = sheetX3.createRow(rowIndex);    //создаем строку и набрасываем в нее ячейки с данными
                rowX3.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getCode());
                rowX3.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getName());
                rowX3.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getPrice().doubleValue());
                rowX3.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getQtyIn().doubleValue());
                rowX3.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getMRKPIn().doubleValue());
                rowX3.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getRDCTIn().doubleValue());
                rowX3.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getTRNOVRIn().doubleValue());
                rowX3.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getQtyOut().doubleValue());
                rowX3.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getMRKPOut().doubleValue());
                rowX3.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getRDCTOut().doubleValue());
                rowX3.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getTRNOVROut().doubleValue());
                rowX3.createCell(12, Cell.CELL_TYPE_STRING).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getBarcode());
                rowX3.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(x3ResultArrayList.get(x3ResultListIterator.nextIndex()).getQty().doubleValue());
                x3ResultListIterator.next();
                rowIndex++;
            }
            sheetX3.autoSizeColumn(1);
            sheetX3.autoSizeColumn(2);
            sheetX3.autoSizeColumn(3);
            sheetX3.autoSizeColumn(4);
            sheetX3.autoSizeColumn(5);
            sheetX3.autoSizeColumn(6);
            sheetX3.autoSizeColumn(7);
            sheetX3.autoSizeColumn(8);
            sheetX3.autoSizeColumn(9);
            sheetX3.autoSizeColumn(10);
            sheetX3.autoSizeColumn(11);
            sheetX3.autoSizeColumn(12);
            sheetX3.autoSizeColumn(13);

            OutputStream outFile = Files.newOutputStream(Paths.get(folderPath + "\\" +excelFileName));
            //FileOutputStream outFile = new FileOutputStream(new File(excelFilePath));
            log.debug("writing X reports file");
            workbook.write(outFile);
            outFile.close();
            return true;
        } catch (IOException e) {
            log.debug("error while writing X reports file: " + e.toString());
            return false;
        }

    }
}
