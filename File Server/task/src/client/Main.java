package client;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static String action(String act) {
        String action = "exit";
        if (act.equals("1")) action = "GET";
        if (act.equals("2")) action = "PUT";
        if (act.equals("3")) action = "DELETE";
        return action;
    }

    public static void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 23456;
        Scanner scanner = new Scanner(System.in);
        String act;
        String msg;
        String f = "";
        String path = "C:\\Users\\Majid\\IdeaProjects\\File Server\\File Server\\task\\src\\client\\data\\";
        Socket socket = new Socket(InetAddress.getByName(address), port);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
        act = action(scanner.nextLine());

        if (!act.equals("exit")) {
            if (act.equals("PUT")) {                 //WRITE A NEW FILE ON CLIENT & SERVER
                System.out.print("Enter filename: ");
                f = scanner.nextLine();
                byte[] ctnt = null;
                File fileClient = new File(path + f);
                if (!fileClient.exists()) {
                    System.out.print("Enter file content: ");
                    ctnt = scanner.nextLine().getBytes();
                    // writing file on client storage
                    Path fileName = Path.of(path + f);
                    try (FileOutputStream fos = new FileOutputStream(String.valueOf(fileName))) {
                        fos.write(ctnt);
                        fos.flush();
                    }
                } else {
                    ctnt = Files.readAllBytes(Paths.get(String.valueOf(fileClient)));
                }
                System.out.print("Enter name of the file to be saved on server: ");
                String fServer = scanner.nextLine();
                if (fServer.equals("")) fServer = f;
                output.writeUTF(act);
                output.writeUTF(fServer);
                output.writeInt(ctnt.length);
                output.write(ctnt);
                output.flush();
                System.out.println("The request was sent.");
                System.out.println(input.readUTF());

            } else if (act.equals("GET")) {             //GET A FILE FROM SERVER AND SAVE ON CLIENT
                String response = "";
                System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                String type = scanner.nextLine();
                if (type.equals("1")) {
                    System.out.print("Enter name: ");
                    String fileName = scanner.nextLine();
                    output.writeUTF(act);
                    output.writeUTF("BY_NAME");
                    output.writeUTF(fileName);
                    output.flush();
                    System.out.println("The request was sent.");
                    response = input.readUTF();
                    if (response.equals("OK")) {
                        int length = input.readInt();
                        byte[] ctnt = new byte[length];
                        input.readFully(ctnt,0,length);
                        System.out.print("The file was downloaded! Specify a name for it: ");
                        f = scanner.nextLine();
                        Path fileClient = Path.of(path + f);
                        try (FileOutputStream fos = new FileOutputStream(String.valueOf(fileClient))) {
                            fos.write(ctnt);
                        }
                        response ="File saved on the hard drive!";
                    }
                } else {
                    System.out.print("Enter id: ");
                    String fileID = scanner.nextLine();
                    output.writeUTF(act);
                    output.writeUTF("BY_ID");
                    output.writeInt(Integer.valueOf(fileID));
                    System.out.println("The request was sent.");
                    response = input.readUTF();
                    if (response.equals("OK")) {
                        int length = input.readInt();
                        byte[] ctnt = new byte[length];
                        input.readFully(ctnt, 0, length);
                        System.out.print("The file was downloaded! Specify a name for it: ");
                        f = scanner.nextLine();
                        Path fileClient = Path.of(path + f);
                        try (FileOutputStream fos = new FileOutputStream(String.valueOf(fileClient))) {
                            fos.write(ctnt);
                        }
                        response = "File saved on the hard drive!";
                    }
                }
                System.out.println(response);
            } else if (act.equals("DELETE")) {
                String response = "";
                System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                String type = scanner.nextLine();
                if (type.equals("2")) {
                    System.out.print("Enter id: ");
                    String fileID = scanner.nextLine();
                    output.writeUTF(act);
                    output.writeUTF("BY_ID");
                    output.writeInt(Integer.valueOf(fileID));
                    output.flush();
                    System.out.println("The request was sent.");
                } else {
                    System.out.print("Enter name: ");
                    String fileName = scanner.nextLine();
                    output.writeUTF(act);
                    output.writeUTF("BY_NAME");
                    output.writeUTF(fileName);
                    output.flush();
                    System.out.println("The request was sent.");
                }
                System.out.println(input.readUTF());
            }
        } else {
            msg = act;
            output.writeUTF(msg);
            output.flush();
            System.out.println("The request was sent.");
        }

        socket.close();

    }
}
