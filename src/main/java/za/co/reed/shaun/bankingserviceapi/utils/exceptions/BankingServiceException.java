package za.co.reed.shaun.bankingserviceapi.utils.exceptions;

public class BankingServiceException {
    public static class AccountExistsException extends RuntimeException {
        public AccountExistsException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class AccountTransactionException extends RuntimeException {
        public AccountTransactionException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
