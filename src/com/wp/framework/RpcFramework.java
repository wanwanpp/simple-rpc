package com.wp.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {
    //发布服务
    public static void exportService(final Object service, int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            final Socket socket = serverSocket.accept();
            new Thread(() -> {
                ObjectInputStream reader = null;
                ObjectOutputStream writer = null;
                try {
                    //reader接收传过来的关于执行方法的信息
                    reader = new ObjectInputStream(socket.getInputStream());
                    String methodName = reader.readUTF();
                    Class[] argumentsType = (Class[]) reader.readObject();
                    Object[] arguments = (Object[]) reader.readObject();

                    //反射获取Method对象并执行此方法.
                    Method method = service.getClass().getMethod(methodName, argumentsType);
                    Object result = method.invoke(service, arguments);

                    //使用writer返回结果给调用端
                    writer = new ObjectOutputStream(socket.getOutputStream());
                    writer.writeObject(result);
                } catch (Exception e) {
                    if (null != writer) {
                        try {
                            writer.writeObject(e);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } finally {
                    if (null != writer) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != reader) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T useService(Class<T> interfaceClass, final String host, final int port) {
        //动态代理
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host, port);
                ObjectOutputStream writer = null;
                ObjectInputStream reader = null;
                try {
                    //通过writer将需要执行的方法信息（方法名，参数类型，参数）传过去
                    writer = new ObjectOutputStream(socket.getOutputStream());
                    writer.writeUTF(method.getName());
                    writer.writeObject(method.getParameterTypes());
                    writer.writeObject(args);

                    //通过reader读取远程返回的执行结果
                    reader = new ObjectInputStream(socket.getInputStream());

                    //将结果返回给调用方.
                    return reader.readObject();
                } finally {
                    if (null != writer) {
                        writer.close();
                    }
                    if (null != reader) {
                        reader.close();
                    }
                }
            }
        });
    }
}