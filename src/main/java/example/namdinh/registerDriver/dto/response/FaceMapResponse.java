package example.namdinh.registerDriver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceMapResponse {
    private Long driverId;
    private String faceModel; // Tên trường 'face' trong Entity được đổi thành 'faceModel' để dễ hiểu
    private boolean isActivated; // Pi cần biết trạng thái để biết tài khoản đã hoàn tất chưa
}