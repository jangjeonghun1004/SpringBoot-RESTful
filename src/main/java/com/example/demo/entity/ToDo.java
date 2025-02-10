package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * 엔티티 클래스: ToDo
 * <p>
 * 데이터베이스의 'todo' 테이블과 매핑되며, 각 할 일(ToDo) 항목을 표현합니다.
 * Lombok을 사용하여 보일러플레이트 코드를 줄였으며, JPA 어노테이션을 통해 ORM 매핑을 수행합니다.
 */
@Getter
@Entity
@Table(name = "todo")
@EqualsAndHashCode(of = "id") // id 값만을 기준으로 equals/hashCode를 생성합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙 상 기본 생성자는 protected로 선언합니다.
public class ToDo {

    /**
     * Primary Key: 데이터베이스에서 자동 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 할 일 제목: 비어있으면 안 됨.
     */
    @NotBlank(message = "Title must not be blank")
    @Column(nullable = false)
    private String title;

    /**
     * 완료 여부: null 값이 들어가지 않도록 @NotNull 적용하며, 기본값은 false입니다.
     */
    @NotNull(message = "Completed status must not be null")
    @Column(nullable = false)
    private Boolean completed = Boolean.FALSE;

    /**
     * 빌더 패턴을 활용한 생성자
     * @param title     할 일 제목 (null 또는 빈 값은 허용되지 않음)
     * @param completed 완료 여부 (null이면 false로 처리)
     */
    @Builder
    private ToDo(String title, Boolean completed) {
        this.title = title;
        // null 안전성을 위해 completed가 null이면 false로 설정
        this.completed = (completed != null) ? completed : Boolean.FALSE;
    }

    /**
     * 엔티티가 데이터베이스에 persist 되기 전에 호출되는 메서드.
     * completed 필드가 null인 경우 false로 초기화합니다.
     */
    @PrePersist
    private void prePersist() {
        if (this.completed == null) {
            this.completed = Boolean.FALSE;
        }
    }

    /**
     * 할 일을 완료 상태로 전환하는 비즈니스 메서드.
     * 추가적인 비즈니스 로직(예: 감사 로깅 등)을 이곳에 추가할 수 있습니다.
     *
     *  @param completed 완료 여부
     */
    public void updateCompleted(Boolean completed) {
        this.completed = completed;
    }

    /**
     * 할 일 제목을 업데이트하는 비즈니스 메서드.
     *
     * @param title 새로운 할 일 제목
     */
    public void updateTitle(String title) {
        // 추가적인 유효성 검증 또는 비즈니스 로직을 이곳에 추가할 수 있습니다.
        this.title = title;
    }
}
