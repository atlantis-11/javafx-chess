package ua.edu.sumdu.chess.javafxchess.backend;

import java.io.*;

public class Stockfish {
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public Stockfish() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
            Stockfish.class
                .getClassLoader()
                .getResource("stockfish-windows-x86-64-sse41-popcnt.exe")
                .getPath()
        );

        Process process = builder.start();
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();

        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));

        sendCommand("uci");
    }

    private void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public String getBestMove(String fen, int waitTime) throws IOException, InterruptedException {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        Thread.sleep(waitTime + 20);

        String line;
        do {
            line = reader.readLine();
        } while (!line.contains("bestmove"));

        return line.split("bestmove ")[1].split(" ")[0];
    }
}