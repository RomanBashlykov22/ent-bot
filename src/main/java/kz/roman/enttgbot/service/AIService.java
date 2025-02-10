package kz.roman.enttgbot.service;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {
    private final AssistantsClient assistantsClient;
    private final Assistant assistant;


    public String askAi(String message) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        ThreadRun run = sendMessageToAi(message);
        List<ThreadMessage> answer = getMessageFromAi(run);
        for (ThreadMessage threadMessage : answer) {
            if (threadMessage.getRole() != MessageRole.ASSISTANT) continue;
            for (MessageContent messageContent : threadMessage.getContent()) {
                MessageTextContent messageTextContent = (MessageTextContent) messageContent;
                sb.append(messageTextContent.getText().getValue()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private ThreadRun sendMessageToAi(String message) {
        CreateAndRunThreadOptions createAndRunThreadOptions = new CreateAndRunThreadOptions(assistant.getId())
                .setThread(new AssistantThreadCreationOptions()
                        .setMessages(Arrays.asList(new ThreadMessageOptions(MessageRole.USER, message))));

        return assistantsClient.createThreadAndRun(createAndRunThreadOptions);
    }

    private List<ThreadMessage> getMessageFromAi(ThreadRun run) throws InterruptedException {
        do {
            run = assistantsClient.getRun(run.getThreadId(), run.getId());
            Thread.sleep(1000);
        } while (run.getStatus() == RunStatus.QUEUED || run.getStatus() == RunStatus.IN_PROGRESS);

        return assistantsClient.listMessages(run.getThreadId()).getData();
    }
}
