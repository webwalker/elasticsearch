package com.xujian.es.config;

/**
 * Created by xujian on 2019-08-01
 */
public class EsConstant {
    public static final String BOOK_INDEX = "book";

    public static final String BOOK_TEST_INDEX = "book1";

    //创建book index语句
    public static final String CREATE_BOOK_INDEX = "{\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"author\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"category\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"price\": {\n" +
            "        \"type\": \"double\"\n" +
            "      },\n" +
            "      \"status\": {\n" +
            "        \"type\": \"short\"\n" +
            "      },\n" +
            "      \"sellReason\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"sellTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"format\": \"yyyy-MM-dd\"\n" +
            "      }\n" +
            "    }\n" +
            "}";
}
