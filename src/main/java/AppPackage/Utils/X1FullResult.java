package AppPackage.Utils;

import java.math.BigDecimal;

/**
 * Created by Eraser on 24.02.2016.
 * <p>
 * Class for decoded X1 full daily report from MiniFP-54
 * <p>
 * <TNUM> 4 двоичный Номер записи о налогах
 * <DATA> 4 двоичный дата записи ставок НДС
 *        байт 1 Дата, Байт 2 месяц, Байты 3,4 год
 * <TAX_F> 1 бинарный Бит 3-7 Не используются
 * Бит 2 0 Сумма дополнительного сбора вычисляется без учета НДС
 *       1 Сумма дополнительного сбора вычисляется с учетом НДС
 * Бит 1 0 Дополнительные сборы запрещены
 *       1 Дополнительные сборы разрешены
 * Бит 0 0 НДС не включен в цену
 *       1 НДС включен в цену
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
public class X1FullResult {
    private int TaxNum;
    private String Data;
    private int TaxParam;
    private BigDecimal TaxVal1;
    private BigDecimal AddTaxVal1;
    private String AddTaxValName1;
    private BigDecimal TaxVal2;
    private BigDecimal AddTaxVal2;
    private String AddTaxValName2;
    private BigDecimal TaxVal3;
    private BigDecimal AddTaxVal3;
    private String AddTaxValName3;
    private BigDecimal TaxVal4;
    private BigDecimal AddTaxVal4;
    private String AddTaxValName4;
    private BigDecimal TaxVal5;
    private BigDecimal AddTaxVal5;
    private String AddTaxValName5;
    private int Z1Num;
    private BigDecimal TurnoverIn1;
    private BigDecimal TurnoverIn2;
    private BigDecimal TurnoverIn3;
    private BigDecimal TurnoverIn4;
    private BigDecimal TurnoverIn5;
    private BigDecimal TurnoverIn6;
    private BigDecimal TaxIn1;
    private BigDecimal TaxIn2;
    private BigDecimal TaxIn3;
    private BigDecimal TaxIn4;
    private BigDecimal TaxIn5;
    private BigDecimal TaxIn6;
    private BigDecimal AddTaxIn1;
    private BigDecimal AddTaxIn2;
    private BigDecimal AddTaxIn3;
    private BigDecimal AddTaxIn4;
    private BigDecimal AddTaxIn5;
    private BigDecimal AddTaxIn6;
    private BigDecimal TurnoverOut1;
    private BigDecimal TurnoverOut2;
    private BigDecimal TurnoverOut3;
    private BigDecimal TurnoverOut4;
    private BigDecimal TurnoverOut5;
    private BigDecimal TurnoverOut6;
    private BigDecimal TaxOut1;
    private BigDecimal TaxOut2;
    private BigDecimal TaxOut3;
    private BigDecimal TaxOut4;
    private BigDecimal TaxOut5;
    private BigDecimal TaxOut6;
    private BigDecimal AddTaxOut1;
    private BigDecimal AddTaxOut2;
    private BigDecimal AddTaxOut3;
    private BigDecimal AddTaxOut4;
    private BigDecimal AddTaxOut5;
    private BigDecimal AddTaxOut6;
    private int ChecksIn;
    private int ChecksOut;


    public X1FullResult() {
    }

     int getTaxNum() {
        return TaxNum;
    }

     void setTaxNum(int taxNum) {
        TaxNum = taxNum;
    }

     String getData() {
        return Data;
    }

     void setData(String data) {
        Data = data;
    }

     int getTaxParam() {
        return TaxParam;
    }

     void setTaxParam(int taxParam) {
        TaxParam = taxParam;
    }

     BigDecimal getTaxVal1() {
        return TaxVal1;
    }

     void setTaxVal1(BigDecimal taxVal1) {
        TaxVal1 = taxVal1;
    }

     BigDecimal getAddTaxVal1() {
        return AddTaxVal1;
    }

     void setAddTaxVal1(BigDecimal addTaxVal1) {
        AddTaxVal1 = addTaxVal1;
    }

     String getAddTaxValName1() {
        return AddTaxValName1;
    }

     void setAddTaxValName1(String addTaxValName1) {
        AddTaxValName1 = addTaxValName1;
    }

     BigDecimal getTaxVal2() {
        return TaxVal2;
    }

     void setTaxVal2(BigDecimal taxVal2) {
        TaxVal2 = taxVal2;
    }

     BigDecimal getAddTaxVal2() {
        return AddTaxVal2;
    }

     void setAddTaxVal2(BigDecimal addTaxVal2) {
        AddTaxVal2 = addTaxVal2;
    }

     String getAddTaxValName2() {
        return AddTaxValName2;
    }

     void setAddTaxValName2(String addTaxValName2) {
        AddTaxValName2 = addTaxValName2;
    }

     BigDecimal getTaxVal3() {
        return TaxVal3;
    }

     void setTaxVal3(BigDecimal taxVal3) {
        TaxVal3 = taxVal3;
    }

     BigDecimal getAddTaxVal3() {
        return AddTaxVal3;
    }

     void setAddTaxVal3(BigDecimal addTaxVal3) {
        AddTaxVal3 = addTaxVal3;
    }

     String getAddTaxValName3() {
        return AddTaxValName3;
    }

     void setAddTaxValName3(String addTaxValName3) {
        AddTaxValName3 = addTaxValName3;
    }

     BigDecimal getTaxVal4() {
        return TaxVal4;
    }

     void setTaxVal4(BigDecimal taxVal4) {
        TaxVal4 = taxVal4;
    }

     BigDecimal getAddTaxVal4() {
        return AddTaxVal4;
    }

     void setAddTaxVal4(BigDecimal addTaxVal4) {
        AddTaxVal4 = addTaxVal4;
    }

     String getAddTaxValName4() {
        return AddTaxValName4;
    }

     void setAddTaxValName4(String addTaxValName4) {
        AddTaxValName4 = addTaxValName4;
    }

     BigDecimal getTaxVal5() {
        return TaxVal5;
    }

     void setTaxVal5(BigDecimal taxVal5) {
        TaxVal5 = taxVal5;
    }

     BigDecimal getAddTaxVal5() {
        return AddTaxVal5;
    }

    void setAddTaxVal5(BigDecimal addTaxVal5) {
        AddTaxVal5 = addTaxVal5;
    }

     String getAddTaxValName5() {
        return AddTaxValName5;
    }

     void setAddTaxValName5(String addTaxValName5) {
        AddTaxValName5 = addTaxValName5;
    }

     int getZ1Num() {
        return Z1Num;
    }

     void setZ1Num(int z1Num) {
        Z1Num = z1Num;
    }

     BigDecimal getTurnoverIn1() {
        return TurnoverIn1;
    }

     void setTurnoverIn1(BigDecimal turnoverIn1) {
        TurnoverIn1 = turnoverIn1;
    }

     BigDecimal getTurnoverIn2() {
        return TurnoverIn2;
    }

     void setTurnoverIn2(BigDecimal turnoverIn2) {
        TurnoverIn2 = turnoverIn2;
    }

     BigDecimal getTurnoverIn3() {
        return TurnoverIn3;
    }

     void setTurnoverIn3(BigDecimal turnoverIn3) {
        TurnoverIn3 = turnoverIn3;
    }

     BigDecimal getTurnoverIn4() {
        return TurnoverIn4;
    }

     void setTurnoverIn4(BigDecimal turnoverIn4) {
        TurnoverIn4 = turnoverIn4;
    }

     BigDecimal getTurnoverIn5() {
        return TurnoverIn5;
    }

     void setTurnoverIn5(BigDecimal turnoverIn5) {
        TurnoverIn5 = turnoverIn5;
    }

     BigDecimal getTurnoverIn6() {
        return TurnoverIn6;
    }

     void setTurnoverIn6(BigDecimal turnoverIn6) {
        TurnoverIn6 = turnoverIn6;
    }

     BigDecimal getTaxIn1() {
        return TaxIn1;
    }

     void setTaxIn1(BigDecimal taxIn1) {
        TaxIn1 = taxIn1;
    }

     BigDecimal getTaxIn2() {
        return TaxIn2;
    }

     void setTaxIn2(BigDecimal taxIn2) {
        TaxIn2 = taxIn2;
    }

     BigDecimal getTaxIn3() {
        return TaxIn3;
    }

     void setTaxIn3(BigDecimal taxIn3) {
        TaxIn3 = taxIn3;
    }

     BigDecimal getTaxIn4() {
        return TaxIn4;
    }

     void setTaxIn4(BigDecimal taxIn4) {
        TaxIn4 = taxIn4;
    }

     BigDecimal getTaxIn5() {
        return TaxIn5;
    }

     void setTaxIn5(BigDecimal taxIn5) {
        TaxIn5 = taxIn5;
    }

     BigDecimal getTaxIn6() {
        return TaxIn6;
    }

     void setTaxIn6(BigDecimal taxIn6) {
        TaxIn6 = taxIn6;
    }

     BigDecimal getAddTaxIn1() {
        return AddTaxIn1;
    }

     void setAddTaxIn1(BigDecimal addTaxIn1) {
        AddTaxIn1 = addTaxIn1;
    }

     BigDecimal getAddTaxIn2() {
        return AddTaxIn2;
    }

     void setAddTaxIn2(BigDecimal addTaxIn2) {
        AddTaxIn2 = addTaxIn2;
    }

     BigDecimal getAddTaxIn3() {
        return AddTaxIn3;
    }

     void setAddTaxIn3(BigDecimal addTaxIn3) {
        AddTaxIn3 = addTaxIn3;
    }

     BigDecimal getAddTaxIn4() {
        return AddTaxIn4;
    }

     void setAddTaxIn4(BigDecimal addTaxIn4) {
        AddTaxIn4 = addTaxIn4;
    }

     BigDecimal getAddTaxIn5() {
        return AddTaxIn5;
    }

     void setAddTaxIn5(BigDecimal addTaxIn5) {
        AddTaxIn5 = addTaxIn5;
    }

     BigDecimal getAddTaxIn6() {
        return AddTaxIn6;
    }

     void setAddTaxIn6(BigDecimal addTaxIn6) {
        AddTaxIn6 = addTaxIn6;
    }

     BigDecimal getTurnoverOut1() {
        return TurnoverOut1;
    }

     void setTurnoverOut1(BigDecimal turnoverOut1) {
        TurnoverOut1 = turnoverOut1;
    }

     BigDecimal getTurnoverOut2() {
        return TurnoverOut2;
    }

     void setTurnoverOut2(BigDecimal turnoverOut2) {
        TurnoverOut2 = turnoverOut2;
    }

     BigDecimal getTurnoverOut3() {
        return TurnoverOut3;
    }

     void setTurnoverOut3(BigDecimal turnoverOut3) {
        TurnoverOut3 = turnoverOut3;
    }

     BigDecimal getTurnoverOut4() {
        return TurnoverOut4;
    }

     void setTurnoverOut4(BigDecimal turnoverOut4) {
        TurnoverOut4 = turnoverOut4;
    }

     BigDecimal getTurnoverOut5() {
        return TurnoverOut5;
    }

     void setTurnoverOut5(BigDecimal turnoverOut5) {
        TurnoverOut5 = turnoverOut5;
    }

     BigDecimal getTurnoverOut6() {
        return TurnoverOut6;
    }

     void setTurnoverOut6(BigDecimal turnoverOut6) {
        TurnoverOut6 = turnoverOut6;
    }

     BigDecimal getTaxOut1() {
        return TaxOut1;
    }

     void setTaxOut1(BigDecimal taxOut1) {
        TaxOut1 = taxOut1;
    }

     BigDecimal getTaxOut2() {
        return TaxOut2;
    }

     void setTaxOut2(BigDecimal taxOut2) {
        TaxOut2 = taxOut2;
    }

     BigDecimal getTaxOut3() {
        return TaxOut3;
    }

     void setTaxOut3(BigDecimal taxOut3) {
        TaxOut3 = taxOut3;
    }

     BigDecimal getTaxOut4() {
        return TaxOut4;
    }

     void setTaxOut4(BigDecimal taxOut4) {
        TaxOut4 = taxOut4;
    }

     BigDecimal getTaxOut5() {
        return TaxOut5;
    }

     void setTaxOut5(BigDecimal taxOut5) {
        TaxOut5 = taxOut5;
    }

     BigDecimal getTaxOut6() {
        return TaxOut6;
    }

     void setTaxOut6(BigDecimal taxOut6) {
        TaxOut6 = taxOut6;
    }

     BigDecimal getAddTaxOut1() {
        return AddTaxOut1;
    }

     void setAddTaxOut1(BigDecimal addTaxOut1) {
        AddTaxOut1 = addTaxOut1;
    }

     BigDecimal getAddTaxOut2() {
        return AddTaxOut2;
    }

     void setAddTaxOut2(BigDecimal addTaxOut2) {
        AddTaxOut2 = addTaxOut2;
    }

     BigDecimal getAddTaxOut3() {
        return AddTaxOut3;
    }

     void setAddTaxOut3(BigDecimal addTaxOut3) {
        AddTaxOut3 = addTaxOut3;
    }

     BigDecimal getAddTaxOut4() {
        return AddTaxOut4;
    }

     void setAddTaxOut4(BigDecimal addTaxOut4) {
        AddTaxOut4 = addTaxOut4;
    }

     BigDecimal getAddTaxOut5() {
        return AddTaxOut5;
    }

     void setAddTaxOut5(BigDecimal addTaxOut5) {
        AddTaxOut5 = addTaxOut5;
    }

     BigDecimal getAddTaxOut6() {
        return AddTaxOut6;
    }

     void setAddTaxOut6(BigDecimal addTaxOut6) {
        AddTaxOut6 = addTaxOut6;
    }

     int getChecksIn() {
        return ChecksIn;
    }

     void setChecksIn(int checksIn) {
        ChecksIn = checksIn;
    }

     int getChecksOut() {
        return ChecksOut;
    }

     void setChecksOut(int checksOut) {
        ChecksOut = checksOut;
    }


    @Override
    public String toString() {
        return "X1FullResult{" +
                "TaxNum=" + TaxNum +
                ", Data='" + Data + '\'' +
                ", TaxParam=" + TaxParam +
                ", TaxVal1=" + TaxVal1 +
                ", AddTaxVal1=" + AddTaxVal1 +
                ", AddTaxValName1='" + AddTaxValName1 + '\'' +
                ", TaxVal2=" + TaxVal2 +
                ", AddTaxVal2=" + AddTaxVal2 +
                ", AddTaxValName2='" + AddTaxValName2 + '\'' +
                ", TaxVal3=" + TaxVal3 +
                ", AddTaxVal3=" + AddTaxVal3 +
                ", AddTaxValName3='" + AddTaxValName3 + '\'' +
                ", TaxVal4=" + TaxVal4 +
                ", AddTaxVal4=" + AddTaxVal4 +
                ", AddTaxValName4='" + AddTaxValName4 + '\'' +
                ", TaxVal5=" + TaxVal5 +
                ", AddTaxVal5=" + AddTaxVal5 +
                ", AddTaxValName5='" + AddTaxValName5 + '\'' +
                ", Z1Num=" + Z1Num +
                ", TurnoverIn1=" + TurnoverIn1 +
                ", TurnoverIn2=" + TurnoverIn2 +
                ", TurnoverIn3=" + TurnoverIn3 +
                ", TurnoverIn4=" + TurnoverIn4 +
                ", TurnoverIn5=" + TurnoverIn5 +
                ", TurnoverIn6=" + TurnoverIn6 +
                ", TaxIn1=" + TaxIn1 +
                ", TaxIn2=" + TaxIn2 +
                ", TaxIn3=" + TaxIn3 +
                ", TaxIn4=" + TaxIn4 +
                ", TaxIn5=" + TaxIn5 +
                ", TaxIn6=" + TaxIn6 +
                ", AddTaxIn1=" + AddTaxIn1 +
                ", AddTaxIn2=" + AddTaxIn2 +
                ", AddTaxIn3=" + AddTaxIn3 +
                ", AddTaxIn4=" + AddTaxIn4 +
                ", AddTaxIn5=" + AddTaxIn5 +
                ", AddTaxIn6=" + AddTaxIn6 +
                ", TurnoverOut1=" + TurnoverOut1 +
                ", TurnoverOut2=" + TurnoverOut2 +
                ", TurnoverOut3=" + TurnoverOut3 +
                ", TurnoverOut4=" + TurnoverOut4 +
                ", TurnoverOut5=" + TurnoverOut5 +
                ", TurnoverOut6=" + TurnoverOut6 +
                ", TaxOut1=" + TaxOut1 +
                ", TaxOut2=" + TaxOut2 +
                ", TaxOut3=" + TaxOut3 +
                ", TaxOut4=" + TaxOut4 +
                ", TaxOut5=" + TaxOut5 +
                ", TaxOut6=" + TaxOut6 +
                ", AddTaxOut1=" + AddTaxOut1 +
                ", AddTaxOut2=" + AddTaxOut2 +
                ", AddTaxOut3=" + AddTaxOut3 +
                ", AddTaxOut4=" + AddTaxOut4 +
                ", AddTaxOut5=" + AddTaxOut5 +
                ", AddTaxOut6=" + AddTaxOut6 +
                ", ChecksIn=" + ChecksIn +
                ", ChecksOut=" + ChecksOut +
                '}';
    }
}
