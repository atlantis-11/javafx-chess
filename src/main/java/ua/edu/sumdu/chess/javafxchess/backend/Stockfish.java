package ua.edu.sumdu.chess.javafxchess.backend;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Stockfish {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final int skillLevel;
    private final int moveTime;

    public Stockfish(int skillLevel) {
        this.skillLevel = Math.min(Math.max(skillLevel, 0), 20);
        this.moveTime = 100 * this.skillLevel + 500;
    }

    public void start() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
            Objects.requireNonNull(
                Stockfish.class
                    .getClassLoader()
                    .getResource("stockfish-windows-x86-64-sse41-popcnt.exe")
            ).getPath()
        );

        process = builder.start();
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();

        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));

        sendCommand("uci");
        setSkillLevel();
    }

    public void stop() {
        try {
            sendCommand("quit");
        } catch (IOException e) {
            process.destroy();
        }
    }

    private void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    private void setSkillLevel() throws IOException {
        sendCommand("setoption name Skill Level value " + skillLevel);
    }

    public CompletableFuture<String> getBestMove(String fen) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                sendCommand("position fen " + fen);
                sendCommand("go movetime " + moveTime);

                Thread.sleep(moveTime);

                String line;
                do {
                    line = reader.readLine();
                } while (!line.contains("bestmove"));

                return line.split(" ")[1];
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}