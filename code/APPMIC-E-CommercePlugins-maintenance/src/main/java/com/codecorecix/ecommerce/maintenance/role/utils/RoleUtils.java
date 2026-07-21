package com.codecorecix.ecommerce.maintenance.role.utils;

import com.codecorecix.ecommerce.maintenance.role.dto.response.RoleResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleUtils {

  /**
   * Method use to build generic response error.
   *
   * @return GenericResponse of RoleResponseDto The generic response.
   */
  public static GenericResponse<RoleResponseDto> buildGenericResponseError() {
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, RoleConstants.NO_EXIST),
        null);
  }

  /**
   * Method use to build generic response success.
   *
   * @return GenericResponse of RoleResponseDto The generic response.
   */
  public static GenericResponse<RoleResponseDto> buildGenericResponseSuccess(final RoleResponseDto roleResponseDto) {
    return new GenericResponse<>(GenericResponseConstants.RPTA_OK,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.CORRECT_OPERATION), roleResponseDto);
  }
}
