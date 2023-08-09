package com.zx.common.constant;

public class ObjectConstant {

    public enum BooleanIntEnum{
        YES(1,"是"),NO(0,"否");
        private Integer code;
        private String message;
        BooleanIntEnum(Integer code,String message){
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
