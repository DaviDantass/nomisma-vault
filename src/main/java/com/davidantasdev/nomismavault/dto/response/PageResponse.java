package com.davidantasdev.nomismavault.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

/** Contrato estável para coleções paginadas expostas pela API. */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {

  public static <T> PageResponse<T> from(Page<T> result) {
    return new PageResponse<>(
        result.getContent(),
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages(),
        result.isFirst(),
        result.isLast());
  }
}
