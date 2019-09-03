package serialize.impl;

import com.alibaba.fastjson.JSON;
import serialize.Serializer;
import serialize.SerializerAlgorithm;



public class JSONSerializer implements Serializer {


    //获取序列化算法标识
    @Override
    public byte getSerializerAlgorithm() {

        return SerializerAlgorithm.JSON;
    }

    //将java对象转化为二进制
    @Override
    public byte[] serialize(Object object) {

        return JSON.toJSONBytes(object);
    }

    //将二进制转化为java对象
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {

        return JSON.parseObject(bytes, clazz);
    }
}
