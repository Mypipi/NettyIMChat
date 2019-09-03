package server;

import codec.PacketCodecHandler;
import codec.PacketDecoder;
import codec.PacketEncoder;
import codec.Spliter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import server.handler.*;
import server.handler.inbound.InBoundHandlerA;
import server.handler.inbound.InBoundHandlerB;
import server.handler.inbound.InBoundHandlerC;
import server.handler.outbound.OutBoundHandlerA;
import server.handler.outbound.OutBoundHandlerB;
import server.handler.outbound.OutBoundHandlerC;

import java.util.Date;

//启动一个Netty服务端，必须要指定三类属性，分别是线程模型、IO 模型、连接读写处理逻辑

public class NettyServer {
    private static final int PORT = 8000;

    public static void main(String[] args) {

        //监听端口，accept新连接的线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();

        //处理没一条连接数据读写的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        //引导服务端的启动
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(bossGroup, workerGroup)//配置两大线程组，搭建线程模型
                .channel(NioServerSocketChannel.class)//指定服务端io为NIO，NIO是netty的优势
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {//定义后续每一条数据的读写，处理
                    //其中的泛型是NioSocketChannel是对NIO类型连接的抽象
                    protected void initChannel(NioSocketChannel ch) {
                        // inBound，处理读数据的逻辑链
                        //ch.pipeline().addLast(new InBoundHandlerA());
                        //ch.pipeline().addLast(new InBoundHandlerB());
                        //ch.pipeline().addLast(new InBoundHandlerC());

                        // outBound，处理写数据的逻辑链
                        // ch.pipeline().addLast(new OutBoundHandlerA());
                        //ch.pipeline().addLast(new OutBoundHandlerB());
                        //ch.pipeline().addLast(new OutBoundHandlerC());

                        //ch.pipeline().addLast(new FirstServerHandler());
                        //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        //ch.pipeline().addLast(new LifeCyCleTestHandler());
                        ch.pipeline().addLast(new IMIdleStateHandler());
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        ch.pipeline().addLast(LoginRequestHandler.INSTANCE);
                        ch.pipeline().addLast(HeartBeatRequestHandler.INSTANCE);
                        ch.pipeline().addLast(AuthHandler.INSTANCE);
                        ch.pipeline().addLast(IMHandler.INSTANCE);
                    }
                });

        bind(serverBootstrap, PORT);
    }


    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        //serverBootstrap.bind（）是异步方法，返回值是ChannelFuture可以添加监听器GenericFutureListener来判断是否绑定成功
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {

            public void operationComplete(Future<? super Void> future) {
                //判断是否成功绑定
                if (future.isSuccess()) {
                    System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
                } else {
                    System.err.println("端口[" + port + "]绑定失败!");
                    bind(serverBootstrap, port + 1);//判定失败端口+1继续绑定
                }
            }
        });
    }


}
