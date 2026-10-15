package br.com.fintrack.app;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InstaladorLauncher {
    private static Process api;

    public static void main(String[] args) {
        try {
            Path raiz = Path.of(System.getProperty("jpackage.app-path")).getParent();
            Path dados = Path.of(System.getenv().getOrDefault("LOCALAPPDATA", System.getProperty("user.home")), "FinTrack");
            Path logs = dados.resolve("logs");
            Files.createDirectories(logs);
            Path javaw = Path.of(System.getProperty("java.home"), "bin", "javaw.exe");
            Path apiJar = raiz.resolve("app").resolve("fintrack-api-3.0.0.jar");
            api = new ProcessBuilder(javaw.toString(), "-jar", apiJar.toString())
                    .directory(dados.toFile()).redirectErrorStream(true)
                    .redirectOutput(logs.resolve("backend.log").toFile()).start();
            Runtime.getRuntime().addShutdownHook(new Thread(InstaladorLauncher::encerrarApi));
            aguardarApi();
            FinApp.main(args);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi possivel iniciar o FinTrack. Consulte AppData\\Local\\FinTrack\\logs\\backend.log", "FinTrack", javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            encerrarApi();
        }
    }

    private static void aguardarApi() throws Exception {
        for (int i = 0; i < 60; i++) {
            if (api != null && !api.isAlive()) throw new IllegalStateException("A API foi encerrada.");
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("127.0.0.1", 8080), 300);
                return;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
        throw new IllegalStateException("A API nao iniciou na porta 8080.");
    }

    private static void encerrarApi() {
        if (api != null && api.isAlive()) api.destroy();
    }
}
