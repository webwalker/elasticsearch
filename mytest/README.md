有两种方式查询：

Spring-data-elas..

直接依赖es..（推荐）

批量测试数据：https://github.com/webwalker/elasticsearch-data

https://github.com/spinscale/elasticsearch-ecommerce-search-app

- @PostConstruct 预创建 index

官方Api：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-getting-started.html#java-rest-high-getting-started

https://www.cnblogs.com/cjsblog/p/10232581.html

查询对应的DSL：

{
    "from":0,
    "size":10,
    "query":{
        "bool":{
            "must":[
                {
                    "match":{
                        "author":{
                            "query":"金庸",
                            "operator":"OR",
                            "prefix_length":0,
                            "max_expansions":50,
                            "fuzzy_transpositions":true,
                            "lenient":false,
                            "zero_terms_query":"NONE",
                            "auto_generate_synonyms_phrase_query":true,
                            "boost":1
                        }
                    }
                },
                {
                    "term":{
                        "status":{
                            "value":1,
                            "boost":1
                        }
                    }
                },
                {
                    "bool":{
                        "should":[
                            {
                                "term":{
                                    "category":{
                                        "value":10010,
                                        "boost":1
                                    }
                                }
                            },
                            {
                                "term":{
                                    "category":{
                                        "value":10011,
                                        "boost":1
                                    }
                                }
                            },
                            {
                                "term":{
                                    "category":{
                                        "value":10012,
                                        "boost":1
                                    }
                                }
                            }
                        ],
                        "adjust_pure_negative":true,
                        "boost":1
                    }
                }
            ],
            "adjust_pure_negative":true,
            "boost":1
        }
    },
    "sort":[
        {
            "id":{
                "order":"asc"
            }
        }
    ]
}