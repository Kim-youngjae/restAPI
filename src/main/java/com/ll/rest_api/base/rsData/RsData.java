package com.ll.rest_api.base.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RsData<T> {
    private String resultCode;
    private String msg;
    private T data;

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    public static <T> RsData<T> of(String resultCode, String msg) {
        return of(resultCode, msg, null);
    }

    @JsonIgnore // json 에 포함 시키고 싶지 않은 정보는 해당 애너테이션을 붙인다.
    public boolean isSuccess() {
        return resultCode.startsWith("S-");
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}