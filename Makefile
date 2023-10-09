JAVAC = javac

JAVA = java

SRCS = NetworkDetailsCalculator.java

CLASSES = NetworkDetailsCalculator.class

.PHONY: all run clean

all: $(CLASSES)

run: all
	$(JAVA) NetworkDetailsCalculator

$(CLASSES): $(SRCS)
	$(JAVAC) $^

clean:
	rm -f $(CLASSES)
