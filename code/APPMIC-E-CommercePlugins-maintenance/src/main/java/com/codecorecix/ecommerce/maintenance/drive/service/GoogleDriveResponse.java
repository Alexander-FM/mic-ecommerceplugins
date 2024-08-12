package com.codecorecix.ecommerce.maintenance.drive.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleDriveResponse {

  private Integer status;

  private String message;

  private String url;
}
