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
    public static void exportService(final Object service, int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            final Socket socket = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObjectInputStream reader = null;
                    ObjectOutputStream writer = null;
                    try {
                        reader = new ObjectInputStream(socket.getInputStream());
                        String methodName = reader.readUTF();
                        Class[] argumentsType = (Class[]) reader.readObject();
                        Object[] arguments = (Object[]) reader.readObject();
                        Method method = service.getClass().getMethod(methodName, argumentsType);
                        Object result = method.invoke(service, arguments);

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
                }
            }).start();
        }
    }

    public static <T> T referenceService(Class<T> interfaceClass, final String host, final int port) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host, port);
                ObjectOutputStream writer = null;
                ObjectInputStream reader = null;
                try {
                    writer = new ObjectOutputStream(socket.getOutputStream());
                    writer.writeUTF(method.getName());
                    writer.writeObject(method.getParameterTypes());
                    writer.writeObject(args);
                    reader = new ObjectInputStream(socket.getInputStream());
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