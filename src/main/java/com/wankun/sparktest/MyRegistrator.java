package com.wankun.sparktest;

import org.apache.spark.serializer.KryoRegistrator;

import com.esotericsoftware.kryo.Kryo;

public class MyRegistrator implements KryoRegistrator {
	@Override
	public void registerClasses(Kryo kryo) {
		kryo.register(org.apache.hadoop.hbase.io.ImmutableBytesWritable.class);
	}
}
