package net.dataforte.infinispan.playground.embeddedhotrod;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfiguration;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;

public class SimpleEmbeddedHotRodServer {

    public static void main(String[] args) throws IOException {
        org.infinispan.configuration.cache.ConfigurationBuilder embeddedBuilder = new org.infinispan.configuration.cache.ConfigurationBuilder();
        embeddedBuilder
           .dataContainer()
              .keyEquivalence(new AnyServerEquivalence())
              .valueEquivalence(new AnyServerEquivalence())
           .compatibility()
              .enable();
        DefaultCacheManager defaultCacheManager = new DefaultCacheManager(embeddedBuilder.build());
        /*
         * Use the following for XML configuration
           InputStream is = SimpleEmbeddedHotRodServer.class.getResourceAsStream("/infinispan.xml");
           DefaultCacheManager defaultCacheManager = new DefaultCacheManager(is);
        */
        Cache<String, String> embeddedCache = defaultCacheManager.getCache();

        HotRodServerConfiguration build = new HotRodServerConfigurationBuilder().build();
        HotRodServer server = new HotRodServer();
        server.start(build, defaultCacheManager);

        ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
        remoteBuilder.addServers("localhost");
        RemoteCacheManager remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
        RemoteCache<String, String> remoteCache = remoteCacheManager.getCache();

        System.out.print("Inserting data into remote cache...");
        for(char ch='A'; ch<='Z'; ch++) {
           String s = Character.toString(ch);
           remoteCache.put(s, s);
           System.out.printf("%s...", s);
        }

        System.out.print("\nVerifying data in remote cache...");
        for(char ch='A'; ch<='Z'; ch++) {
           String s = Character.toString(ch);
           assert s.equals(remoteCache.get(s));
           System.out.printf("%s...", s);
        }

        System.out.print("\nVerifying data in embedded cache...");
        for(char ch='A'; ch<='Z'; ch++) {
           String s = Character.toString(ch);
           assert s.equals(embeddedCache.get(s));
           System.out.printf("%s...", s);
        }

        System.out.print("\nInserting data into embedded cache...");
        for(char ch='a'; ch<='z'; ch++) {
           String s = Character.toString(ch);
           embeddedCache.put(s, s);
           System.out.printf("%s...", s);
        }

        System.out.print("\nVerifying data in embedded cache...");
        for(char ch='a'; ch<='z'; ch++) {
           String s = Character.toString(ch);
           assert s.equals(embeddedCache.get(s));
           System.out.printf("%s...", s);
        }

        System.out.print("\nVerifying data in remote cache...");
        for(char ch='a'; ch<='z'; ch++) {
           String s = Character.toString(ch);
           assert s.equals(remoteCache.get(s));
           System.out.printf("%s...", s);
        }
        System.console().readLine();

        System.out.println("\nDone !");
        remoteCacheManager.stop();
        server.stop();
        defaultCacheManager.stop();
    }
}
