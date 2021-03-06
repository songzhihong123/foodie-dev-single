package com.imooc.enums;

/**
 * @Desc: 商品评价的枚举.
 */
public enum CommentLevel {

    GOOD(1,"好评"),
    NORMAL(2,"中评"),
    BAD(3,"差评");

    public Integer type;
    public String value;

    CommentLevel(Integer type,String value){
        this.type = type;
        this.value = value;
    }


}
