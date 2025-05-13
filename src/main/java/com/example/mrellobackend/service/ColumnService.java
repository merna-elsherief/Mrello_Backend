package com.example.mrellobackend.service;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.auth.user.UserRepository;
import com.example.mrellobackend.dto.ColumnCreateDto;
import com.example.mrellobackend.dto.ColumnDto;
import com.example.mrellobackend.dto.ColumnUpdateDto;
import com.example.mrellobackend.entity.Board;
import com.example.mrellobackend.entity.Column;
import com.example.mrellobackend.entity.Task;
import com.example.mrellobackend.exception.AccessDeniedException;
import com.example.mrellobackend.exception.ResourceNotFoundException;
import com.example.mrellobackend.repository.BoardRepository;
import com.example.mrellobackend.repository.ColumnRepository;
import com.example.mrellobackend.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColumnService {
    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
    private ColumnDto convertToDto(Column column) {
        return ColumnDto.builder()
                .id(column.getId())
                .title(column.getTitle())
                .position(column.getPosition())
                .boardId(column.getBoard().getId())
                .build();
    }

    @Transactional
    public ColumnDto createColumn(ColumnCreateDto columnDto) {
        Board board = boardRepository.findById(columnDto.getBoardId())
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        // Verify user has access to the board's workspace
        if (!board.getWorkspace().getMembers().contains(getCurrentUser())) {
            throw new AccessDeniedException("You don't have permission to create columns in this board");
        }

        Column column = new Column();
        column.setTitle(columnDto.getTitle());
        column.setPosition(columnDto.getPosition());
        column.setBoard(board);

        Column savedColumn = columnRepository.save(column);
        return convertToDto(savedColumn);
    }

    public List<ColumnDto> getAllColumnsInBoard(Long boardId) {
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ColumnDto getColumnById(Long columnId) {
        User currentUser = getCurrentUser();
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        // Verify user has access to the board's workspace
        if (!column.getBoard().getWorkspace().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("You don't have permission to access this column");
        }

        return convertToDto(column);
    }
    @Transactional
    public ColumnDto updateColumn(Long columnId, ColumnUpdateDto columnDto) {
        User currentUser = getCurrentUser();
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        if (!column.getBoard().getWorkspace().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("You don't have permission to update this column");
        }

        column.setTitle(columnDto.getTitle());
        column.setPosition(columnDto.getPosition());

        Column updatedColumn = columnRepository.save(column);
        return convertToDto(updatedColumn);
    }

    @Transactional
    public void deleteColumn(Long columnId) {
        User currentUser = getCurrentUser();
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        // Verify user is workspace owner or board creator
        if (!column.getBoard().getWorkspace().getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Only workspace owner can delete columns");
        }

        columnRepository.delete(column);
    }
    @Transactional
    public void reorderColumns(Long boardId, Long columnId, Integer newPosition) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        if (!board.getWorkspace().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("No permission to reorder columns");
        }

        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        // Update positions of other columns
        if (newPosition < column.getPosition()) {
            columnRepository.incrementPositions(boardId, column.getPosition(), newPosition);
        } else {
            columnRepository.decrementPositions(boardId, column.getPosition(), newPosition);
        }

        column.setPosition(newPosition);
        columnRepository.save(column);
    }
    public List<ColumnDto> bulkUpdateColumns(List<ColumnUpdateDto> columnDtos) {
        return columnDtos.stream()
                .map(dto -> updateColumn(dto.getId(), dto))
                .collect(Collectors.toList());
    }
    public int getColumnsCountInBoard(Long boardId) {
        return columnRepository.countByBoardId(boardId);
    }
    public ColumnDto duplicateColumn(Long columnId) {
        Column sourceColumn = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        Column newColumn = new Column();
        newColumn.setTitle(sourceColumn.getTitle() + " (Copy)");
        newColumn.setPosition(sourceColumn.getPosition() + 1);
        newColumn.setBoard(sourceColumn.getBoard());

        Column savedColumn = columnRepository.save(newColumn);

        sourceColumn.getTasks().forEach(sourceTask -> {
            Task newTask = new Task();
            newTask.setTitle(sourceTask.getTitle());
            newTask.setDescription(sourceTask.getDescription());
            newTask.setPosition(sourceTask.getPosition());
            newTask.setColumn(savedColumn);
            taskRepository.save(newTask);
        });

        return convertToDto(savedColumn);
    }

}
