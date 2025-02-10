package kz.roman.enttgbot.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.roman.enttgbot.model.dto.TestSession;
import kz.roman.enttgbot.model.dto.UserAnswer;
import kz.roman.enttgbot.model.entity.*;
import kz.roman.enttgbot.service.*;
import kz.roman.enttgbot.util.CallbackStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
@Slf4j
public class EntBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final TopicService topicService;
    private final SubtopicService subtopicService;
    private final QuestionService questionService;
    private final SessionService sessionService;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    private final Map<String, TestSession> testSessions = new HashMap<>();

    private static final String START = "/start";
    private static final String TEST = "/test";
    private static final String RESULTS = "/results";
    private static final String ASK = "/ask";

    private static final String TOPIC_ID_CALLBACK_DATA = "Topic ID: ";
    private static final String SUBTOPIC_ID_CALLBACK_DATA = "Subtopic ID: ";
    private static final String ANSWER = "Answer_";

    @Value("${tg.bot.name}")
    private String botUsername;
    private DialogMode dialogMode;
    private Integer messageId = null;
    private List<UserAnswer> userAnswers = new ArrayList<>();

    public EntBot(
            @Value("${tg.bot.token}") String botToken,
            UserService userService,
            TopicService topicService,
            SubtopicService subtopicService,
            QuestionService questionService,
            SessionService sessionService,
            AIService aiService,
            ObjectMapper objectMapper
    ) {
        super(botToken);
        this.userService = userService;
        this.topicService = topicService;
        this.subtopicService = subtopicService;
        this.questionService = questionService;
        this.sessionService = sessionService;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand(START, "Главное меню"));
        botCommands.add(new BotCommand(TEST, "Начать тестирование"));
        botCommands.add(new BotCommand(RESULTS, "Посмотреть историю попыток тестирований"));
        botCommands.add(new BotCommand(ASK, "Задать вопрос искусственному интеллекту"));
        try {
            execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка добавления команд в меню", e);
        }
        dialogMode = DialogMode.MAIN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String message = update.getMessage().getText();
                String chatId = update.getMessage().getChatId().toString();

                if (message.equalsIgnoreCase(START)) {
                    handleStartCommand(chatId);
                }
                if (message.equalsIgnoreCase(TEST)) {
                    getTopics(chatId);
                }
                if (message.equalsIgnoreCase(RESULTS)) {
                    getUserResults(chatId);
                }
                if (message.equalsIgnoreCase(ASK) || dialogMode.equals(DialogMode.AI_ASK)) {
                    aiConversation(chatId, message);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String callback = CallbackStorage.retrieve(update.getCallbackQuery().getData());

            if (callback.startsWith(TOPIC_ID_CALLBACK_DATA) && dialogMode.equals(DialogMode.TEST)) {
                long topicId = Long.parseLong(callback.split(":")[1].trim());
                getSubtopics(chatId, topicId);
            }
            if (callback.startsWith(SUBTOPIC_ID_CALLBACK_DATA) && dialogMode.equals(DialogMode.TEST)) {
                List<Question> questions;
                if (callback.contains("null")) {
                    long topicId = Long.parseLong(callback.split(":")[2].trim());
                    questions = questionService.getQuestionsByTopic(topicId);
                } else {
                    long subtopicId = Long.parseLong(callback.split(":")[1].trim());
                    questions = questionService.getQuestionsBySubtopic(subtopicId);
                }
                startTest(chatId, questions);
            }
            if (callback.startsWith(ANSWER) && dialogMode.equals(DialogMode.TEST)) {
                int questionIndex = Integer.parseInt(callback.split("_")[1]);
                String selectedAnswer = callback.split("_")[2];
                handleAnswer(chatId, questionIndex, selectedAnswer);
            }
        }
    }

    private void handleStartCommand(String chatId) {
        userService.save(chatId);

        String welcomeMessage = """
                Привет! Я бот для подготовки к ЕНТ. Вот что я умею:
                                
                /start - Начать работу с ботом.
                /test - Начать тестирование по теме.
                /results - Посмотреть свои результаты.
                /ask - Задать вопрос искусственному интеллекту.
                /help - Получить помощь.
                                
                Выберите команды чтобы продолжить.
                """;

        dialogMode = DialogMode.MAIN;
        sendTextMessage(new SendMessage(chatId, welcomeMessage));
    }

    private void getTopics(String chatId) {
        List<Topic> topics = topicService.findAll();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        topics.forEach(t -> {
            var button = new InlineKeyboardButton();
            button.setText(t.getName());
            button.setCallbackData(CallbackStorage.store(TOPIC_ID_CALLBACK_DATA + t.getId()));
            rows.add(List.of(button));
        });

        inlineKeyboardMarkup.setKeyboard(rows);

        String messageText = """
                Выберите тему из списка, представленного ниже.
                """;

        dialogMode = DialogMode.TEST;
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        Message sentMessage = sendTextMessage(message);
        messageId = sentMessage.getMessageId();
    }

    private void getSubtopics(String chatId, long topicId) {
        List<Subtopic> subtopics = subtopicService.findByTopicId(topicId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        var buttonWithoutTopic = new InlineKeyboardButton();
        buttonWithoutTopic.setText("Пройти тест по всем подтемам");
        buttonWithoutTopic.setCallbackData(CallbackStorage.store(SUBTOPIC_ID_CALLBACK_DATA + "null. Topic ID: " + topicId));
        rows.add(List.of(buttonWithoutTopic));
        subtopics.forEach(s -> {
            var button = new InlineKeyboardButton();
            button.setText(s.getName());
            button.setCallbackData(CallbackStorage.store(SUBTOPIC_ID_CALLBACK_DATA + s.getId()));
            rows.add(List.of(button));
        });

        inlineKeyboardMarkup.setKeyboard(rows);

        String messageText = """
                Выберите подтему из списка, представленного ниже.
                """;

        dialogMode = DialogMode.TEST;
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(messageText)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        editTextMessage(editMessageText);
    }

    private void startTest(String chatId, List<Question> questions) {
        Collections.shuffle(questions);
        List<Question> testQuestions = questions.stream().limit(10).toList(); //TODO Изменить количество

        TestSession testSession = new TestSession(testQuestions);
        testSessions.put(chatId, testSession);
        userAnswers = new ArrayList<>();

        sendQuestion(chatId, testSession, 0);
    }

    private void sendQuestion(String chatId, TestSession testSession, int questionIndex) {
        Question question = testSession.getQuestions().get(questionIndex);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (String option : question.getOptions()) {
            buttons.add(List.of(InlineKeyboardButton.builder()
                    .text(option)
                    .callbackData(CallbackStorage.store(ANSWER + questionIndex + "_" + option))
                    .build()));
        }

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(question.getQuestionText())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
        editTextMessage(editMessageText);
    }

    private void handleAnswer(String chatId, int questionIndex, String selectedAnswer) {
        TestSession testSession = testSessions.get(chatId);
        if (testSession == null) {
            sendTextMessage(SendMessage.builder().chatId(chatId).text("Сессия теста не найдена. Начните заново").build());
            return;
        }
        testSession.getAnswers().put(questionIndex, selectedAnswer);
        userAnswers.add(UserAnswer.builder()
                .question(testSession.getQuestions().get(questionIndex).getQuestionText())
                .options(testSession.getQuestions().get(questionIndex).getOptions())
                .correctOption(testSession.getQuestions().get(questionIndex).getCorrectOption())
                .selectedOption(selectedAnswer).build());

        if (questionIndex + 1 < testSession.getQuestions().size()) {
            sendQuestion(chatId, testSession, questionIndex + 1);
        } else {
            endTest(chatId, testSession);
        }
    }

    private void endTest(String chatId, TestSession testSession) {
        long correctAnswers = testSession.getQuestions().stream()
                .filter(q -> q.getCorrectOption()
                        .equals(testSession.getAnswers().get(testSession.getQuestions().indexOf(q))))
                .count();

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("Тест завершен! Ваш результат: " + correctAnswers + " из " + testSession.getQuestions().size())
                .build();
        editTextMessage(editMessageText);

        Session session = Session.builder()
                .user(userService.findById(chatId).orElseThrow())
                .topic(testSession.getQuestions().get(0).getTopic())
                .correctAnswers((int) correctAnswers)
                .totalQuestions(testSession.getQuestions().size())
                .build();
        sessionService.save(session);
        dialogMode = DialogMode.MAIN;
        testSessions.remove(chatId);
        messageId = null;

        aiCheckUserAnswer(chatId, userAnswers);
    }

    private void getUserResults(String chatId) {
        StringBuilder sb = new StringBuilder();
        List<Session> results = sessionService.getUserResults(chatId);
        if (results.isEmpty()) {
            sb.append("Ваша история тестирований пуста. Пройдите тест.");
        } else {
            for (Session s : results) {
                sb.append(s.toString()).append("\n");
            }
        }
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(sb.toString().trim())
                .build();
        sendTextMessage(message);
    }

    private void aiCheckUserAnswer(String chatId, List<UserAnswer> userAnswers) {
        try {
            String jsonUserAnswer = "Проверка:\n" + objectMapper.writeValueAsString(userAnswers);
            String answer = aiService.askAi(jsonUserAnswer);
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(answer)
                    .build();
            sendTextMessage(message);
        } catch (InterruptedException | JsonProcessingException e) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Не удалось получить ответ от ИИ. Повторите попытку")
                    .build();
            sendTextMessage(message);
        }
    }

    private void aiConversation(String chatId, String userMessage) {
        dialogMode = DialogMode.AI_ASK;

        try {
            String answer = userMessage.equalsIgnoreCase(ASK) ? aiService.askAi("Привет!") : aiService.askAi(userMessage);
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(answer)
                    .build();
            sendTextMessage(message);
        } catch (InterruptedException e) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Не удалось получить ответ от ИИ. Повторите попытку")
                    .build();
            sendTextMessage(message);
        }
    }

    private Message sendTextMessage(SendMessage message) {
        try {
            return execute(message);
        } catch (TelegramApiException e) {
            dialogMode = DialogMode.MAIN;
            log.error("Ошибка отправки сообщения", e);
        }
        return null;
    }

    private void editTextMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            dialogMode = DialogMode.MAIN;
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
