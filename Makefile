JARS := $(shell find lib -name "*.jar")
CLASSPATH := bin:$(shell printf "%s:" $(JARS) | sed 's/:$$//')
SRC := $(wildcard src/*.java)

state: $(SRC)
	mkdir -p bin
	@javac -cp "$(CLASSPATH)" -d bin $(SRC)
	touch state

clean:
	rm -rf bin
	rm -f state

run_cliente: state
	@java -cp "$(CLASSPATH)" UI

run_servidor: state
	@java -cp "$(CLASSPATH)" Servidor

run: state
	@kitty --hold -d "${PWD}" java -cp "$(CLASSPATH)" UI & 
	@kitty --hold -d "${PWD}" java -cp "$(CLASSPATH)" UI & 
	@kitty --hold -d "${PWD}" java -cp "$(CLASSPATH)" UI & 
	@java -cp "$(CLASSPATH)"  Servidor

