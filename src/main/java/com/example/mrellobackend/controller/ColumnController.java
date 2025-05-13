package com.example.mrellobackend.controller;

import com.example.mrellobackend.dto.ColumnCreateDto;
import com.example.mrellobackend.dto.ColumnDto;
import com.example.mrellobackend.dto.ColumnUpdateDto;
import com.example.mrellobackend.entity.Column;
import com.example.mrellobackend.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boards/{boardId}/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @PostMapping
    public ResponseEntity<ColumnDto> createColumn(@RequestBody ColumnCreateDto columnDto) {
        ColumnDto createdColumn = columnService.createColumn(columnDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdColumn);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<ColumnDto>> getColumnsByBoard(@PathVariable Long boardId) {
        List<ColumnDto> columns = columnService.getAllColumnsInBoard(boardId);
        return ResponseEntity.ok(columns);
    }

    @GetMapping("/{columnId}")
    public ResponseEntity<ColumnDto> getColumnById(
            @PathVariable Long columnId
    ) {
        ColumnDto column = columnService.getColumnById(columnId);
        return ResponseEntity.ok(column);
    }

    @PutMapping("/{columnId}")
    public ResponseEntity<ColumnDto> updateColumn(
            @PathVariable Long columnId,
            @RequestBody ColumnUpdateDto columnDto
    ) {
        ColumnDto updatedColumn = columnService.updateColumn(columnId, columnDto);
        return ResponseEntity.ok(updatedColumn);
    }

    @PutMapping("/{columnId}/reorder")
    public ResponseEntity<Void> reorderColumn(
            @PathVariable Long columnId,
            @RequestParam Long boardId,
            @RequestParam Integer newPosition
    ) {
        columnService.reorderColumns(boardId, columnId, newPosition);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable Long columnId) {
        columnService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<ColumnDto>> bulkUpdateColumns(
            @RequestBody List<ColumnUpdateDto> columnDtos
    ) {
        List<ColumnDto> updatedColumns = columnService.bulkUpdateColumns(columnDtos);
        return ResponseEntity.ok(updatedColumns);
    }
    @GetMapping("/board/{boardId}/count")
    public ResponseEntity<Integer> getColumnsCount(
            @PathVariable Long boardId
    ) {
        int count = columnService.getColumnsCountInBoard(boardId);
        return ResponseEntity.ok(count);
    }
    @PostMapping("/{columnId}/duplicate")
    public ResponseEntity<ColumnDto> duplicateColumn(
            @PathVariable Long columnId
    ) {
        ColumnDto duplicatedColumn = columnService.duplicateColumn(columnId);
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedColumn);
    }
}

