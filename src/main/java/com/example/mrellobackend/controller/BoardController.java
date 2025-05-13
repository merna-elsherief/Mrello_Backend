package com.example.mrellobackend.controller;

import com.example.mrellobackend.dto.BoardCreateDto;
import com.example.mrellobackend.dto.BoardDto;
import com.example.mrellobackend.entity.Board;
import com.example.mrellobackend.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardDto> createBoard(
            @Valid @RequestBody BoardCreateDto boardDto,
            @RequestParam Long workspaceId
    ) {
        BoardDto createdBoard = boardService.createBoard(boardDto, workspaceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoardById(@PathVariable Long boardId) {
        BoardDto board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(board);
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<BoardDto>> getBoardsByWorkspace(
            @PathVariable Long workspaceId
    ) {
        List<BoardDto> boards = boardService.getAllBoardsInWorkspace(workspaceId);
        return ResponseEntity.ok(boards);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardCreateDto boardDto
    ) {
        BoardDto updatedBoard = boardService.updateBoard(boardId, boardDto);
        return ResponseEntity.ok(updatedBoard);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user/me")
    public ResponseEntity<List<BoardDto>> getMyBoards() {
        List<BoardDto> boards = boardService.getBoardsForCurrentUser();
        return ResponseEntity.ok(boards);
    }
}