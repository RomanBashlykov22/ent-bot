package kz.roman.enttgbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subtopics")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subtopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
}
