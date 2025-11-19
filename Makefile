JARS := $(shell find lib -name "*.jar")
CLASSPATH := bin:$(shell printf "%s:" $(JARS) | sed 's/:$$//')
SRC := $(wildcard src/*.java)

state: $(SRC)
	mkdir -p bin
	javac -cp "$(CLASSPATH)" -d bin $(SRC)
	touch state

clean:
	rm -rf bin
	rm -f state

run_servidor: state
	java -cp "$(CLASSPATH)" Cliente

run_servidor: state
	java -cp "$(CLASSPATH)" Servidor

run: state
	kitty --hold -d "${PWD}" java -cp "$(CLASSPATH)" Cliente & 
	java -cp "$(CLASSPATH)"  Servidor

