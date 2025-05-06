package kz.roman.enttgbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentErrors {
    private String mistakeTopic;
    private Integer count;
}
