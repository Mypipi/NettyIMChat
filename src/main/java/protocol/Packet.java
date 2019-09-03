package protocol;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public abstract class Packet {

    //定义版本号
    @JSONField(deserialize = false, serialize = false)
    private Byte version = 1;

    //获取指令的抽象方法，所以数据包都要实现
    @JSONField(serialize = false)
    public abstract Byte getCommand();

}
