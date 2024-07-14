package com.codecorecix.ecommerce.category.api.service;

import java.util.List;

import com.codecorecix.ecommerce.category.api.dto.request.CategoryRequestDto;
import com.codecorecix.ecommerce.category.api.dto.response.CategoryResponseDto;
import com.codecorecix.ecommerce.category.api.mapper.CategoriaFieldsMapper;
import com.codecorecix.ecommerce.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceImpl implements CategoriaService {

  private final CategoryRepository repository;

  private final CategoriaFieldsMapper mapper;

  public CategoriaServiceImpl(CategoryRepository repository, CategoriaFieldsMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public List<CategoryResponseDto> categoryList(CategoryRequestDto categoryRequestDto) {
    return this.mapper.toDto(this.repository.findAll());
  }
}
