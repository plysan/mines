if ! [ -x $JAVA_HOME/bin/java ]; then
	echo 'java is not installed, or $JAVA_HOME is not specified.'
	exit 1
fi

cd src
javac mine/Mine.java
java mine.Mine
