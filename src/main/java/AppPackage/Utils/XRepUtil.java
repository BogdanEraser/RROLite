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
     * ��������� ������� ���� �� ����� x1.bin
     * <p>
     * <TNUM> 4 �������� ����� ������ � �������
     * <DATA> 4 �������� ���� ������ ������ ���
     * ���� 1 ����, ���� 2 �����, ����� 3,4 ���
     * <TAX_F> 1 �������� ��� 3-7 �� ������������
     * ��� 2 0 ����� ��������������� ����� ����������� ��� ����� ���
     * 1 ����� ��������������� ����� ����������� � ������ ���
     * ��� 1 0 �������������� ����� ���������
     * 1 �������������� ����� ���������
     * ��� 0 0 ��� �� ������� � ����
     * 1 ��� ������� � ����
     * <TAX_VALn> 2 �������� �������� n-�� ������ ���
     * <ADD_TAX_VALn> 2 �������� �������� n-�� ������ ��������������� �����
     * <ADD_TAX_NAMEn> 20 ����� �������� n-�� ��������������� �����
     * <Z1_NUM> 2 �������� ����� �������� Z ������
     * <TURNOVERn_IN> 8 �������� ������ �� ������ n, �������
     * <TAXn_IN> 8 �������� ����� �� ������ n, �������
     * <ADD_TAXn_IN> 8 �������� ���� �� ������ n, �������
     * <TURNOVERn_OUT> 8 �������� ������ �� ������ n, ��������
     * <TAXn_OUT> 8 �������� ����� �� ������ n, ��������
     * <ADD_TAXn_OUT> 8 �������� ���� �� ������ n, ��������
     * <CHECKS_IN> 4 �������� ���������� �����, �������
     * <CHECKS_OUT> 4 �������� ���������� �����, ��������
     */
    public static X1FullResult decodeX1Full(byte[] bFile) {
        X1FullResult result = new X1FullResult();
        //������ ������ � �������
        byte[] temp4 = new byte[4];
        System.arraycopy(bFile, 0, temp4, 0, 4);
        result.setTaxNum(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        //����, ���������� ������ �� ������ ������
        byte[] temp1 = new byte[1];
        System.arraycopy(bFile, 4, temp1, 0, 1);
        byte day = ByteBuffer.wrap(temp1).get(); //getting wrapped int from 1 byte in reverse byte order
        System.arraycopy(bFile, 5, temp1, 0, 1);
        byte month = ByteBuffer.wrap(temp1).get(); //getting wrapped int from 1 byte in reverse byte order
        byte[] temp2 = new byte[2];
        System.arraycopy(bFile, 6, temp2, 0, 2);
        short year = ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort(); //getting wrapped int from 2 byte in reverse byte order
        result.setData(String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year));

        //��������� ���
        System.arraycopy(bFile, 8, temp1, 0, 1);
        //TODO �������� ����������� ���� ���������� ���
        byte taxF = ByteBuffer.wrap(temp1).order(ByteOrder.LITTLE_ENDIAN).get(); //getting wrapped 1 byte from 1 byte in reverse byte order
        result.setTaxParam(taxF);

        //�������� 1-� ������ ��� (�)
        System.arraycopy(bFile, 9, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setTaxVal1(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //�������� 1-� ������ ��������������� �����
        System.arraycopy(bFile, 11, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setAddTaxVal1(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //������������ 1-� ������ ��������������� �����
        byte[] temp20 = new byte[20];
        System.arraycopy(bFile, 13, temp20, 0, 20);
        try {
            result.setAddTaxValName1(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName1("�/�");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //�������� 2-� ������ ��� (�)
        System.arraycopy(bFile, 33, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setTaxVal2(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //�������� 2-� ������ ��������������� �����
        System.arraycopy(bFile, 35, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setAddTaxVal2(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //������������ 2-� ������ ��������������� �����
        System.arraycopy(bFile, 37, temp20, 0, 20);
        try {
            result.setAddTaxValName2(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName2("�/�");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //�������� 3-� ������ ��� (�)
        System.arraycopy(bFile, 57, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setTaxVal3(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //�������� 3-� ������ ��������������� �����
        System.arraycopy(bFile, 59, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setAddTaxVal3(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //������������ 3-� ������ ��������������� �����
        System.arraycopy(bFile, 61, temp20, 0, 20);
        try {
            result.setAddTaxValName3(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName3("�/�");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //�������� 4-� ������ ��� (�)
        System.arraycopy(bFile, 81, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setTaxVal4(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //�������� 4-� ������ ��������������� �����
        System.arraycopy(bFile, 83, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setAddTaxVal4(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //������������ 4-� ������ ��������������� �����
        System.arraycopy(bFile, 85, temp20, 0, 20);
        try {
            result.setAddTaxValName4(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName4("�/�");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //�������� 5-� ������ ��� (�)
        System.arraycopy(bFile, 105, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setTaxVal5(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //�������� 5-� ������ ��������������� �����
        System.arraycopy(bFile, 107, temp2, 0, 2);  //����� �� 100 ��� � ��������
        result.setAddTaxVal5(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 2 byte in reverse byte order

        //������������ 5-� ������ ��������������� �����
        System.arraycopy(bFile, 109, temp20, 0, 20);
        try {
            result.setAddTaxValName5(new String(temp20, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
        } catch (UnsupportedEncodingException e) {
            result.setAddTaxValName5("�/�");
            log.debug("got unsupported encoding error: " + e.toString());
        }

        //����� �������� Z-������
        System.arraycopy(bFile, 129, temp2, 0, 2);
        result.setZ1Num(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getShort()); //getting wrapped int from 2 byte in reverse byte order

        //������ �� 1-� ������ (�), �������
        byte[] temp8 = new byte[8];
        System.arraycopy(bFile, 131, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //������ �� 2-� ������ (�), �������
        System.arraycopy(bFile, 139, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverIn2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //������ �� 3-� ������ (�), �������
        System.arraycopy(bFile, 147, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //������ �� 4-� ������ (�), �������
        System.arraycopy(bFile, 155, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //������ �� 5-� ������ (�), �������
        System.arraycopy(bFile, 163, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //������ �� 6-� ������ (�), �������
        System.arraycopy(bFile, 171, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverIn6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //����� �� 1-� ������ (�), �������
        System.arraycopy(bFile, 179, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //����� �� 2-� ������ (�), �������
        System.arraycopy(bFile, 187, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxIn2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //����� �� 3-� ������ (�), �������
        System.arraycopy(bFile, 195, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //����� �� 4-� ������ (�), �������
        System.arraycopy(bFile, 203, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //����� �� 5-� ������ (�), �������
        System.arraycopy(bFile, 211, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //����� �� 6-� ������ (�), �������
        System.arraycopy(bFile, 219, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxIn6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //���� �� 1-� ������ (�), �������
        System.arraycopy(bFile, 227, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxIn1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //���� �� 2-� ������ (�), �������
        System.arraycopy(bFile, 235, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxIn2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //���� �� 3-� ������ (�), �������
        System.arraycopy(bFile, 243, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxIn3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //���� �� 4-� ������ (�), �������
        System.arraycopy(bFile, 251, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxIn4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //���� �� 5-� ������ (�), �������
        System.arraycopy(bFile, 259, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxIn5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //���� �� 6-� ������ (�), �������
        System.arraycopy(bFile, 267, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxIn6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

        //������ �� 1-� ������ (�), �������
        System.arraycopy(bFile, 275, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //������ �� 2-� ������ (�), �������
        System.arraycopy(bFile, 283, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverOut2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //������ �� 3-� ������ (�), �������
        System.arraycopy(bFile, 291, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //������ �� 4-� ������ (�), �������
        System.arraycopy(bFile, 299, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //������ �� 5-� ������ (�), �������
        System.arraycopy(bFile, 307, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //������ �� 6-� ������ (�), �������
        System.arraycopy(bFile, 315, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTurnoverOut6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //����� �� 1-� ������ (�), �������
        System.arraycopy(bFile, 323, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //����� �� 2-� ������ (�), �������
        System.arraycopy(bFile, 331, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxOut2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //����� �� 3-� ������ (�), �������
        System.arraycopy(bFile, 339, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //����� �� 4-� ������ (�), �������
        System.arraycopy(bFile, 347, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //����� �� 5-� ������ (�), �������
        System.arraycopy(bFile, 355, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //����� �� 6-� ������ (�), �������
        System.arraycopy(bFile, 363, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setTaxOut6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���� �� 1-� ������ (�), �������
        System.arraycopy(bFile, 371, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxOut1(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���� �� 2-� ������ (�), �������
        System.arraycopy(bFile, 379, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxOut2(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���� �� 3-� ������ (�), �������
        System.arraycopy(bFile, 387, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxOut3(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���� �� 4-� ������ (�), �������
        System.arraycopy(bFile, 395, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxOut4(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���� �� 5-� ������ (�), �������
        System.arraycopy(bFile, 403, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxOut5(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���� �� 6-� ������ (�), �������
        System.arraycopy(bFile, 411, temp8, 0, 8);  //����� �� 100 ��� � ��������
        result.setAddTaxOut6(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //gettOutg wrapped BigDecimal from 8 byte Out reverse byte order

        //���-�� ����� �������
        System.arraycopy(bFile, 419, temp4, 0, 4);
        result.setChecksIn(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        //���-�� ����� ��������
        System.arraycopy(bFile, 423, temp4, 0, 4);
        result.setChecksOut(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

        return result;
    }


    /**
     * ��������� ������� ���� �� ����� x3.bin
     * <p>
     * PAR3     1     �������     ���� 3-7     �� ������������
     * ��� 2     ����������/�� ���������� ������� ������ (1/0)
     * ��� 1     ����������/�� ���������� �����-��� ������ (1/0)
     * ��� 0     ����������/�� ���������� ������������ ������ (1/0)
     * CODEx     4     ��������     ��� ������
     * PRICEx     4     ��������     ���� � �������� ������
     * NAMEx     48     �����     ������������ ������
     * BARCODEx     8     ��������     �����-��� ������
     * QTYx     4     ��������     ������� ������ � �������
     * QTYx_IN     4     ��������     ���������� ������ (�������)
     * MRKPx_IN     8     ��������     ������� � �������� (�������)
     * RDCTx_IN     8     ��������     ������ � �������� (�������)
     * TRNOVRx_IN     8     ��������     ������ � �������� (�������)
     * QTYx_OUT     4     ��������     ���������� ������ (��������)
     * MRKPx_OUT     8     ��������     ������� � �������� (��������)
     * RDCTx_OUT     8     ��������     ������ � �������� (��������)
     * TRNOVRx_OUT     8     ��������     ������ � �������� (��������)
     */
    public static ArrayList<X3Result> decodeX3ToArrayList(byte[] bFile) {
        ArrayList<X3Result> resultArrayList = new ArrayList<X3Result>();
        int i = 0;
        for (int j = 1; j < bFile.length; j += 124) {
            X3Result tmp = new X3Result();
            //���
            byte[] temp1 = new byte[4];
            System.arraycopy(bFile, j, temp1, 0, 4);
            tmp.setCode(ByteBuffer.wrap(temp1).order(ByteOrder.LITTLE_ENDIAN).getInt()); //getting wrapped int from 4 byte in reverse byte order

            //����
            byte[] temp2 = new byte[4];
            System.arraycopy(bFile, j + 4, temp2, 0, 4);    //����� �� 100 ��� � ��������
            tmp.setPrice(BigDecimal.valueOf(ByteBuffer.wrap(temp2).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //������������
            byte[] temp3 = new byte[48];
            System.arraycopy(bFile, j + 4 + 4, temp3, 0, 48);
            try {
                tmp.setName(new String(temp3, "CP1251").replaceAll("\u0000.*", ""));  //getting clear string from bytes array
            } catch (UnsupportedEncodingException e) {
                log.debug("got unsupported encoding error: " + e.toString());
            }

            //��������
            byte[] temp4 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48, temp4, 0, 8);
            tmp.setBarcode(Integer.toString(ByteBuffer.wrap(temp4).order(ByteOrder.LITTLE_ENDIAN).getInt())); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //�������
            byte[] temp5 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8, temp5, 0, 4);    //����� �� 1000 ��� � �������
            tmp.setQty(BigDecimal.valueOf(ByteBuffer.wrap(temp5).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //���������� (�������)
            byte[] temp6 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4, temp6, 0, 4);       //����� �� 1000 ��� � �������
            tmp.setQtyIn(BigDecimal.valueOf(ByteBuffer.wrap(temp6).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //������� (�������)
            byte[] temp7 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4, temp7, 0, 8);    //����� �� 100 ��� � ��������
            tmp.setMRKPIn(BigDecimal.valueOf(ByteBuffer.wrap(temp7).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //������ (�������)
            byte[] temp8 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8, temp8, 0, 8);    //����� �� 100 ��� � ��������
            tmp.setRDCTIn(BigDecimal.valueOf(ByteBuffer.wrap(temp8).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //������ (�������)
            byte[] temp9 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8, temp9, 0, 8);    //����� �� 100 ��� � ��������
            tmp.setTRNOVRIn(BigDecimal.valueOf(ByteBuffer.wrap(temp9).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order


            //���������� (�������)
            byte[] temp10 = new byte[4];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8, temp10, 0, 4);       //����� �� 1000 ��� � �������
            tmp.setQtyOut(BigDecimal.valueOf(ByteBuffer.wrap(temp10).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_CEILING)); //getting wrapped BigDecimal from 4 byte in reverse byte order

            //������� (�������)
            byte[] temp11 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8 + 4, temp11, 0, 8);    //����� �� 100 ��� � ��������
            tmp.setMRKPOut(BigDecimal.valueOf(ByteBuffer.wrap(temp11).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //������ (�������)
            byte[] temp12 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8 + 4 + 8, temp12, 0, 8);    //����� �� 100 ��� � ��������
            tmp.setRDCTOut(BigDecimal.valueOf(ByteBuffer.wrap(temp12).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            //������ (�������)
            byte[] temp13 = new byte[8];
            System.arraycopy(bFile, j + 4 + 4 + 48 + 8 + 4 + 4 + 8 + 8 + 8 + 4 + 8 + 8, temp13, 0, 8);    //����� �� 100 ��� � ��������
            tmp.setTRNOVROut(BigDecimal.valueOf(ByteBuffer.wrap(temp13).order(ByteOrder.LITTLE_ENDIAN).getInt()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN)); //getting wrapped BigDecimal from 8 byte in reverse byte order

            resultArrayList.add(new X3Result());
            resultArrayList.set(i, tmp);
            i++;
        }
        return resultArrayList;
    }


    /**
     * ������� ������, ���������� �� ������ �3 � ���� Excel
     *
     * @param x1FullResult      ������ � ������� �� ��������������� ����� x1.bin
     * @param x3ResultArrayList ������ �������� � ������� �� ��������������� ����� x3.bin
     * @param excelFilePath     ���� � ��� ����� xlsx
     * @param X1sheetName       ��� ����� ��� ������� �������� ������
     * @param X3sheetName       ��� ����� ��� ������ �� ������� � �����
     * @return successfulness of operation
     */
    public static boolean writeToXlsx(X1FullResult x1FullResult, ArrayList<X3Result> x3ResultArrayList, String excelFilePath, String X1sheetName, String X3sheetName) {

        try {
            //�������� �����
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


            //���������� ����� � ������� � ������� (�3)
            Sheet sheet = workbook.createSheet(X3sheetName); //���� ���� �� ���������� - ������� ���
            ListIterator<X3Result> x3ResultListIterator = x3ResultArrayList.listIterator();

            Row row;
            row = sheet.createRow(1);    //������� ������ � ���������� "�������" � "�������"
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
            cell4.setCellValue("�������");
            Cell cell8 = row.createCell(8, Cell.CELL_TYPE_STRING);
            cell8.setCellValue("�������");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 7));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 11));
            cell4.setCellStyle(styleBoldHCenter);
            cell8.setCellStyle(styleBoldHCenter);
            //style.setAlignment(CellStyle.ALIGN_GENERAL);

            row = sheet.createRow(2);    //������� ������ � ���������� ��������
            String[] heading = {"���", "������������ ������", "����", "����������", "�������", "������", "������", "����������", "�������", "������", "������", "��������", "�������"};
            int s = 1;
            for (String str : heading) {
                Cell cell = row.createCell(s, Cell.CELL_TYPE_STRING);
                cell.setCellValue(str);
                cell.setCellStyle(styleBoldHCenter);
                s++;
            }

            int rowIndex = 3;   //������ ������ � 4-�!!! ������
            //int colIndex = 0;   //� 1-��!!! �������

            while (x3ResultListIterator.hasNext()) {
                row = sheet.createRow(rowIndex);    //������� ������ � ����������� � ��� ������ � �������
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
