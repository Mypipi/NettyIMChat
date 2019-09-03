package client;

/*
//该逻辑处理器继承ChannelInboundHandlerAdapter，重写了channelActive方法，连接建立后调用该方法
public class ClientHandler extends ChannelInboundHandlerAdapter {

    //该方法中写数据内容
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(new Date() + ": 客户端开始登录");

        // 1.创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();//创建登录对象
        loginRequestPacket.setUserId(UUID.randomUUID().toString());//设置随机id
        loginRequestPacket.setUsername("flash");
        loginRequestPacket.setPassword("pwd");

        // 2.将登录对象编码,在编码环节把PacketCodeC变成单例模式，然后把ByteBuf分配器抽取出一个参数，ctx.alloc()获取的就是与当前连接
        //相关的ByteBuf分配器
        ByteBuf buffer = PacketCodec.INSTANCE.encode(ctx.alloc(), loginRequestPacket);

        // 3. 写数据到服务器时，通过ctx.channel获取当前连接对象
        ctx.channel().writeAndFlush(buffer);
    }

    //从服务器读数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        //解码
        Packet packet = PacketCodec.INSTANCE.decode(byteBuf);

        //校验
        if (packet instanceof LoginResponsePacket) {
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;

            if (loginResponsePacket.isSuccess()) {
                System.out.println(new Date() + ": 客户端登录成功");
                //打上已经登录的标志
                LoginUtil.markAsLogin(ctx.channel());
            } else {
                System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
            }
        }else if (packet instanceof MessageResponsePacket) {
            MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
            System.out.println(new Date() + ": 收到服务端的消息: " + messageResponsePacket.getMessage());
        }
    }

    //Netty 里面数据是以 ByteBuf 为单位的， 所有需要写出的数据都必须塞到一个 ByteBuf，数据的读写都是如此
    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1. 获取二进制抽象 ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();

        // 2. 准备数据，指定字符串的字符集为 utf-8
        byte[] bytes = "你好，闪电侠!".getBytes(Charset.forName("utf-8"));

        // 3. 填充数据到 ByteBuf
        buffer.writeBytes(bytes);

        return buffer;
    }
}

 */