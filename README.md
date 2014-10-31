# Spark 测试

## 测试说明

	JavaWordCount.java  统计各行单词个数，最后所有行单词汇总统计个数
	
	Spark 使用lazy evaluation，即只有当action操作发生的时候transformations才会在集群上执行
	
	程序运行：
	
spark-submit --class com.wankun.sparktest.JavaWordCount --master local target/sparkwordcount-0.0.1-SNAPSHOT.jar <input file> 2
	

程序运行方式：
1. --master local ： 本地运行
2. --master spark://<master host>:<master port> 集群运行
3. --master yarn ：在yarn上运行，程序自动检测ResourceManager's Address 
	