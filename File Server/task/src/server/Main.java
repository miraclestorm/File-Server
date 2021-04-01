package server;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Scanner;

public class Main {

    private static int ID() throws FileNotFoundException {
        int id = 0;
        File f = new File("C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\serial");
        if (f.exists() && !f.isDirectory()) {
            Scanner myReader = new Scanner(f);
            id = myReader.nextInt() + 1;
            PrintWriter prw= new PrintWriter (f);
            prw.println(id);
            prw.close();
        } else {
            PrintWriter prw= new PrintWriter (f);
            prw.println(id);
            prw.close();
        }
        return id;
    }

    public static void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 23456;
        System.out.println("Server started!");
        ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));
        String act = "";

        do {
            Socket socket = server.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            act = input.readUTF();

            if (!act.equals("exit")) {
                String serverMSG = "";
                File f = new File("");

                if (act.equals("GET")) {
                    if (input.readUTF().equals("BY_NAME")) {
                        f = new File("C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\" + input.readUTF());
                        if (f.exists() && !f.isDirectory()) {
                            serverMSG = "OK";
                            output.writeUTF(serverMSG);
                            Path fileName = Paths.get(String.valueOf(f));
                            byte[] ctnt = Files.readAllBytes(fileName);
                            output.writeInt(ctnt.length);
                            output.write(ctnt);
                            output.flush();
                        } else {
                            serverMSG = "The response says that this file is not found!";
                            output.writeUTF(serverMSG);
                            output.flush();
                        }
                    } else {
                        int fileID = input.readInt();
                        String path = "C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\";
                        File folder = new File("C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data");
                        File[] listOfFiles = folder.listFiles();
                        for (int i = 0; i < listOfFiles.length; i++) {
                            Path fileName = Path.of(path + listOfFiles[i].getName());
                            UserDefinedFileAttributeView view = Files.getFileAttributeView(fileName, UserDefinedFileAttributeView.class);
                            String id = "FileID";
                            ByteBuffer buf = ByteBuffer.allocate(view.size(id));
                            view.read(id, buf);
                            buf.flip();
                            String value = Charset.defaultCharset().decode(buf).toString();
                            if (value.equals(String.valueOf(fileID))){
                                serverMSG = "OK";
                                output.writeUTF(serverMSG);
                                byte[] ctnt = Files.readAllBytes(fileName);
                                output.writeInt(ctnt.length);
                                output.write(ctnt);
                                output.flush();
                                break;
                            }
                        }
                        if (!serverMSG.equals("OK")) {
                            serverMSG = "The response says that this file is not found!";
                            output.writeUTF(serverMSG);
                            output.flush();
                        }
                    }
                } else if (act.equals("PUT")) {
                    f = new File("C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\" + input.readUTF());
                    if (f.exists() && !f.isDirectory()) {
                        serverMSG = "The response says that creating the file was forbidden!";
                        output.writeUTF(serverMSG);
                        output.flush();
                    } else {
                        int length = input.readInt();
                        byte[] ctnt = new byte[length];
                        input.readFully(ctnt,0,length);
                        Path fileName = Path.of(String.valueOf(f));
                        try (FileOutputStream fos = new FileOutputStream(String.valueOf(fileName))) {
                            fos.write(ctnt);
                        }

                        UserDefinedFileAttributeView view = Files.getFileAttributeView(fileName, UserDefinedFileAttributeView.class);
                        view.write("FileID", Charset.defaultCharset().encode(String.valueOf(ID())));
                        String fileID = "FileID";
                        ByteBuffer buf = ByteBuffer.allocate(view.size(fileID));
                        view.read(fileID, buf);
                        buf.flip();
                        String value = Charset.defaultCharset().decode(buf).toString();
//                        System.out.println(f + " FileID: " + value);

                        serverMSG = "Response says that file is saved! ID = " + value;
                        output.writeUTF(serverMSG);
                        output.flush();
                    }
                } else if (act.equals("DELETE")) {
                    if (input.readUTF().equals("BY_NAME")) {
                        f = new File("C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\" + input.readUTF());
                        if (f.exists() && !f.isDirectory()) {
                            serverMSG = "The response says that the file was successfully deleted!";
                            output.writeUTF(serverMSG);
                            f.delete();
                        } else {
                            serverMSG = "The response says that this file is not found!";
                            output.writeUTF(serverMSG);
                            output.flush();
                        }
                    } else {
                        int fileID = input.readInt();
                        String path = "C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\";
                        File folder = new File("C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data");
                        File[] listOfFiles = folder.listFiles();
                        for (int i = 0; i < listOfFiles.length; i++) {
                            Path fileName = Path.of(path + listOfFiles[i].getName());
                            UserDefinedFileAttributeView view = Files.getFileAttributeView(fileName, UserDefinedFileAttributeView.class);
                            String id = "FileID";
                            ByteBuffer buf = ByteBuffer.allocate(view.size(id));
                            view.read(id, buf);
                            buf.flip();
                            String value = Charset.defaultCharset().decode(buf).toString();
                            if (value.equals(String.valueOf(fileID))){
                                serverMSG = "The response says that the file was successfully deleted!";
                                output.writeUTF(serverMSG);
                                listOfFiles[i].delete();
                                break;
                            }
                        }
                        if (!serverMSG.equals("The response says that the file was successfully deleted!")) {
                            serverMSG = "The response says that this file is not found!";
                            output.writeUTF(serverMSG);
                            output.flush();
                        }
                    }

                }

                socket.close();

            }

        } while (!act.equals("exit"));

        server.close();

    }
}
