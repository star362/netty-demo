package com.netty.star.csdn;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author star362
 * @Date 2023/4/30 17:15
 * @Description: TODO
 */
public class HttpServer {
    //端口监听
    private  int  PORT;
    //定义构造函数
    public HttpServer(int PORT) {
        this.PORT = PORT;
    }
    //定义run方法，处理客户端请求
    public void  run(){
        //创建BossGroup和WorkerGroup
        //负责客户端连接
        //启动服务端
        //     new ServerChat(6666).run();
        EventLoopGroup bossGroup= new NioEventLoopGroup();
        //负责网络读写
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来进行设置
            bootstrap.group(bossGroup,workGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class)
                    //  .option(ChannelOption.SO_BACKLOG,128) //设置线程对列得到连接个数
                    // .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>(){ //创建一个通道测试对象

                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //返回channel关联的pipeline
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //该方法为netty提供的空闲状态处理器
                            //参数分别表示为： long readerIdleTime,多长时间没读发送心跳检测包，检测是否连接
                            // long writerIdleTime,：多长时间没写发送心跳检测包检测是否连接
                            // long allIdleTime,多长时间即没有读也没有写
                            //  pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            pipeline.addLast(new HttpServerCodec());//添加解码器，解码器
                            //以块方式写，添加ChunkedWriteHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            //http数据的传输过程是分段的 HttpObjectAggregator可以将多个端聚合
                            //浏览器在发送大数据时就会多次发送http请求
                            pipeline.addLast(new HttpObjectAggregator(1024));
                            /*
                             * 1.对应webSocket，它的数据时以帧（frame）的形式传播
                             * 2。websocketFrame下有6个子类
                             * 3.浏览器请求时 ws://localhost:6666/ ，表示请求的url
                             * 4.WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议保持长连接
                             * */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hll"));
                            pipeline.addLast(new webSocketServerHandler());  //添加自定义业务处理器

                        }
                    });
            //给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("服务器is ready");

            //绑定端口，并同步处理
            ChannelFuture sync = bootstrap.bind(PORT).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (sync.isSuccess()){
                        System.out.println("注册监听成功");
                    }else {
                        System.out.println("error");
                    }
                }
            });
//对关闭通道进行监听
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) {
        new HttpServer(8888).run();
    }
}

