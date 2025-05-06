package kz.roman.enttgbot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ApiResponse", description = "Универсальный ответ API")
public class ApiResponse<T> {
    @Schema(description = "Список данных", example = "[{\"id\":1,\"name\":\"Example\"}]")
    private List<T> data;
    @Schema(description = "Ошибки, если таковые есть")
    private List<ErrorDetail> errors;

    public static <T> ApiResponse<T> success(List<T> data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(List.of(data), null);
    }

    public static ApiResponse<?> error(List<ErrorDetail> errors) {
        return new ApiResponse<>(null, errors);
    }
}