package com.sanerzone.common.support.storedMap;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.queue.BdbQueueMap;
import com.sanerzone.common.support.utils.PropertiesLoader;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public enum BDBStoredMapFactoryImpl implements StoredMapFactory<String, Serializable> {
	INS;
	private static final Logger logger = LoggerFactory.getLogger(BDBStoredMapFactoryImpl.class);
	private final ConcurrentHashMap<String, QueueEnvironment> envMap = new ConcurrentHashMap<String, QueueEnvironment>();

	private final ConcurrentHashMap<String, StoredMap<String, Serializable>> storedMaps = new ConcurrentHashMap<String, StoredMap<String, Serializable>>();
	private final ConcurrentHashMap<String, StoredSortedMap<Long, Serializable>> sortedstoredMap = new ConcurrentHashMap<String, StoredSortedMap<Long, Serializable>>();
	private final ConcurrentHashMap<String, BlockingQueue<Serializable>> queueMap = new ConcurrentHashMap<String, BlockingQueue<Serializable>>();


	public synchronized Map<String, Serializable> buildMap(String storedpath, String name) {
		QueueEnvironment env = buildBDB(storedpath);
		FstSerialBinding<String> SerializableKeyBinding =  new FstSerialBinding<String>();
		FstSerialBinding<Serializable> SerializableValueBinding =  new FstSerialBinding<Serializable>();
		Database db = env.buildDatabase(name);

		String keyName = new StringBuilder().append(storedpath).append(name).toString();
		StoredMap<String, Serializable> map = storedMaps.get(keyName);
		if (map == null) {
			StoredMap<String, Serializable> tmpMap = new StoredMap<String, Serializable>(db, SerializableKeyBinding, SerializableValueBinding, true);
			StoredMap<String, Serializable> old = storedMaps.putIfAbsent(keyName, tmpMap);
			return old ==null ? tmpMap:old;
		}
		return map;
	}
	

	public synchronized BlockingQueue<Serializable> getQueue(String storedpath, String name) {
		String keyName = new StringBuilder().append(storedpath).append(name).toString();
		BlockingQueue<Serializable> queue = queueMap.get(keyName);
		if (queue == null) {
			StoredSortedMap<Long, Serializable> sortedStoredmap = buildStoredSortedMap(storedpath, "Trans_" + name);
			BlockingQueue<Serializable> newqueue = new BdbQueueMap<Serializable>(sortedStoredmap);
			BlockingQueue<Serializable> oldqueue = queueMap.putIfAbsent(keyName, newqueue);
			return oldqueue==null?newqueue:oldqueue;
		}
		return queue;
	}

	private StoredSortedMap<Long, Serializable> buildStoredSortedMap(String storedpath, String name) {
		QueueEnvironment env = buildBDB(storedpath);
		SerialBinding<Long> SerializableKeyBinding = new SerialBinding<Long>(env.getStoredClassCatalog(), Long.class);
		SerialBinding<Serializable> SerializableValueBinding = new SerialBinding<Serializable>(env.getStoredClassCatalog(), Serializable.class);
//		FstSerialBinding<Long> SerializableKeyBinding =  new FstSerialBinding<Long>();
//		FstSerialBinding<Serializable> SerializableValueBinding =  new FstSerialBinding<Serializable>();
		Database db = env.buildDatabase(name);
		String keyName = new StringBuilder().append(storedpath).append(name).toString();

		StoredSortedMap<Long, Serializable> soredMap = sortedstoredMap.get(keyName);

		if (soredMap == null) {
			soredMap = new StoredSortedMap<Long, Serializable>(db, (EntryBinding<Long>) SerializableKeyBinding, (EntryBinding<Serializable>) SerializableValueBinding, true);

			StoredSortedMap<Long, Serializable> old = sortedstoredMap.putIfAbsent(keyName, soredMap);
			return old == null ? soredMap : old;
		}

		return soredMap;
	}

	private QueueEnvironment buildBDB(String basename) {
		String pathName;
		basename = basename==null?"":basename;
		if(Global.getConfig("je.dbd.basehome").endsWith("/")){
			 pathName = Global.getConfig("je.dbd.basehome") + basename;
		}else{
			 pathName = Global.getConfig("je.dbd.basehome") +"/"+ basename;
		}
		
		File file = new File(pathName);
		if (!file.exists()) {
			boolean succ = file.mkdirs();

			if (!succ) {
				logger.error("create Directory {} failed. ", pathName);
				return null;
			}

		}

		if (!file.isDirectory()) {
			logger.error("file  {} is not a Directory ", pathName);
			return null;
		}
		logger.info("init BDBPath : {}" ,pathName);
		QueueEnvironment env = envMap.get(pathName);

		if (env == null) {
			env = new QueueEnvironment().buildEnvironment(pathName).buildStoredClassCatalog();
			QueueEnvironment oldenv = envMap.putIfAbsent(pathName, env);
			return oldenv == null ? env : oldenv;
		}
		return env;
	}

	private class QueueEnvironment {
		private Environment environment;
		private DatabaseConfig dbConfig;
		private Database classCatalogDB;
		private StoredClassCatalog storedClassCatalog;
		private ConcurrentHashMap<String, Database> dbMap = new ConcurrentHashMap<String, Database>();
		private PropertiesLoader loader = new PropertiesLoader("je.properties");
		
		public QueueEnvironment buildEnvironment(String pathHome) {

			File home = new File(pathHome);
			// 获取BDB的配置文件
			EnvironmentConfig environmentConfig = new EnvironmentConfig(loader.getProperties());
			environmentConfig.setAllowCreate(true);
			environmentConfig.setTransactional(true);
			environment = new Environment(home, environmentConfig);
			dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setTransactional(true);
			cleanLogSchedule();
			return this;
		}

		public QueueEnvironment buildStoredClassCatalog() {
			return buildStoredClassCatalog("classCatalog");
		}

		public QueueEnvironment buildStoredClassCatalog(String Name) {
			classCatalogDB = environment.openDatabase(null, Name, dbConfig);
			storedClassCatalog = new StoredClassCatalog(classCatalogDB);
			return this;
		}

		@SuppressWarnings("unchecked")
		public Database buildDatabase(String queueName) {
			Database queueDB = dbMap.get(queueName);
			if (queueDB == null) {
				queueDB = environment.openDatabase(null, queueName, dbConfig);
				Database olddb = dbMap.putIfAbsent(queueName, queueDB);
				return olddb == null?queueDB : olddb;
			}
			return queueDB;
		}

		public void clearLog() {
			environment.cleanLog();
		}

		public void close() {
			environment.cleanLog();
			classCatalogDB.close();
			closeAllQueue();
			environment.close();
		}

		private synchronized void closeAllQueue() {
			for (Entry<String, Database> entry : dbMap.entrySet()) {
				entry.getValue().close();
			}
		}

		public StoredClassCatalog getStoredClassCatalog() {
			return storedClassCatalog;
		}

		private Properties loadFrompropertiesFile(File file) {
			Properties tmpProperties = new Properties();
			InputStream in = null;
			try {
				in = FileUtils.openInputStream(file);
				tmpProperties.load(in);
			} catch (Exception ex) {
				logger.error("load je.properties error.", ex);
			} finally {
				IOUtils.closeQuietly(in);
			}
			return tmpProperties;
		}

		/**
		 * 定时清除BDB的Log
		 * 
		 */
		private void cleanLogSchedule() {
//			EventLoopGroupFactory.INS.getBusiWork().scheduleWithFixedDelay(new Runnable() {
//
//				@Override
//				public void run() {
//					clearLog();
//				}
//			}, 60, 60, TimeUnit.SECONDS);
		}

	}

}
