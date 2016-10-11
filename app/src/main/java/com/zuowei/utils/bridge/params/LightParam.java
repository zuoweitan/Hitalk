package com.zuowei.utils.bridge.params;


/**
 * Created by zuowei on 16-4-6.
 */
public class LightParam extends AbstractParam{

    private String mAction;
    public int arg1;
    public int arg2;
    public Object obj;
    public LightParam(String action){
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }

    public static class Builder{
        LightParam lightParam;
        public Builder(String action){
            lightParam = new LightParam(action);
        }

        public Builder setArg1(int arg1){
            lightParam.arg1 = arg1;
            return this;
        }

        public Builder setArg2(int arg2){
            lightParam.arg2 = arg2;
            return this;
        }

        public Builder setObj(Object obj){
            lightParam.obj = obj;
            return this;
        }

        public LightParam create(){
            return lightParam;
        }
    }
}
