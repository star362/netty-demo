package com.netty.star.csdn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * @Author star362
 * @Date 2023/4/30 17:23
 * @Description: TODO
 */
public class webSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 当web客户端连接后触发该方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id表示唯一的值， longText是唯一的
        System.out.println("handleradded被调用" + ctx.channel().id().asLongText());
        //不唯一的
        System.out.println("被调用" + ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生");
        ctx.close();
        //  super.exceptionCaught(ctx, cause);
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        //writeAndFlush 是 write + flush
//        //将数据写入到缓存，并刷新
//        //一般讲，我们对这个发送的数据进行编码
////        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵", CharsetUtil.UTF_8));
//
//        ctx.channel().eventLoop().execute(()->{
//            for (int i = 0; i < 3; i++) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 \n", CharsetUtil.UTF_8));
//            }
//        });
//    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved被调用" + ctx.channel().id().asLongText());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println("收到消息:" + msg.text());

        for (int i = 0; i < 10; i++) {
            Thread.sleep(3000);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("成功接收到客户端发送的：" + msg.text()));
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器端返回：" + LocalDateTime.now()));
        }


    }
}


