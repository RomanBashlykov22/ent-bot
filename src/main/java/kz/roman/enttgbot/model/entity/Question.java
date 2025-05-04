package kz.roman.enttgbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "questions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    @ManyToOne
    @JoinColumn(name = "subtopic_id", nullable = false)
    private Subtopic subtopic;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option", columnDefinition = "TEXT")
    private List<String> options;
    @Column(nullable = false)
    private String correctOption;
    @Enumerated(EnumType.STRING)
    private QuestionDifficulty difficulty;

    public enum QuestionDifficulty {
        easy, medium, difficult
    }
}
