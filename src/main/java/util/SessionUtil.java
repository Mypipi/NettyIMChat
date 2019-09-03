package util;

import attribute.Attributes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import session.Session;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionUtil {
    // userId -> channel 的映射
    private static final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();
    private static final Map<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<>();

    public static void bindSession(Session session, Channel channel) {
        userIdChannelMap.put(session.getUserId(), channel);
        channel.attr(Attributes.SESSION).set(session);
    }

    //内存里面自动删除 userId 到 channel 的映射关系，这是通过调用 SessionUtil.unBindSession() 来实现的。
    public static void unBindSession(Channel channel) {
        if (hasLogin(channel)) {
            Session session = getSession(channel);
            userIdChannelMap.remove(session.getUserId());
            channel.attr(Attributes.SESSION).set(null);
            System.out.println(session + " 退出登录!");
        }
    }

    //判断当前是否有用户的会话信息即可
    public static boolean hasLogin(Channel channel) {

        return getSession(channel) != null;
    }

    //提供了 getSession() 方法，非常方便地拿到对应 channel 的会话信息。
    public static Session getSession(Channel channel) {

        return channel.attr(Attributes.SESSION).get();
    }

    //可以通过 userId 拿到对应的 channel。
    public static Channel getChannel(String userId) {

        return userIdChannelMap.get(userId);
    }

    public static void bindChannelGroup(String groupId, ChannelGroup channelGroup) {
        groupIdChannelGroupMap.put(groupId, channelGroup);
    }

    public static ChannelGroup getChannelGroup(String groupId) {
        return groupIdChannelGroupMap.get(groupId);
    }
}