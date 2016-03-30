package AppPackage.Utils;

import java.math.BigDecimal;

/**
 * Created by Eraser on 24.02.2016.
 * <p>
 * Class for decoded X5 cashier report from MiniFP-54
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
public class X5Result {
    private BigDecimal PayCashIn;
    private BigDecimal PayCheckIn;
    private BigDecimal PayCCIn;
    private BigDecimal PayUserIn1;
    private BigDecimal PayUserIn2;
    private BigDecimal PayUserIn3;
    private BigDecimal PayUserIn4;
    private BigDecimal PayUserIn5;
    private BigDecimal PayCashOut;
    private BigDecimal PayCheckOut;
    private BigDecimal PayCCOut;
    private BigDecimal PayUserOut1;
    private BigDecimal PayUserOut2;
    private BigDecimal PayUserOut3;
    private BigDecimal PayUserOut4;
    private BigDecimal PayUserOut5;
    private BigDecimal CashIn;
    private BigDecimal CheckIn;
    private BigDecimal CCIn;
    private BigDecimal UserIn1;
    private BigDecimal UserIn2;
    private BigDecimal UserIn3;
    private BigDecimal UserIn4;
    private BigDecimal UserIn5;
    private BigDecimal CashOut;
    private BigDecimal CheckOut;
    private BigDecimal CCOut;
    private BigDecimal UserOut1;
    private BigDecimal UserOut2;
    private BigDecimal UserOut3;
    private BigDecimal UserOut4;
    private BigDecimal UserOut5;

    private int AbortChecks;
    private int AbortPos;

    public X5Result() {
    }

     BigDecimal getPayCashIn() {
        return PayCashIn;
    }

     void setPayCashIn(BigDecimal payCashIn) {
        PayCashIn = payCashIn;
    }

     BigDecimal getPayCheckIn() {
        return PayCheckIn;
    }

     void setPayCheckIn(BigDecimal payCheckIn) {
        PayCheckIn = payCheckIn;
    }

     BigDecimal getPayCCIn() {
        return PayCCIn;
    }

     void setPayCCIn(BigDecimal payCCIn) {
        PayCCIn = payCCIn;
    }

     BigDecimal getPayUserIn1() {
        return PayUserIn1;
    }

     void setPayUserIn1(BigDecimal payUserIn1) {
        PayUserIn1 = payUserIn1;
    }

     BigDecimal getPayUserIn2() {
        return PayUserIn2;
    }

     void setPayUserIn2(BigDecimal payUserIn2) {
        PayUserIn2 = payUserIn2;
    }

     BigDecimal getPayUserIn3() {
        return PayUserIn3;
    }

     void setPayUserIn3(BigDecimal payUserIn3) {
        PayUserIn3 = payUserIn3;
    }

     BigDecimal getPayUserIn4() {
        return PayUserIn4;
    }

     void setPayUserIn4(BigDecimal payUserIn4) {
        PayUserIn4 = payUserIn4;
    }

     BigDecimal getPayUserIn5() {
        return PayUserIn5;
    }

     void setPayUserIn5(BigDecimal payUserIn5) {
        PayUserIn5 = payUserIn5;
    }

     BigDecimal getPayCashOut() {
        return PayCashOut;
    }

     void setPayCashOut(BigDecimal payCashOut) {
        PayCashOut = payCashOut;
    }

     BigDecimal getPayCheckOut() {
        return PayCheckOut;
    }

     void setPayCheckOut(BigDecimal payCheckOut) {
        PayCheckOut = payCheckOut;
    }

     BigDecimal getPayCCOut() {
        return PayCCOut;
    }

     void setPayCCOut(BigDecimal payCCOut) {
        PayCCOut = payCCOut;
    }

     BigDecimal getPayUserOut1() {
        return PayUserOut1;
    }

     void setPayUserOut1(BigDecimal payUserOut1) {
        PayUserOut1 = payUserOut1;
    }

     BigDecimal getPayUserOut2() {
        return PayUserOut2;
    }

     void setPayUserOut2(BigDecimal payUserOut2) {
        PayUserOut2 = payUserOut2;
    }

     BigDecimal getPayUserOut3() {
        return PayUserOut3;
    }

     void setPayUserOut3(BigDecimal payUserOut3) {
        PayUserOut3 = payUserOut3;
    }

     BigDecimal getPayUserOut4() {
        return PayUserOut4;
    }

     void setPayUserOut4(BigDecimal payUserOut4) {
        PayUserOut4 = payUserOut4;
    }

     BigDecimal getPayUserOut5() {
        return PayUserOut5;
    }

     void setPayUserOut5(BigDecimal payUserOut5) {
        PayUserOut5 = payUserOut5;
    }

     BigDecimal getCashIn() {
        return CashIn;
    }

     void setCashIn(BigDecimal cashIn) {
        CashIn = cashIn;
    }

     BigDecimal getCheckIn() {
        return CheckIn;
    }

     void setCheckIn(BigDecimal checkIn) {
        CheckIn = checkIn;
    }

     BigDecimal getCCIn() {
        return CCIn;
    }

     void setCCIn(BigDecimal CCIn) {
        this.CCIn = CCIn;
    }

     BigDecimal getUserIn1() {
        return UserIn1;
    }

     void setUserIn1(BigDecimal userIn1) {
        UserIn1 = userIn1;
    }

     BigDecimal getUserIn2() {
        return UserIn2;
    }

     void setUserIn2(BigDecimal userIn2) {
        UserIn2 = userIn2;
    }

     BigDecimal getUserIn3() {
        return UserIn3;
    }

     void setUserIn3(BigDecimal userIn3) {
        UserIn3 = userIn3;
    }

     BigDecimal getUserIn4() {
        return UserIn4;
    }

     void setUserIn4(BigDecimal userIn4) {
        UserIn4 = userIn4;
    }

     BigDecimal getUserIn5() {
        return UserIn5;
    }

     void setUserIn5(BigDecimal userIn5) {
        UserIn5 = userIn5;
    }

     BigDecimal getCashOut() {
        return CashOut;
    }

     void setCashOut(BigDecimal cashOut) {
        CashOut = cashOut;
    }

     BigDecimal getCheckOut() {
        return CheckOut;
    }

     void setCheckOut(BigDecimal checkOut) {
        CheckOut = checkOut;
    }

     BigDecimal getCCOut() {
        return CCOut;
    }

     void setCCOut(BigDecimal CCOut) {
        this.CCOut = CCOut;
    }

     BigDecimal getUserOut1() {
        return UserOut1;
    }

     void setUserOut1(BigDecimal userOut1) {
        UserOut1 = userOut1;
    }

     BigDecimal getUserOut2() {
        return UserOut2;
    }

     void setUserOut2(BigDecimal userOut2) {
        UserOut2 = userOut2;
    }

     BigDecimal getUserOut3() {
        return UserOut3;
    }

     void setUserOut3(BigDecimal userOut3) {
        UserOut3 = userOut3;
    }

     BigDecimal getUserOut4() {
        return UserOut4;
    }

     void setUserOut4(BigDecimal userOut4) {
        UserOut4 = userOut4;
    }

     BigDecimal getUserOut5() {
        return UserOut5;
    }

     void setUserOut5(BigDecimal userOut5) {
        UserOut5 = userOut5;
    }

     int getAbortChecks() {
        return AbortChecks;
    }

     void setAbortChecks(int abortChecks) {
        AbortChecks = abortChecks;
    }

     int getAbortPos() {
        return AbortPos;
    }

     void setAbortPos(int abortPos) {
        AbortPos = abortPos;
    }
    
    
}
