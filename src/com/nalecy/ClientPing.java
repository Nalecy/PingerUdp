package com.nalecy;

import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class ClientPing {
    private static final int USER_ID = (int) (Math.random() * 10000);
    public static void main(String[] args) throws Exception{
//        Scanner scanner = new Scanner(System.in);// 接收从系统指定输入方式输入的数据
        String host = args[0];
//        String host = scanner.nextLine(); // 获取服务器端所在的主机地址
        int port = Integer.parseInt(args[1]);
//        int port = scanner.nextInt(); // 获取服务器端监听的端口号
        Long[] rtts = new Long[10];

        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Socket创建失败，程序结束运行");
            e.printStackTrace();
            System.exit(-1);
        }

        for (int i = 0; i < 10; i++) {
            long beforeTime = new Date().getTime();
            long afterTime;
            String data = "head: request " + i + " \n" // 模拟用的请求数据
                    + "playload: PingUDP UserId: " + USER_ID + " SequenceNumber :" + i + " TimeStamp:" + beforeTime + "\n";
            byte[] sendData = data.getBytes();
            byte[] receiveData = new byte[1024];

            //发送
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
            sendPacket.setAddress(InetAddress.getByName(host));
            sendPacket.setPort(port);
            clientSocket.send(sendPacket);

            //接受
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            afterTime = new Date().getTime();

            System.out.print(new String(receivePacket.getData()));
            rtts[i] = afterTime - beforeTime;
            System.out.println("rtt = " + rtts[i]);
        }
        // 统计出平均rtt,最大rtt和最小rtt
        long sumRtt = 0;
        long maxRtt = 0;
        long minRtt = rtts[0];
        for (int i = 0; i < 10; i++) {
            if (rtts[i] > maxRtt) {
                maxRtt = rtts[i];
            }
            if (rtts[i] < minRtt) {
                minRtt = rtts[i];
            }
            sumRtt += rtts[i];
        }
        System.out.println("平均 rtt: " + sumRtt / 10 + " 毫秒");
        System.out.println("最大 RTT: " + maxRtt + " 毫秒");
        System.out.println("最小 RTT: " + minRtt + " 毫秒");
    }
}
