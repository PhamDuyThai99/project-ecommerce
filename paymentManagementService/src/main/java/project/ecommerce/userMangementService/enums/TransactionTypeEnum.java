package project.ecommerce.userMangementService.enums;

public enum TransactionTypeEnum {
    WITHDRAW("WITHDRAW"),
    RELOAD("RELOAD"),
    P2P("P2P"),
    CART("CART")
    ;

    private final String transactionType;

    TransactionTypeEnum(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
