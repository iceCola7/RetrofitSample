package com.cxz.retrofitlib;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc 用来保存参数的注解值，参数值，用于拼接最终的请求
 */
abstract class ParameterHandler {

    /**
     * 抽象方法，外部赋值和调用，自己的内部类实现了
     *
     * @param requestBuilder 请求构建者（拼装者）
     * @param value          方法参数值
     */
    abstract void apply(RequestBuilder requestBuilder, String value);

    // 自己的内部类实现自己的抽象方法

    static final class Query extends ParameterHandler {
        // 参数名
        private String name;

        Query(String name) {
            if (name.isEmpty()) {
                throw new NullPointerException("name is empty");
            }
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, String value) {
            // 此处的value是参数值
            if (value == null) return;
            builder.addQueryParam(name, value);
        }
    }


    static final class Field extends ParameterHandler {
        // 参数名
        private String name;

        Field(String name) {
            if (name.isEmpty()) {
                throw new NullPointerException("name is empty");
            }
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, String value) {
            // 此处的value是参数值
            if (value == null) return;
            // 拼接Field参数，此处的name为参数注解值，value为参数值
            builder.addFormField(name, value);
        }
    }

}
