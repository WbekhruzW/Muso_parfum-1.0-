package org.example;

import Connection_Db.TestConnection;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import payload.InlineString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static util.Utils.*;

public class BotLogicService {
    private SendMessage sendMessage = new SendMessage();
    BotService botService = BotService.getInstance();
    private final ReplyMarkupService replyService = new ReplyMarkupService();
    private final InlineMarkupService inlineService = new InlineMarkupService();
    public Long userID;
    public Long neededID;
    public Integer state = userState.get(userID);
    static Map<Long, Integer> userState = new HashMap<>();
    static Map<Long, Map<String,String>> userBasket = new HashMap<>();
    static Map<String, String> parfums = new HashMap<>();
    static Map<String,Map<String,String>>columnsOfParfums = new HashMap();
    String userNum = "";
    String firstName = "";
    private Set<Contact> users = new HashSet<>();
    private final Long adminId = 1430381201l;
    private String currentPhoto = "";
    private String currentPhotoText = "";
    private String currentQ = "";
    static Map<Long,Contact> userContact = new HashMap<>();
    static ArrayList<String>history = new ArrayList<>();
    static SendContact sendContact = new SendContact();
    static TestConnection testConnection = TestConnection.getInstance();
    @SneakyThrows
    public void messageHandler(Update update) {
        userBasket.putIfAbsent(userID, new HashMap<>());
        userID = update.getMessage().getChatId();
        if (update.getMessage().hasPhoto() && Objects.equals(userID, adminId)){
            state = 888;
            currentPhoto = handlePhoto(update);
            sendMessage.setText("tegiga nima yozilishini kiriting: ");
            sendMessage.setChatId(userID);
            sendMessage.setReplyMarkup(null);
            botService.executeMessages(sendMessage);
            return;
        }if (update.getMessage().hasSticker()){
            sendMessage.setText("Zor stiker ekan! 😊");
            sendMessage.setChatId(userID);
            botService.executeMessages(sendMessage);
            return;
        }
        if (update.getMessage().hasLocation() && state == 9525807){
            if (userContact.isEmpty() || !userContact.containsKey(userID)){
                sendMessage.setChatId(userID);
                sendMessage.setText("Uzur, afsuski sizni systemadan topa olmadik!\n" +
                        "katta ehtimol bilan botimizni qayta ishga tushurganimiz uchun sizning ma'lumotlaringizni yoqotik! 😥😥\n" +
                        "shuning uchun boshqattan regestratsiyadan otishingizni sorab qolaman!\n" +
                        "/start 👈 boshing");
                sendMessage.setReplyMarkup(null);
                botService.executeMessages(sendMessage);
                return;
            }
            sendContact.setChatId(adminId);
            sendContact.setFirstName(userContact.get(userID).getFirstName());
            sendContact.setPhoneNumber(userContact.get(userID).getPhoneNumber());
            sendContact.setLastName(userContact.get(userID).getLastName());
            sendContact.setVCard(userContact.get(userID).getVCard());
            int a = genOrdrNum();
            ForwardMessage forwardMessage = new ForwardMessage();
            forwardMessage.setChatId(adminId);
            forwardMessage.setFromChatId(userID);
            forwardMessage.setMessageId(update.getMessage().getMessageId());
            botService.executeMessages(forwardMessage);
            LocalDateTime localDateTime = LocalDateTime.now();
            String answer = "Sizning zakazingiz qabul qilindi!✅\n" +
                    "\n\nKlient nomeri va ismi: " + userContact.get(userID).getFirstName() + ", " + userContact.get(userID).getPhoneNumber() + " Zakazingizni nomeri: " + a + " Agar siz bilan 1 soat ichida ulanishmasa \n +998 95 108 90 90 shu nomer ga murajat qiling!" +
                    "\nZakazingiz uchun kottakon raxmat!😊";
            history.add(answer + "\n\n" + localDateTime.format(DateTimeFormatter.ofPattern("Kun: yyyy-MM-dd  HH:mm")));
            sendMessage.setChatId(userID);
            sendMessage.setParseMode("Markdown");
            sendMessage.setText(answer);
            sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
            botService.executeMessages(sendMessage);
            userBasket.get(userID).forEach((k,v) -> {
                sendPhoto(v,k,adminId);
            });
            sendMessage.setText("Yengi zakaz keldi! \nZakaz Nomeri:" + a + "\nKlientni nomeri va ismi: " + userContact.get(userID).getPhoneNumber() + ", " + userContact.get(userID).getFirstName());
            sendMessage.setChatId(adminId);
            sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
            botService.executeMessages(sendContact);
            botService.executeMessages(sendMessage);
            return;
        }
        if (update.getMessage().hasContact()) {
            if (checkIsThere("users","chat_id",String.valueOf(update.getMessage().getChatId()))){
                Statement statement = testConnection.getStatement();
                String query = String.format("insert into users(chat_id,name,phone_number) values('%s','%s','%s')",
                        update.getMessage().getChatId(),
                        update.getMessage().getContact().getFirstName(),
                        update.getMessage().getContact().getPhoneNumber());
                statement.execute(query);
            }
            userContact.put(userID,update.getMessage().getContact());
            sendMessage.setChatId(adminId);
            sendMessage.setText("Yengi User Sistemaga kirdi! :" + update.getMessage().getContact().getFirstName() + ", " + update.getMessage().getContact().getPhoneNumber());
            users.add(update.getMessage().getContact());
            sendMessage.setText("Assalomu alaykum xurmatli mijos!😊 Bu Muso Parfum boti bu yerda siz atirlar zakaz qila olasiz!");
            sendMessage.setChatId(userID);
            if (Objects.equals(userID, adminId)) {
                sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
            }else {
                sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
            }
            botService.executeMessages(sendMessage);
            userNum = update.getMessage().getContact().getPhoneNumber();
            firstName = update.getMessage().getContact().getFirstName();
            System.out.println("Received contact: " + firstName + " - " + userNum);
            return;
        }
        switch (update.getMessage().getText()){
            case "/start"->{
                state = 0;
                sendMessage.setText("Nomeringizni tashlang: ");
                sendMessage.setChatId(userID);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(req_num));
                botService.executeMessages(sendMessage);
            }case "/help"->{
                state = 0;
                sendMessage.setText(CONTACT + " - Tugmani bosangiz siz admin bilan ulana olasiz! va har qanday sovolizga javob tapa ololisiz!\n" +
                        HISTORY + " - Tugmani bosangiz siz oldingi qilgan zakazlaringizni royxatini olasiz!\n" +
                        PARFUMS + " - Tugmani bosangiz siz atirlar royxatini ololisiz!\n" +
                        BASKET + " - Tugmani bosangiz siz ozingizni sotib olmoxchi bogan atirlarizni royxatini korasiz!");
                sendMessage.setChatId(userID);
                if (Objects.equals(userID, adminId)) {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                }else {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                }
                botService.executeMessages(sendMessage);
            }
            case CONTACT -> {
                sendMessage.setText("Nima sovollariz bor?\uD83E\uDD14");
                sendMessage.setChatId(userID);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(just_back));
                botService.executeMessages(sendMessage);
                state = 1111222;
                return;
            }case HOW_MUCH_USERS -> {
                int a = 0;
                int u = 0;
                for (Contact c : users) {
                    if (Objects.equals(c.getUserId(), adminId)){
                        sendContact.setVCard(c.getVCard());
                        sendContact.setLastName(c.getLastName());
                        sendContact.setFirstName(c.getFirstName());
                        sendContact.setChatId(adminId);
                        sendContact.setPhoneNumber(c.getPhoneNumber());
                        botService.executeMessages(sendContact);
                        a++;
                        sendMessage.setText("Mana hamma adminlar👆 \n" +"Jami: "+ a);
                        sendMessage.setChatId(adminId);
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                        botService.executeMessages(sendMessage);
                    }
                    if (!Objects.equals(c.getUserId(), adminId)){
                        sendContact.setVCard(c.getVCard());
                        sendContact.setLastName(c.getLastName());
                        sendContact.setFirstName(c.getFirstName());
                        sendContact.setChatId(adminId);
                        sendContact.setPhoneNumber(c.getPhoneNumber());
                        botService.executeMessages(sendContact);
                        u++;
                        sendMessage.setText("Mana hamma userlar👆 \n" +"Jami: "+ u);
                        sendMessage.setChatId(adminId);
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                        botService.executeMessages(sendMessage);
                        return;
                    }
                }
            }
            case HISTORY -> {
                if (history.isEmpty()){
                    sendMessage.setText("Siz hali zakaz qilmadingiz!");
                    sendMessage.setChatId(userID);
                    if (Objects.equals(userID, adminId)) {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                    }else {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                    }
                    botService.executeMessages(sendMessage);
                    return;
                }
                for (String s :
                        history) {
                    sendMessage.setText(s);
                    sendMessage.setChatId(userID);
                    sendMessage.setReplyMarkup(null);
                    botService.executeMessages(sendMessage);
                }
                sendMessage.setText("Shu sizning zakazlar tarixingiz👆");
                sendMessage.setChatId(userID);
                if (Objects.equals(userID, adminId)) {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                }else {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                }
                botService.executeMessages(sendMessage);
            }
            case Add_Column -> {
                if (!Objects.equals(userID, adminId)){
                    sendMessage.setText("Urunmang babir admin bololmaysiz \uD83E\uDD17");
                    return;
                }
                sendMessage.setText("Kategoriyani nomini kiriting");
                sendMessage.setChatId(userID);
                sendMessage.setReplyMarkup(null);
                botService.executeMessages(sendMessage);
                state = 1111989;
                return;
            }
            case PARFUMS -> {
                state = 1;
                if (columnsOfParfums.isEmpty()){
                    sendMessage.setText("uzur lekn hali bizda atir yoq!");
                    sendMessage.setChatId(userID);
                    if (Objects.equals(userID, adminId)) {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                    }else {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                    }
                    botService.executeMessages(sendMessage);
                    return;
                }
                sendMessage.setText("Kategoriyalar");
                sendMessage.setChatId(userID);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(createSingleColumnKeyboardOfColumns(columnsOfParfums)));
                botService.executeMessages(sendMessage);
            }
            case BACK -> {
                if (state == 1){
                    state = 0;
                    sendMessage.setChatId(userID);
                    sendMessage.setText("Bosh menu");
                    if (Objects.equals(userID, adminId)) {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                    }else {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                    }
                    botService.executeMessages(sendMessage);
                }if (state == 2){
                    state = 1;
                    sendMessage.setChatId(userID);
                    sendMessage.setText("Kategoriyalar");
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(createSingleColumnKeyboardOfColumns(columnsOfParfums)));
                    botService.executeMessages(sendMessage);
                }if (state == 7 || state == 1111989 || state == 19){
                    state = 0;
                    sendMessage.setChatId(userID);
                    sendMessage.setText("Bosh menu");
                    if (Objects.equals(userID, adminId)) {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                    }else {
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                    }
                    botService.executeMessages(sendMessage);
                }
            }
            case ORDER_IT -> {
                state = 9525807;
                sendMessage.setText("Qatga oborib berish kerak?");
                sendMessage.setChatId(userID);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(loc));
                botService.executeMessages(sendMessage);
                return;
            }
            case BASKET -> {
                if (userBasket.get(userID).isEmpty()){
                    sendMessage.setChatId(userID);
                    sendMessage.setText("Sizda hali heshnima yoq!");
                    botService.executeMessages(sendMessage);
                    state = 0;
                    return;
                }
                state = 7;
                sendMessage.setText("Savat: ");
                sendMessage.setChatId(userID);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(order));
                botService.executeMessages(sendMessage);
                userBasket.get(userID).forEach((k,v) -> {
                    sendPhoto(v,X,"delete" + k);
                });
            }
            default ->  {
                String text = update.getMessage().getText();
                if (state == 888 && columnsOfParfums.containsKey(text)){
                    if (columnsOfParfums.containsKey(currentPhotoText) && columnsOfParfums.get(text).containsKey(currentPhotoText)){
                        sendMessage.setText("Uzur lekn Atir qaytarilishi mumun emas va atir nomi bilan \nKategoriya nomi bir xil bolishi mutlaqo munkun EMAS!");
                        sendMessage.setChatId(userID);
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                        botService.executeMessages(sendMessage);
                        return;
                    }
                    columnsOfParfums.get(text).put(currentPhotoText,currentPhoto);
                    sendMessage.setText("Atir muvofaqiyatli qoshildi!");
                    sendMessage.setChatId(userID);
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                    botService.executeMessages(sendMessage);
                    state = 0;
                    return;
                }
                columnsOfParfums.forEach((k,v)-> {
                    if (Objects.equals(k,text) && state != 888){
                        if (state == 1111989){
                            sendMessage.setChatId(adminId);
                            sendMessage.setText("Ikkta bir xil nomli kategoriya qoshish munkum ekams");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                            botService.executeMessages(sendMessage);
                            state = 0;
                            return;
                        }
                        state = 2;
                        if (v.isEmpty()){
                            state = 1;
                            sendMessage.setChatId(userID);
                            sendMessage.setText("Uzur lekn hali bu kategoriyada atir yoq!");
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(createSingleColumnKeyboardOfColumns(columnsOfParfums)));
                            botService.executeMessages(sendMessage);
                            return;
                        }
                        sendMessage.setChatId(userID);
                        sendMessage.setText("Atir tanlang");
                        sendMessage.setReplyMarkup(replyService.keyboardMaker(createSingleColumnKeyboard(columnsOfParfums.get(text))));
                        botService.executeMessages(sendMessage);
                        return;
                    }
                });
                parfums.forEach((k,v)-> {
                    if (Objects.equals(k,text)) {
                        sendPhoto(v,ADD_TO_BASKET,k,k);
                        return;
                    }
                });

            }
        }

        if(state == 888){
            currentPhotoText = update.getMessage().getText();
            parfums.put(currentPhotoText,currentPhoto);
            sendMessage.setChatId(userID);
            if (columnsOfParfums.isEmpty()){
                sendMessage.setText("Atir qoshish uchun kategoriya qoshin u siz atoi qosha olmaysiz!!!!!");
                botService.executeMessages(sendMessage);
                return;
            }
            sendMessage.setText("qaysi kategoriyaga qoshasiz?");
            sendMessage.setReplyMarkup(replyService.keyboardMaker(createSingleColumnKeyboardOfColumns(columnsOfParfums)));
            botService.executeMessages(sendMessage);
        }if(state == 1111989){
            String text = update.getMessage().getText();
            columnsOfParfums.put(text,new HashMap<>());
            sendMessage.setText("Kategoriya muvafaqiyatli qoshildi!");
            sendMessage.setChatId(userID);
            sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
            botService.executeMessages(sendMessage);
            state = 0;
        }if(state == 11114096){
            String text = update.getMessage().getText();
            sendMessage.setText("Shu savolizga: " + currentQ +"\nAdmin shunaqa javob beridi! : " + text);
            sendMessage.setChatId(neededID);
            botService.executeMessages(sendMessage);
            sendMessage.setText("Javobingiz yetkazib berildi!");
            sendMessage.setChatId(adminId);
            botService.executeMessages(sendMessage);
            state = 0;
            return;
        }if(state == 1111222){
            if (Objects.equals(update.getMessage().getText(), BACK)){
                state = 0;
                sendMessage.setChatId(userID);
                sendMessage.setText("Bosh menu");
                if (Objects.equals(userID, adminId)) {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                }else {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                }
                botService.executeMessages(sendMessage);
                return;
            }
            currentQ = update.getMessage().getText();
            sendMessage.setText("Adminga jonatib qoydik azginadan song javob qaytadi)");
            sendMessage.setChatId(userID);
            sendMessage.setReplyMarkup(replyService.keyboardMaker(just_back));
            botService.executeMessages(sendMessage);
            sendMessage.setChatId(adminId);
            sendMessage.setText(currentQ);
            sendMessage.setReplyMarkup(inlineService.inlineMarkup(new InlineString[][] {
                    {new InlineString("Jovob qaytarish",REPLY + userID)}
            }));
            state = 19;
            botService.executeMessages(sendMessage);
        }
    }

    private int genOrdrNum() {
        Random random = new Random();
        return random.nextInt(1000, 9999);
    }

    private String[][] createSingleColumnKeyboard(Map<String, String> options) {
        int rows = options.size() + 1;
        String[][] keyboard = new String[rows][1];
        int i = 0;
        for (String key : options.keySet()) {
            keyboard[i][0] = key;
            i++;
        }
        keyboard[i][0] = BACK;
        return keyboard;
    }private String[][] createSingleColumnKeyboardOfColumns(Map<String, Map<String,String>> options) {
        int rows = options.size() + 1;
        String[][] keyboard = new String[rows][1];
        int i = 0;
        for (String key : options.keySet()) {
            keyboard[i][0] = key;
            i++;
        }
        keyboard[i][0] = BACK;
        return keyboard;
    }

    @SneakyThrows
    private void sendPhoto(String fileID,String userShow,String callback,String text){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(userID);
        sendPhoto.setCaption(text);
        sendPhoto.setPhoto(new InputFile(fileID));
        sendPhoto.setReplyMarkup(inlineService.inlineMarkup(new InlineString[][]{
                {new InlineString(userShow,callback)}
        }));
        botService.executeMessages(sendPhoto);
    }@SneakyThrows
    private void sendPhoto(String fileID,String text,Long id){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(id);
        sendPhoto.setCaption(text);
        sendPhoto.setPhoto(new InputFile(fileID));
        botService.executeMessages(sendPhoto);
    }@SneakyThrows
    private void sendPhoto(String fileID,String userShow,String callback){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(userID);
        sendPhoto.setPhoto(new InputFile(fileID));
        sendPhoto.setReplyMarkup(inlineService.inlineMarkup(new InlineString[][]{
                {new InlineString(userShow,callback)}
        }));
        botService.executeMessages(sendPhoto);
    }
    private String handlePhoto(Update update) {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        String fileId = photos.get(photos.size() - 1).getFileId();
        System.out.println("Received photo with file ID: " + fileId);
        return fileId;
    }
    @SneakyThrows
    public void callbackHandler(Update update) {
        String a = update.getCallbackQuery().getData();
        String b = a.substring(6);
        if (a.startsWith("delete") && parfums.containsKey(b)){
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            deleteMessage.setChatId(userID);
            userBasket.get(userID).remove(b);
            sendMessage.setText(b + "nomli atir muvafaqiyatli ochirildi!");
            sendMessage.setChatId(userID);
            if (userBasket.get(userID).isEmpty()){
                if (Objects.equals(userID, adminId)) {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
                }else {
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
                }
            }
            botService.executeMessages(deleteMessage);
            botService.executeMessages(sendMessage);
        }else if (a.startsWith(REPLY)){
            neededID = Long.valueOf(a.substring(5));
            sendMessage.setText("Javobingizni yozing: ");
            sendMessage.setChatId(adminId);
            sendMessage.setReplyMarkup(null);
            botService.executeMessages(sendMessage);
            state = 11114096;
            return;
        } else if (parfums.containsKey(a)){
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(userID);
            editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageReplyMarkup.setReplyMarkup(null);
            botService.executeMessages(editMessageReplyMarkup);
            userBasket.get(userID).put(a,parfums.get(a));
            sendMessage.setChatId(userID);
            sendMessage.setText("Muvafaqiyatli qoshildi!");
            if (Objects.equals(userID, adminId)) {
                sendMessage.setReplyMarkup(replyService.keyboardMaker(admin_main));
            }else {
                sendMessage.setReplyMarkup(replyService.keyboardMaker(main));
            }
            botService.executeMessages(sendMessage);
        }
    }

    private static BotLogicService botLogicService;
    public static BotLogicService getInstance() {
        if (botLogicService == null) {
            botLogicService = new BotLogicService();
        }
        return botLogicService;
    }
    @SneakyThrows
    private static boolean checkIsThere(String table,String colum,String camparement){
        Statement statement = testConnection.getStatement();
        ResultSet resultSet = statement.executeQuery(String.format("select * from %s;",table));
        List<String> cards = new ArrayList<>();
        try {
            while (true) {
                if (!resultSet.next()) break;
                cards.add(resultSet.getString(colum));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (String card : cards) {
            if (Objects.equals(card, camparement)){
                return false;
            }
        }
        return true;
    }

}