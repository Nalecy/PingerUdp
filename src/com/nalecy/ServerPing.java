package com.nalecy;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPing {

    private static final int MAX_DELAY = 1500;
    private static final int LOSS_TIME = 1000;

    private static final ExecutorService executors = Executors.newCachedThreadPool();

    //懒得在里面try catch了，直接main throws
    public static void main(String[] args) throws Exception {
//        Scanner scanner = new Scanner(System.in);// 接收从系统指定输入方式输入的数据
        String host = args[0];
//        String host = scanner.nextLine(); // 获取服务器端所在的主机地址
        int port = Integer.parseInt(args[1]);
//        int port = scanner.nextInt(); // 获取服务器端监听的端口号
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(port, InetAddress.getByName(host));
        } catch (SocketException e) {
            System.out.println("Socket创建失败，程序结束运行");
            e.printStackTrace();
            System.exit(-1);
        } catch (UnknownHostException e) {
            System.out.println("未知host，程序结束运行");
            e.printStackTrace();
            System.exit(-1);
        }
        byte[] receiveData = new byte[1024];            //用于接收数据的byte数组
        //死循环，不断的监听是否有请求数据
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);        //生成接收数据报包实例
            serverSocket.receive(receivePacket);        //接收到数据
            executors.submit(new ReplyTask(serverSocket, receivePacket));
        }

    }

    private static class ReplyTask implements Runnable {
        private final DatagramPacket receivePacket; // 接受到的数据分组
        private final DatagramSocket serverSocket; // 数据包套接字

        ReplyTask(DatagramSocket serverSocket, DatagramPacket receivePacket) {
            this.receivePacket = receivePacket;
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            byte[] sendData;

            String sentence = new String(receivePacket.getData());      //将数据从byte数组轮换回String
            InetAddress IPAddress = receivePacket.getAddress();         //获得client端的ip
            int port = receivePacket.getPort();                         //获得client端的port

            long randomTime = (long) (Math.random() * MAX_DELAY);       //生成随机数，用于模拟传输延迟

            try {
                Thread.sleep(randomTime);            //程度睡眠，用于模拟传输延迟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (randomTime > LOSS_TIME){
                sentence = "data loss！ \n";             //数据丢失的信息
            }

            System.out.println(sentence);               //显示请求数据
            sendData = sentence.getBytes();             //请求数据转换成byte数组，用于发回client端
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);        //生成数据包
            try {
                serverSocket.send(sendPacket);            //发回client端
            } catch (IOException e) {
                System.out.println("发送错误");
                e.printStackTrace();
            }
        }
    }
}
