package com.codecorecix.ecommerce.maintenance.drive.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Objects;

import com.codecorecix.ecommerce.event.entities.ProductImage;
import com.codecorecix.ecommerce.maintenance.drive.service.GoogleDriveService;
import com.codecorecix.ecommerce.maintenance.product.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/googleDrive")
public class GoogleDriveController {

  private final GoogleDriveService googleDriveService;

  private final ProductService productService;

  public GoogleDriveController(GoogleDriveService googleDriveService, ProductService productService) {
    this.googleDriveService = googleDriveService;
    this.productService = productService;
  }

  @PostMapping("/uploadImageProduct")
  public ResponseEntity<ProductImage> uploadImage(@RequestParam("file") final MultipartFile file,
      @RequestParam("productId") final Integer productId) {
    try {
      // Guardamos el archivo en el directorio temporal
      Path tempDir = Files.createTempDirectory("");
      Path tempFilePath = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
      Files.write(tempFilePath, file.getBytes());

      // Subir archivo a Google Drive en la carpeta "products"
      final String fileUrl = this.googleDriveService.uploadFile(tempFilePath.toFile(), file.getContentType(), "products");

      // Limpiar archivos temporales
      Files.delete(tempFilePath);
      Files.delete(tempDir);
      System.out.println(fileUrl);
      /*// Crear y guardar objeto ProductImage
      ProductImage productImage = new ProductImage();
      productImage.setImagenUrl(fileUrl);
      // Asociar la imagen al producto y guardar en la base de datos
      final GenericResponse<ProductResponseDto> productResponseDto = this.productService.findById(productId);
      productImage.setProduct(new Product());
      productImage = productImageService.save(productImage);*/
      return ResponseEntity.status(HttpStatus.OK).body(null);
    } catch (IOException | GeneralSecurityException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
