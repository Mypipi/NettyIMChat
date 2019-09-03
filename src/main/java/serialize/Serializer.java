package serialize;

import serialize.impl.JSONSerializer;

public interface Serializer {

    /**
     * 获取序列化算法标识
     */



    Serializer DEFAULT = new JSONSerializer();

    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
