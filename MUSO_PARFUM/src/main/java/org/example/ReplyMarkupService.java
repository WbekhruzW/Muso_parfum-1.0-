package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import util.Utils;

import java.util.ArrayList;
import java.util.List;


public class ReplyMarkupService {
    public  ReplyKeyboardMarkup keyboardMaker(String[][] buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (String[] button : buttons) {
            KeyboardRow row = new KeyboardRow();
            for (String s : button) {
                if (s.equals(Utils.SEND_NUM)) {
                    KeyboardButton keyboardButton = new KeyboardButton(s);
                    keyboardButton.setRequestContact(true);
                    row.add(keyboardButton);
                } else if (s.equals(Utils.LOCATION)) {
                    KeyboardButton keyboardButton = new KeyboardButton(s);
                    keyboardButton.setRequestLocation(true);
                    row.add(keyboardButton);
                } else {
                    row.add(s);
                }
            }
            keyboardRows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
