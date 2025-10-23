package net.paradise_client.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;

import java.util.ArrayList;

public class PacketHelper {
    private static final TimerHelper th = new TimerHelper();
    private static final float listTime = 300.0F;
    private static final ArrayList<Float> tpsList = new ArrayList<>();
    public static float fiveMinuteTPS = 0.0F;
    public static double lastTps;
    public static double tps;
    private static int tempTicks = 0;
    private static long lastReceiveTime = -1L;
    private static long startTime;
    private static boolean doneOneTime;
    private static long lastMS;
    private static int packetsPerSecond;
    private static int packetsPerSecondTemp = 0;
    public static int packetReceived = 0;
    public static int packetSent = 0;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void onPacketReceive(Packet<?> event) {
        lastTps = tps;

        if (event instanceof LoginHelloS2CPacket) {
            tps = 20.0D;
            fiveMinuteTPS = 20.0F;
        }

        if (event instanceof CommonPingS2CPacket || event instanceof TickTimeUpdateS2CPacket) {
            long currentReceiveTime = System.currentTimeMillis();
            if (lastReceiveTime != -1L) {
                long timeBetween = currentReceiveTime - lastReceiveTime;
                double neededTps = (double) timeBetween / 50.0D;
                double multi = neededTps / 20.0D;
                tps = 20.0D / multi;
                if (tps < 0.0D) tps = 0.0D;
                if (tps > 20.0D) tps = 20.0D;
            }
            lastReceiveTime = currentReceiveTime;
        }

        if (event instanceof CommonPingS2CPacket || event instanceof TickTimeUpdateS2CPacket) {
            ++packetsPerSecondTemp;
        }
    }

    public static void onAnyPacketReceived() {
        packetReceived++;
    }

    public static void onAnyPacketSent() {
        packetSent++;
    }

    public static void onUpdate() {
        if (TimerHelper.hasReached(2000L) && getServerLagTime() > 5000L) {
            th.reset();
            tps /= 2.0D;
        }

        if (MinecraftClient.getInstance().player == null) {
            tpsList.clear();
        }

        float temp = 0.0F;
        if (tempTicks >= 20) {
            tpsList.add((float) tps);
            tempTicks = 0;
        }

        if (tpsList.size() >= listTime) {
            tpsList.clear();
            tpsList.add((float) tps);
        }

        for (Float value : tpsList) {
            temp += value;
        }

        fiveMinuteTPS = tpsList.isEmpty() ? 0.0F : temp / tpsList.size();
        ++tempTicks;

        if (System.currentTimeMillis() - lastMS >= 1000L) {
            lastMS = System.currentTimeMillis();
            packetsPerSecond = packetsPerSecondTemp;
            packetsPerSecondTemp = 0;
            packetSent = 0;
            packetReceived = 0;
        }

        if (packetsPerSecond < 1) {
            if (!doneOneTime) {
                startTime = System.currentTimeMillis();
                doneOneTime = true;
            }
        } else {
            if (doneOneTime) {
                doneOneTime = false;
            }
            startTime = 0L;
        }
    }

    public static long getServerLagTime() {
        return startTime <= 0L ? 0L : System.currentTimeMillis() - startTime;
    }

    public static double lagTimeForSec() {
        double lagTime = getServerLagTime();
        if (lagTime > 100) {
            return Math.round(lagTime / 1000.0 * 10.0) / 10.0;
        }
        return 0;
    }

    public static void send(Packet<?> packet) {
        if (isPlayerConnected()) mc.getNetworkHandler().sendPacket(packet);
    }

    public static void sendChat(String message) {
        if (isPlayerConnected()) mc.getNetworkHandler().sendChatMessage(message);
    }

    public static void sendCommand(String message) {
        if (isPlayerConnected()) mc.getNetworkHandler().sendCommand(message);
    }

    private static boolean isPlayerConnected() {
        return mc.getNetworkHandler() != null && mc.getNetworkHandler().getConnection().isOpen();
    }
}
