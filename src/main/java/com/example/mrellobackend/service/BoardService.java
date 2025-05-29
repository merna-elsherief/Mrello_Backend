package com.example.mrellobackend.service;

import com.example.mrellobackend.dto.BoardCreateDto;
import com.example.mrellobackend.dto.BoardDto;

import java.util.List;

public interface BoardService {
    BoardDto createBoard(BoardCreateDto boardDto, Long workspaceId);

    List<BoardDto> getAllBoardsInWorkspace(Long workspaceId);

    BoardDto getBoardById(Long boardId);

    BoardDto updateBoard(Long boardId, BoardCreateDto boardDto);

    void deleteBoard(Long boardId);

    List<BoardDto> getBoardsForCurrentUser();
}
