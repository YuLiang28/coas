package org.example.common.config.exception;

import lombok.Data;
import org.example.common.result.ResultCodeEnum;

@Data
public class OAException extends RuntimeException{

    private Integer code;
    private String message;

    public OAException(Integer code,String message){
        super(message);
        this.code = code;
        this.message=message;
    }

    public OAException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "OAException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}

