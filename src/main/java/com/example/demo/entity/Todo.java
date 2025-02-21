package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 엔티티 클래스: Todo
 * <p>
 * 데이터베이스의 'todos' 테이블과 매핑되며, 각 할 일(ToDo) 항목을 표현합니다.
 */
@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직접 객체 생성 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더 패턴을 통한 객체 생성 유도
@FieldDefaults(level = AccessLevel.PRIVATE) // 모든 필드를 private로 설정
@EqualsAndHashCode(of = "id") // ID를 기준으로 동등성 비교
public class Todo {

    /**
     * Primary Key: 데이터베이스에서 자동 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * 할 일 제목: null이 될 수 없으며, 비어있는 문자열은 허용하지 않습니다.
     */
    @Column(nullable = false)
    String title;

    /**
     * 완료 여부: 기본값은 false이며, null 값은 허용되지 않습니다.
     */
    @Column(nullable = false)
    Boolean completed;

    /**
     * 할 일 생성일 (엔티티가 처음 저장될 때 자동으로 설정됨)
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    /**
     * 할 일 수정일 (엔티티가 업데이트될 때 자동으로 갱신됨)
     */
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    /**
     * 빌더 생성자입니다.
     *
     * @param title 할 일 제목
     * @param completed 완료 여부
     */
    @Builder
    public Todo(String title, Boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    /**
     * 할 일 완료 상태를 업데이트합니다.
     *
     * @param completed 새로운 완료 상태
     */
    public void updateCompleted(Boolean completed) {
        this.completed = completed;
    }

    /**
     * 할 일 제목을 업데이트합니다.
     *
     * @param title 새로운 제목 (앞뒤 공백 제거)
     */
    public void updateTitle(String title) {
        this.title = title.trim();
    }
}
