package com.codecorecix.ecommerce.maintenance.drive.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleDriveResponse {

  private String message;

  private String url;
}
