package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import lombok.Getter;
import lombok.Setter;
import org.acrabsoft.common.model.Result;

@Getter
@Setter
public class ResultDto extends Result {
    private Object spare1;
    private Object spare2;
    private Object spare3;
    public ResultDto() {

    }
    public ResultDto(Result result,Object spare1) {
        this.setData( result.getData() );
        this.setErrcode( result.getErrcode() );
        this.setErrmsg( result.getErrmsg() );
        this.spare1 = spare1;
    }
}
