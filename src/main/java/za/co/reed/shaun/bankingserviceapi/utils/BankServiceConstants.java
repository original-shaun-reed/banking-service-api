package za.co.reed.shaun.bankingserviceapi.utils;

public class BankServiceConstants {
    public  static final Double overdraftMaxAmount = -100000.00;
    public  static final Double minimumSavingsAccountBalance = 1000.00;
    public static final String validateAccountHolderNameMsg = "Please provide account holder name to open account";
    public static final String validateAccountHolderSurnameMsg = "Please provide account holder surname to open account";
    public static final String validateAccountNumberMsg = "Please provide account number to open account";
    public static final String validateAccountNumberNegativeMsg = "Account number can't be a negative number";
    public static final String validateAccountTypeMsg = "Please provide an account type (e.g. CURRENT or SAVINGS)";
    public static final String validateAmountNegativeMsg = "Amount can't be a negative number";
    public static final String validateAmountMsg = "Please provide amount";
    public static final String validateMinimumAmountMsg = "Amount must at least be ";
}
