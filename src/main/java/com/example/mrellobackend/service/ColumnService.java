package com.example.mrellobackend.service;

import com.example.mrellobackend.dto.ColumnCreateDto;
import com.example.mrellobackend.dto.ColumnDto;
import com.example.mrellobackend.dto.ColumnUpdateDto;

import java.util.List;

public interface ColumnService {
    ColumnDto createColumn(ColumnCreateDto columnDto);

    List<ColumnDto> getAllColumnsInBoard(Long boardId);

    ColumnDto getColumnById(Long columnId);

    ColumnDto updateColumn(Long columnId, ColumnUpdateDto columnDto);

    void deleteColumn(Long columnId);

    void reorderColumns(Long boardId, Long columnId, Integer newPosition);

    List<ColumnDto> bulkUpdateColumns(List<ColumnUpdateDto> columnDtos);

    int getColumnsCountInBoard(Long boardId);

    ColumnDto duplicateColumn(Long columnId);
}
