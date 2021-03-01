JFLAGS = -g --source-path src/main/java -d build/classes/main
JC = javac
SRC_DIR = src/main/java
CLASSES_DIR = build/classes/main
JAR_NAME = $(shell basename $PWD).jar

$(CLASSES_DIR)/%.class: $(SRC_DIR)/%.java
	$(JC) $(JFLAGS) $(SRC_DIR)/$*.java

CLASSES = $(shell find src/main/java -name "*.java")

default: classes

classes: $(patsubst $(SRC_DIR)/%.java,$(CLASSES_DIR)/%.class,$(CLASSES))

build/libs/$(JAR_NAME): classes
	mkdir -p build/libs/ && \
	jar cvf build/libs/$(JAR_NAME) -C build/classes/main .

build: build/libs/$(JAR_NAME)

clean:
	rm -rf build
