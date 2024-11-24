package project.ecommerce.userMangementService.enums;

public enum PaymentStatusEnum {

    SUCCESS("success"),
    FAILED("failed"),
    PENDING("pending");

    private final String status;

    PaymentStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
