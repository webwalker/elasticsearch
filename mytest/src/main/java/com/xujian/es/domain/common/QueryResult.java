package com.xujian.es.domain.common;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by xujian on 2019-07-31
 */
@Data
public class QueryResult {

    /**
     * 查询是否成功true=成功，false=失败
     */
    private boolean succesful;
    /**
     * 查询耗时
     */
    private long took;
    /**
     * 是否超时
     */
    private boolean timedout;
    /**
     * 查询总数
     */
    private long hitsTotal;
    /**
     * 最高评分
     */
    private float maxScore;
    /**
     * 分片信息
     * total : 分片总数
     * successful : 成功查询的分片数
     * skipped" : 跳过查询的分片数
     * failed" : 失败查询的分片数
     */
    private Map<String, Integer> shardsInfo;
    /**
     * 查询结果
     */
    private List<Map<String, Object>> hitsBody;

    public QueryResult() {
    }

    public QueryResult(boolean succesful) {
        this.succesful = succesful;
    }
}
