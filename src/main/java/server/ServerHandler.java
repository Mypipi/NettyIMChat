package server;

//服务端侧的逻辑处理器同样继承自 ChannelInboundHandlerAdapter，与客户端不同的是，
// 这里覆盖的方法是 channelRead()，这个方法在接收到客户端发来的数据之后被回调。
/*
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {//msg参数是netty里数据读写的载体
        ByteBuf requestByteBuf = (ByteBuf) msg;

        // 解码
        Packet packet = PacketCodec.INSTANCE.decode(requestByteBuf);


        // 判断是否是登录请求数据包
        if (packet instanceof LoginRequestPacket) {
            System.out.println(new Date() + ": 收到客户端登录请求……");
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;

            //构造登录响应包
            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            //
            loginResponsePacket.setVersion(packet.getVersion());

            // 登录校验
            if (valid(loginRequestPacket)) {
                loginResponsePacket.setSuccess(true);// 校验成功
                System.out.println(new Date() + ": 登录成功!");
            } else {
                // 校验失败
                loginResponsePacket.setReason("账号密码校验失败");
                loginResponsePacket.setSuccess(false);
                System.out.println(new Date() + ": 登录失败!");
            }

            //编码
            ByteBuf responseByteBuf = PacketCodec.INSTANCE.encode(ctx.alloc(), loginResponsePacket);
            //写数据到客户端
            ctx.channel().writeAndFlush(responseByteBuf);

        }else if (packet instanceof MessageRequestPacket) {
            // 处理消息
            MessageRequestPacket messageRequestPacket = ((MessageRequestPacket) packet);
            //
            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());

            messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
            ByteBuf responseByteBuf = PacketCodec.INSTANCE.encode(ctx.alloc(), messageResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }

    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        byte[] bytes = "你好，欢迎关注我的微信公众号，《闪电侠的博客》!".getBytes(Charset.forName("utf-8"));

        //获得一个连接的bytebuf
        ByteBuf buffer = ctx.alloc().buffer();

        //写入数据
        buffer.writeBytes(bytes);

        return buffer;
    }
}


 */