package com.codecorecix.ecommerce.maintenance.attribute.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.codecorecix.ecommerce.event.entities.Attribute;
import com.codecorecix.ecommerce.exception.GenericException;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.request.AttributeRequestDto;
import com.codecorecix.ecommerce.maintenance.attribute.api.dto.response.AttributeResponseDto;
import com.codecorecix.ecommerce.maintenance.attribute.mapper.AttributeFieldsMapper;
import com.codecorecix.ecommerce.maintenance.attribute.repository.AttributeRepository;
import com.codecorecix.ecommerce.utils.GenericErrorMessage;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

  private final AttributeRepository repository;

  private final AttributeFieldsMapper mapper;

  @Override
  public GenericResponse<List<AttributeResponseDto>> getAllAttributes() {
    final List<Attribute> attrs = this.repository.findAll();
    if (!attrs.isEmpty()) {
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
          this.mapper.toDto(attrs));
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
          new ArrayList<>());
    }
  }

  @Override
  public GenericResponse<AttributeResponseDto> findById(final Integer id) {
    final Optional<Attribute> attr = this.repository.findById(id);
    return attr.map(value -> new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
        this.mapper.toDto(value))).orElseGet(() -> new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
        StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, "Attribute no existe"), null));
  }

  @Override
  public GenericResponse<AttributeResponseDto> save(final AttributeRequestDto dto) {
    try {
      // Verificar si ya existe por nombre para devolver conflicto manejable
      final Optional<Attribute> existing = this.repository.findByName(dto.getName());
      if (existing.isPresent()) {
        return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
            StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, GenericResponseConstants.CONFLICT), null);
      }

      final Attribute saved = this.repository.save(this.mapper.toEntity(dto));
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
          this.mapper.toDto(saved));
    } catch (final Exception e) {
      throw new GenericException(GenericErrorMessage.DATABASE_SAVE_ERROR);
    }
  }

  @Override
  public GenericResponse<AttributeResponseDto> update(final Integer id, final AttributeRequestDto dto) {
    final Optional<Attribute> attr = this.repository.findById(id);
    if (attr.isPresent()) {
      final Attribute toUpdate = this.mapper.toEntity(dto);
      toUpdate.setId(id);
      final Attribute saved = this.repository.save(toUpdate);
      return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION,
          this.mapper.toDto(saved));
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, "Attribute no existe"), null);
    }
  }

  @Override
  @Transactional
  public GenericResponse<AttributeResponseDto> deleteById(final Integer id) {
    final Optional<Attribute> attr = this.repository.findById(id);
    if (attr.isPresent()) {
      try {
        this.repository.deleteById(id);
        return new GenericResponse<>(GenericResponseConstants.RPTA_OK, GenericResponseConstants.CORRECT_OPERATION, null);
      } catch (final Exception e) {
        throw new GenericException(GenericErrorMessage.DATABASE_DELETE_ERROR);
      }
    } else {
      return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR,
          StringUtils.joinWith(GenericResponseConstants.DASH, GenericResponseConstants.INCORRECT_OPERATION, "Attribute no existe"), null);
    }
  }
}
