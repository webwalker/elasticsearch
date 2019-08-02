package com.xujian.es.domain.model;

import lombok.Data;

/**
 * ss_ware_resource_info实体类
 * <p>
 * simple分词,按分号等一般字符分词
 * IK分词,按中文含义分词
 */
@Data
public class IndexResourceInfoModel {
    //单位id
    private String unit_id;
    //资源id，唯一标识
    private String resource_id;
    //专题id-simple分词
    private String res_ware_id_simple;
    //题名-IK分词
    private String title_IK;
    //工作单位-IK分词
    private String basic_id_contact_IK;
    //工作单位-simple分词
    private String basic_id_contact_simple;
    //作者-simple分词
    private String basic_id_creator_simple;
    //关键词-IK分词
    private String basic_id_keyword_IK;
    //关键词-simple分词
    private String basic_id_keyword_simple;
    //年代-simple分词
    private String year;
    //摘要-IK分词
    private String basic_description_IK;
}
