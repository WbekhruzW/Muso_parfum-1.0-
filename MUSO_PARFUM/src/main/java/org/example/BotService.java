package org.example;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotService extends TelegramLongPollingBot {
    private static final BotLogicService logicService = BotLogicService.getInstance();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            logicService.callbackHandler(update);
        } else if (update.hasMessage()) {
            logicService.messageHandler(update);
        }
    }

    @Override
    public String getBotUsername() {
        return "@musoparfumbot";
    }

    @Override
    public String getBotToken() {
        return "7244583362:AAElO9-K5nB39jmhlQ4PlPid-qQsXa7WK0k";
    }

    public Message executeMessages(SendMessage... messages) {
        Message lastSentMessage = null;

        for (SendMessage message : messages) {
            try {
                lastSentMessage = execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return lastSentMessage;
    }

    public void executeMessages(EditMessageText... messages) {
        for (EditMessageText message : messages) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void executeMessages(EditMessageReplyMarkup... messages) {
        for (EditMessageReplyMarkup message : messages) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static BotService botService;

    public static BotService getInstance() {
        if (botService == null) {
            botService = new BotService();
        }
        return botService;
    }

    @SneakyThrows
    public void executeMessages(ForwardMessage forwardMessage) {
        execute(forwardMessage);
    }@SneakyThrows
    public void executeMessages(SendContact forwardMessage) {
        execute(forwardMessage);
    }@SneakyThrows
    public void executeMessages(DeleteMessage forwardMessage) {
        execute(forwardMessage);
    }

    @SneakyThrows
    public void executeMessages(SendPhoto sendPhoto) {
        execute(sendPhoto);
    }

}
