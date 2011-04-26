#!/bin/bash

(
	cd src;
	find . -name "*.java" -exec sh -c "(echo -n ' '; echo -n {})" \; | sed 's;\./;;g' > ../files.lst;
	gcj -o ../regression --main=regression.Regression $(cat ../files.lst);
	gcj -o ../ic --main=ic.IC $(cat ../files.lst); 
)

