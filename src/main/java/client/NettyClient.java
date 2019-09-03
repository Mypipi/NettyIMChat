package client;

import client.console.ConsoleCommandManager;
import client.console.LoginConsoleCommand;
import client.handler.*;
import codec.PacketDecoder;
import codec.PacketEncoder;
import codec.Spliter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import server.handler.GroupMessageRequestHandler;
import server.handler.IMIdleStateHandler;
import util.SessionUtil;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private static final int MAX_RETRY = 5;//最多重新连接次数
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 1.指定线程模型
                .group(workerGroup)
                // 2.指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                // 3.IO 处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {

                        //ch.pipeline()返回和这条连接相关的逻辑处理连接，addLast()是添加一个逻辑处理器
                        //在建立连接后向服务端写数据
                        //ch.pipeline().addLast(new ClientHandler());

                        //ch.pipeline().addLast(new FirstClientHandler());
                        //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        // 空闲检测
                        ch.pipeline().addLast(new IMIdleStateHandler());
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new PacketDecoder());
                        // 登录响应处理器
                        ch.pipeline().addLast(new LoginResponseHandler());
                        // 收消息处理器
                        ch.pipeline().addLast(new MessageResponseHandler());
                        ch.pipeline().addLast(new GroupMessageResponseHandler());
                        // 创建群响应处理器
                        ch.pipeline().addLast(new CreateGroupResponseHandler());
                        // 加群响应处理器
                        ch.pipeline().addLast(new JoinGroupResponseHandler());
                        // 退群响应处理器
                        ch.pipeline().addLast(new QuitGroupResponseHandler());
                        // 获取群成员响应处理器
                        ch.pipeline().addLast(new ListGroupMembersResponseHandler());
                        // 登出响应处理器
                        ch.pipeline().addLast(new LogoutResponseHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                        // 心跳定时器
                        ch.pipeline().addLast(new HeartBeatTimerHandler());
                    }
                });
        // 4.建立连接，异步的方法，返回值是future
        connect(bootstrap, HOST, PORT, MAX_RETRY);


    }

    //建立连接和失败重新连接的方法
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 连接成功，启动控制台线程……");
                Channel channel = ((ChannelFuture) future).channel();
                // 连接成功之后，启动控制台线程
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");

                //bootstrap.config()返回BootstrapConfig，他是对 Bootstrap 配置参数的抽象，然后 bootstrap.config().group()
                // 返回的就是我们在一开始的时候配置的线程模型 workerGroup，调 workerGroup 的 schedule 方法即可实现定时任务逻辑。
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }


    private static void startConsoleThread(Channel channel) {
        ConsoleCommandManager consoleCommandManager = new ConsoleCommandManager();
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        Scanner scanner = new Scanner(System.in);

        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!SessionUtil.hasLogin(channel)) {
                    loginConsoleCommand.exec(scanner, channel);
                } else {
                    consoleCommandManager.exec(scanner, channel);
                }
            }
        }).start();
    }

    /*
    private static void startConsoleThread(Channel channel) {
        Scanner sc = new Scanner(System.in);
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();

        new Thread(() -> {
            //如果没有中断一直执行
            while (!Thread.interrupted()) {
                //判断是否有标志位

                //如果当前用户还未登录，我们在控制台输入一个用户名，然后构造一个登录数据包发送
                // 给服务器，发完之后，我们等待一个超时时间，可以当做是登录逻辑的最大处理时间，这里就简单粗暴点了。
                if (!SessionUtil.hasLogin(channel)) {
                    System.out.println("输入用户名登录: ");

                    //输入一行消息
                    String username = sc.nextLine();
                    loginRequestPacket.setUserName(username);
                    loginRequestPacket.setPassword("pwd");
                    channel.writeAndFlush(loginRequestPacket);
                    waitForLoginResponse();

                    //一个消息包
                    //MessageRequestPacket packet = new MessageRequestPacket();
                    //packet.setMessage(line);
                    //编码
                    //ByteBuf byteBuf = PacketCodec.INSTANCE.encode(channel.alloc(), packet);
                    //写入到服务端
                    //channel.writeAndFlush(new MessageRequestPacket(line));
                }else{
                    //如果当前用户已经是登录状态，我们可以在控制台输入消息接收方的 userId，
                    // 然后输入一个空格，再输入消息的具体内容，然后，我们就可以构建一个消息数据包，发送到服务端。

                    String toUserId = sc.next();
                    String message = sc.next();
                    channel.writeAndFlush(new MessageRequestPacket(toUserId, message));

                }
            }
        }).start();
    }

     */


    private static void waitForLoginResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }


}