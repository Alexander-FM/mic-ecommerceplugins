package com.codecorecix.ecommerce.maintenance.drive.service;

import lombok.Data;

@Data
public class GoogleDriveResponse {

  private Integer status;

  private String message;

  private String url;
}
