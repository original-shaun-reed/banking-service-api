package za.co.reed.shaun.bankingserviceapi.utils;

public enum AccountType {
    SAVINGS,
    CURRENT;

    public static Boolean doesAccountExist(AccountType accountType) {
        Boolean doesExist = Boolean.FALSE;

        for (AccountType type : AccountType.values()) {
            if (type.equals(accountType)) {
                doesExist = Boolean.TRUE;
                break;
            }
        }

        return doesExist;
    }
}
