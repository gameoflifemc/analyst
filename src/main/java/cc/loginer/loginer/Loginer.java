package cc.loginer.loginer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Plugin(id = "loginer", name = "Loginer", version = BuildConstants.VERSION)
public class Loginer {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("loginer:log");
    private final ProxyServer server;
    private final Path dataDirectory;
    private final File logFile;
    @Inject
    private Logger logger;

    @Inject
    public Loginer(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        if(!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir();
        }

        this.logFile = new File(dataDirectory.toFile(), "loginer.txt");

        if(!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info("Velocity logger initialized");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(IDENTIFIER);
    }

    @Subscribe
    public void onPluginMessageFromPlugin(PluginMessageEvent event) {
        // Ensure the identifier is what you expect before trying to handle the data
        if (event.getIdentifier() != IDENTIFIER) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        new Date().getTime();
        String log = "["+formatter.format(new Date())+"] "+in.readUTF()+"\n";
        try {
            Files.write(logFile.toPath(), log.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // handle packet data
    }
}
