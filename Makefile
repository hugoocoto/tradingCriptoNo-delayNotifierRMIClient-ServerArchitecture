SRC = $(wildcard ./src/*.java)

state: $(SRC)
	javac $(SRC) -d bin -cp "lib/jsoup-1.21.2.jar" 
	touch state

clean:
	rm -rf bin
	rm state

run_servidor: state
	java -cp "bin" -cp "lib/jsoup-1.21.2.jar:." Servidor

run_cliente: state
	java -cp "bin" -cp "lib/jsoup-1.21.2.jar:."  UI
