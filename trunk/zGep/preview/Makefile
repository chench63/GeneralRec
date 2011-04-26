all: gep.pdf
	start gep.pdf

gep.pdf: gep.dvi figure.s
	dvipdfmx gep

figure.s: figure.mp
	mp figure.mp
	touch figure.s

gep.dvi: gep.tex figure.s
	latex gep
	latex gep

clean:
	-ren figure.mp zuojie.mp
	-ren gep.tex zuojie.tex
	-del gep.* figure.*
	-ren zuojie.mp figure.mp
	-ren zuojie.tex gep.tex
