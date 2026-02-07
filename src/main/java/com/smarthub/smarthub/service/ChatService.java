package com.smarthub.smarthub.service;

import com.smarthub.smarthub.domain.Product;
import com.smarthub.smarthub.domain.dto.GoogleAIRequest;
import com.smarthub.smarthub.domain.dto.GoogleAIResponse;
import com.smarthub.smarthub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ChatService {

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Value("${google.ai.api-url}")
    private String apiUrl;

    private static String promtFirst = """
        Bạn hãy dựa vào danh sách sản phẩm của shop tôi và đựa ra lựa chọn phù hợp 
        theo yêu cầu mà khách hàng mong muốn
    """;

    private final WebClient webClient;
    private final ProductRepository productRepository;

    public ChatService(WebClient.Builder webClientBuilder, ProductRepository productRepository) {
        this.webClient = webClientBuilder.build();
        this.productRepository = productRepository;
    }

    public String chat(String message) {
        // Tạo request
        GoogleAIRequest.Part part = new GoogleAIRequest.Part();

        List<Product> products = productRepository.findAll();

        part.setText(buildPromt(products, message));

        GoogleAIRequest.Content content = new GoogleAIRequest.Content();
        content.setParts(List.of(part));

        GoogleAIRequest request = new GoogleAIRequest();
        request.setContents(List.of(content));

        // Call API
        GoogleAIResponse response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GoogleAIResponse.class)
                .block();

        // Extract response
        return response.getCandidates().get(0)
                .getContent().getParts().get(0).getText();
    }

    private String buildPromt(List<Product> productList, String message) {
        StringBuilder prompt = new StringBuilder();

        // 1. Thiết lập vai trò và nhiệm vụ
        prompt.append("### ROLE: Chuyên gia tư vấn điện thoại thông minh.\n");
        prompt.append("### NHIỆM VỤ: Phân tích yêu cầu khách hàng và gợi ý sản phẩm phù hợp nhất từ danh sách cho sẵn.\n\n");

        // 2. Danh sách sản phẩm (Định dạng lại để AI dễ đọc)
        prompt.append("### DANH SÁCH SẢN PHẨM CỦA CỬA HÀNG:\n");
        for (Product item : productList) {
            // Định dạng: ID | Tên | Giá | Cấu hình (RAM/Storage/Pin/Màn hình) | Mô tả
            prompt.append(String.format("- ID: %d | Tên: %s | Giá: %,d VNĐ | Cấu hình: [RAM: %dGB, ROM: %dGB, Pin: %d mAh, Màn hình: %s] | Mô tả: %s\n",
                    item.getId(), item.getName(), item.getPrice(), item.getRam(), item.getStorage(),
                    item.getBattery(), item.getScreenSize(), item.getDescription() != null ? item.getDescription() : "Đang cập nhật"
            ));
        }

        // 3. Yêu cầu khách hàng
        prompt.append("\n### YÊU CẦU CỦA KHÁCH HÀNG:\n\"").append(message).append("\"\n\n");

        // 4. Các ràng buộc nghiêm ngặt
        prompt.append("### RÀNG BUỘC PHẢI TUÂN THỦ:\n");
        prompt.append("1. Chỉ được phép tư vấn về các sản phẩm điện thoại trong danh sách trên.\n");
        prompt.append("2. Nếu khách hàng hỏi vấn đề không liên quan, hãy trả lời chính xác câu: \"Tôi là chuyên gia tư vấn sản phẩm, không thể trả lời vấn đề khác.\"\n");
        prompt.append("3. ĐỊNH DẠNG TRẢ LỜI: Liệt kê tối đa 5 ID sản phẩm phù hợp nhất theo định dạng: 1, 2, 3, 4, 5\n");
        prompt.append("4. Không trả lời thêm bất cứ gì.\n");

        return prompt.toString();
    }
}
