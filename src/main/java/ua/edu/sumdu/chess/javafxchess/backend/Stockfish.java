package ua.edu.sumdu.chess.javafxchess.backend;

import java.io.*;
import java.util.Objects;

public class Stockfish {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final int skillLevel;

    public Stockfish(int skillLevel) {
        this.skillLevel = skillLevel;
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
        setSkillLevel(skillLevel);
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

    private void setSkillLevel(int skillLevel) throws IOException {
        sendCommand("setoption name Skill Level value "
            + Math.min(Math.max(skillLevel, 0), 20));
    }

    public String getBestMove(String fen, int waitTime) throws IOException, InterruptedException {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        Thread.sleep(waitTime + 20);

        String line;
        do {
            line = reader.readLine();
        } while (!line.contains("bestmove"));

        return line.split(" ")[1];
    }
}