package protocol.request;

import lombok.Data;
import protocol.Packet;

import static protocol.command.Command.LOGIN_REQUEST;


//定义一个登录请求数据包
@Data
public class LoginRequestPacket extends Packet {
   // private String userId;//id

    private String userName;//用户名

    private String password;//密码

    @Override
    public Byte getCommand() {

        return LOGIN_REQUEST;
    }
}
