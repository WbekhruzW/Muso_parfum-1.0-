package util;

public interface Utils {

    String PARFUMS = "Atirlar\uD83C\uDF39";
    String REPLY = "reply";
    String ADD_TO_BASKET = "Savatga qoshish";
    String X = "❌";
    String BACK = "Orqaga🔙";
    String BASKET = "Savat🧺";
    String HISTORY = "Zakazlani tarixi\uD83D\uDCDC";
    String SEARCH = "Qidirish\uD83D\uDD0D";
    String CONTACT = "   Admin bilan ulanish📞";
    String LANGUAGE ="Tilni ozgartirish 🌍";
    String SEND_NUM = "Nomerni jonatish📨";
    String Add_Column = "Kategoriya qo'shing";
    String ORDER_IT = "Zakaz qilish";
    String LOCATION = "Lakatsiya tashlash";
    String HOW_MUCH_USERS = "Neshta user";
    String[][] main = {
            {PARFUMS},
            {BASKET,HISTORY},
            {CONTACT}
    };

    String[][] admin_main = {
            {PARFUMS},
            {BASKET,HISTORY},
            {CONTACT},
            {Add_Column,HOW_MUCH_USERS}

    };String[][] req_num = {
            {SEND_NUM}
    };String[][] order = {
            {ORDER_IT},
            {BACK}
    };String[][] loc = {
            {LOCATION},
            {BACK}
    };String[][] just_back = {
            {BACK}
    };

}
