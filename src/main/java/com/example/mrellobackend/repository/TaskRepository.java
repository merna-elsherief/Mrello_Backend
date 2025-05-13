package com.example.mrellobackend.repository;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByColumnIdOrderByPositionAsc(Long columnId);
    Integer countByColumnId(Long columnId);
    List<Task> findByColumnId(Long columnId);
    List<Task> findByAssignedUserId(Long userId);
    @Query("SELECT t FROM Task t JOIN t.column c JOIN c.board b WHERE b.workspace.id = :workspaceId")
    List<Task> findAllTasksByWorkspaceId(@Param("workspaceId") Long workspaceId);
    @Modifying
    @Query("UPDATE Task t SET t.position = t.position + 1 " +
            "WHERE t.column.id = :columnId AND t.position >= :newPosition")
    void incrementPositions(@Param("columnId") Long columnId,
                            @Param("newPosition") Integer newPosition);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position - 1 " +
            "WHERE t.column.id = :columnId AND t.position > :position")
    void decrementPositions(@Param("columnId") Long columnId,
                            @Param("position") Integer position);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position - 1 " +
            "WHERE t.column.id = :columnId AND t.position BETWEEN :start AND :end")
    void decrementPositionsInRange(@Param("columnId") Long columnId,
                                   @Param("start") Integer start,
                                   @Param("end") Integer end);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position + 1 " +
            "WHERE t.column.id = :columnId AND t.position BETWEEN :start AND :end")
    void incrementPositionsInRange(@Param("columnId") Long columnId,
                                   @Param("start") Integer start,
                                   @Param("end") Integer end);

    List<Task> findByAssignedUserOrderByDueDateAsc(User assignedUser);

    @Query("SELECT t FROM Task t " +
            "WHERE t.assignedUser = :user " +
            "AND t.dueDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.dueDate ASC")
    List<Task> findByAssignedUserAndDueDateBetweenOrderByDueDateAsc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
