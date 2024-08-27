package com.codecorecix.ecommerce.maintenance.product.image.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageRequestDto {

  private Integer id;

  private String imageUrl;

  private Integer productId;

  private MultipartFile file;
}
