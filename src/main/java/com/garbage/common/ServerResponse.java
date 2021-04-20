package com.garbage.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse <T>{

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess()
    {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static com.garbage.common.ServerResponse createBySuccess()
    {
        return new com.garbage.common.ServerResponse(ResponseCode.SUCCESS.getCode());
    }

    public static com.garbage.common.ServerResponse createBySuccessMsg(String msg)
    {
        return new com.garbage.common.ServerResponse(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> com.garbage.common.ServerResponse createBySuccess(T data)
    {
        return new com.garbage.common.ServerResponse(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> com.garbage.common.ServerResponse createBySuccess(String msg, T data)
    {
        return new com.garbage.common.ServerResponse(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static com.garbage.common.ServerResponse createByError()
    {

        return new com.garbage.common.ServerResponse(ResponseCode.ERROR.getCode());
    }

    public static com.garbage.common.ServerResponse createByErrorMsg(String msg)
    {
        return new com.garbage.common.ServerResponse(ResponseCode.ERROR.getCode(), msg);
    }

    public static <T> com.garbage.common.ServerResponse createByError(T data)
    {
        return new com.garbage.common.ServerResponse(ResponseCode.ERROR.getCode(), data);
    }

    public static com.garbage.common.ServerResponse createByCodeErrorMsg(int status, String msg)
    {
        return new com.garbage.common.ServerResponse(status, msg);
    }

}
