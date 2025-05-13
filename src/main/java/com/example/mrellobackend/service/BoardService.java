package com.example.mrellobackend.service;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.auth.user.UserRepository;
import com.example.mrellobackend.dto.BoardCreateDto;
import com.example.mrellobackend.dto.BoardDto;
import com.example.mrellobackend.entity.Board;
import com.example.mrellobackend.entity.Workspace;
import com.example.mrellobackend.exception.AccessDeniedException;
import com.example.mrellobackend.exception.ResourceNotFoundException;
import com.example.mrellobackend.repository.BoardRepository;
import com.example.mrellobackend.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
    private BoardDto convertToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .workspaceId(board.getWorkspace().getId())
                .createdAt(board.getCreatedAt())
                .build();
    }

    private Board convertToEntity(BoardCreateDto boardDto) {
        return Board.builder()
                .title(boardDto.getTitle())
                .description(boardDto.getDescription())
                .build();
    }

    @Transactional
    public BoardDto createBoard(BoardCreateDto boardDto, Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found with id: " + workspaceId));

        if (!workspace.getMembers().contains(getCurrentUser())) {
            throw new AccessDeniedException("You don't have permission to create boards in this workspace");
        }

        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setWorkspace(workspace);
        board.setCreatedAt(LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);
        return convertToDto(savedBoard);
    }

    public List<BoardDto> getAllBoardsInWorkspace(Long workspaceId) {
        return boardRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BoardDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));

        if (!board.getWorkspace().getMembers().contains(getCurrentUser())) {
            throw new AccessDeniedException("You don't have permission to access this board");
        }

        return convertToDto(board);
    }
    @Transactional
    public BoardDto updateBoard(Long boardId, BoardCreateDto boardDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));

        if (!board.getWorkspace().getMembers().contains(getCurrentUser())) {
            throw new AccessDeniedException("You don't have permission to update this board");
        }

        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());

        Board updatedBoard = boardRepository.save(board);
        return convertToDto(updatedBoard);
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));

        if (!board.getWorkspace().getOwner().equals(getCurrentUser())) {
            throw new AccessDeniedException("Only workspace owner can delete boards");
        }

        boardRepository.delete(board);
    }

    public List<BoardDto> getBoardsForCurrentUser() {
        List<Board> boards = boardRepository.findByWorkspaceOwnerOrWorkspaceMembersContaining(getCurrentUser());

        return boards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
