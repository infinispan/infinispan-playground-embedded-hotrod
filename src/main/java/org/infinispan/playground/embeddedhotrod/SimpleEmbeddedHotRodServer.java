package org.infinispan.playground.embeddedhotrod;

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
        // Create a cache manager
        DefaultCacheManager defaultCacheManager = new DefaultCacheManager("infinispan.xml");
        Cache<String, String> embeddedCache = defaultCacheManager.getCache("default");

        // Create a Hot Rod server which exposes the cache manager
        HotRodServerConfiguration build = new HotRodServerConfigurationBuilder().build();
        HotRodServer server = new HotRodServer();
        server.start(build, defaultCacheManager);

        // Create a Hot Rod client
        ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
        remoteBuilder.addServers("localhost");
        RemoteCacheManager remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
        RemoteCache<String, String> remoteCache = remoteCacheManager.getCache("default");

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

        System.out.println("\nDone !");
        remoteCacheManager.stop();
        server.stop();
        defaultCacheManager.stop();
    }
}
