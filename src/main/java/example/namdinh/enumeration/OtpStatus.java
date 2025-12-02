package example.namdinh.enumeration;

public enum OtpStatus {
    PENDING,    // Đang chờ xác minh
    VERIFIED,   // Đã xác minh thành công
    EXPIRED,    // Đã hết hạn (do hết thời gian)
    USED
}
