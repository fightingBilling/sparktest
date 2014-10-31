# Spark 测试

## 测试说明

	JavaWordCount.java  统计各行单词个数，最后所有行单词汇总统计个数
	
	Spark 使用lazy evaluation，即只有当action操作发生的时候transformations才会在集群上执行
	