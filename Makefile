# Makefile for Minishell project

# Variables
JAVAC = javac
JAVAFLAGS = -d bin -sourcepath src
SRCDIR = src
BINDIR = bin
CLASSES = EchoCatNano MkdirTouch Cd_PWD_Ls Shell

# Targets
all: $(CLASSES)

EchoCatNano:
	$(JAVAC) $(JAVAFLAGS) $(SRCDIR)/EchoCatNano.java

MkdirTouch:
	$(JAVAC) $(JAVAFLAGS) $(SRCDIR)/MkdirTouch.java

Cd_PWD_Ls:
	$(JAVAC) $(JAVAFLAGS) $(SRCDIR)/Cd_PWD_Ls.java

Shell:
	$(JAVAC) $(JAVAFLAGS) $(SRCDIR)/Shell.java

clean:
	rm -rf $(BINDIR)/*

.PHONY: all clean
