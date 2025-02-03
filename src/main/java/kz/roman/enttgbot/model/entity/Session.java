package kz.roman.enttgbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Table(name = "sessions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    private static final Locale RUS_LOCALE = new Locale.Builder().setLanguage("ru").setScript("Cyrl").build();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss", RUS_LOCALE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    @Column(nullable = false)
    private int correctAnswers;
    @Column(nullable = false)
    private int totalQuestions;
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime sessionDate = LocalDateTime.now();

    @Override
    public String toString() {
        int percent = (correctAnswers * 100) / totalQuestions;
        return "Тестирование от " + sessionDate.format(DATE_TIME_FORMATTER) + "\n" +
                "Тема - " + topic.getName() + "\n" +
                "Всего вопросов - " + totalQuestions + ". Правильных ответов - " + correctAnswers + ". Процент правильных ответов - " + percent + "%.\n";
    }
}
