package com.example.mrellobackend.repository;

import com.example.mrellobackend.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<Column, Long> {
    List<Column> findByBoardIdOrderByPositionAsc(Long boardId);

    List<Column> findByBoardId(Long boardId);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position + 1 WHERE c.board.id = :boardId AND c.position >= :newPosition AND c.position < :oldPosition")
    void incrementPositions(@Param("boardId") Long boardId,
                            @Param("oldPosition") Integer oldPosition,
                            @Param("newPosition") Integer newPosition);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position - 1 WHERE c.board.id = :boardId AND c.position <= :newPosition AND c.position > :oldPosition")
    void decrementPositions(@Param("boardId") Long boardId,
                            @Param("oldPosition") Integer oldPosition,
                            @Param("newPosition") Integer newPosition);

    int countByBoardId(Long boardId);
}
