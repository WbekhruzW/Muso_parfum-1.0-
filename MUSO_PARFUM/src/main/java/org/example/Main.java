package org.example;


import lombok.SneakyThrows;
import org.example.BotService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {

//        =================================
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new BotService());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}