package com.xujian.es.domain.common;

import lombok.Data;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.rest.RestStatus;

/**
 * Created by xujian on 2019-07-31
 */
@Data
public class EsResponse {
    private DocWriteResponse response;
    private boolean success;

    public EsResponse() {
    }

    public EsResponse(DocWriteResponse response) {
        this.response = response;
        if (response.status() == RestStatus.OK) {
            this.success = true;
        }
    }

    public static EsResponse ok(DocWriteResponse response) {
        return new EsResponse(response);
    }

    public static EsResponse fail() {
        return new EsResponse();
    }
}
